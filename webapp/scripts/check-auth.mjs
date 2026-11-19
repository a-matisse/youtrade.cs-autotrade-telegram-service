// Run with the local Postman simulation on :4921 and an installed Playwright module.
import {createServer} from 'node:http'
import {readFile} from 'node:fs/promises'
import {resolve, extname, sep} from 'node:path'
import {fileURLToPath} from 'node:url'
import {mkdir} from 'node:fs/promises'
import {createRequire} from 'node:module'
import assert from 'node:assert/strict'

const require = createRequire(import.meta.url)
const {chromium} = require(process.env.PLAYWRIGHT_MODULE || 'playwright')
const root = fileURLToPath(new URL('../dist', import.meta.url))
const backend = 'http://127.0.0.1:4921/backend'
const origin = 'http://127.0.0.1:4192'
const request = (path, init) => fetch(backend + path, init)
const state = async () => (await request('/__fixture__/state')).json()
const count = (calls, path) => calls.filter(call => call.path === path).length
const server = createServer(async (req, res) => {
  try {
    const path = new URL(req.url, origin).pathname
    if (path.startsWith('/api/') || path.startsWith('/__fixture__/')) {
      const headers = new Headers()
      for (const name of ['authorization', 'cookie', 'origin', 'content-type']) if (req.headers[name]) headers.set(name, req.headers[name])
      const chunks = []; for await (const chunk of req) chunks.push(chunk)
      const reply = await request(req.url, {method: req.method, headers, redirect: 'manual',
        body: chunks.length ? Buffer.concat(chunks) : undefined})
      res.writeHead(reply.status, Object.fromEntries(reply.headers))
      res.end(Buffer.from(await reply.arrayBuffer()))
      return
    }
    const file = resolve(root, path.startsWith('/assets/') || path === '/app-loader.js' ? '.' + path : 'app.html')
    if (!file.startsWith(root + sep)) throw new Error('Invalid fixture path')
    res.writeHead(200, {'Content-Type': {'.html': 'text/html', '.js': 'text/javascript', '.css': 'text/css', '.png': 'image/png'}[extname(file)]})
    res.end(await readFile(file))
  } catch {res.writeHead(502); res.end('Fixture connection failed')}
})
await new Promise(resolve => server.listen(4192, '127.0.0.1', resolve))
const browser = await chromium.launch({headless: true,
  ...(process.env.PLAYWRIGHT_EXECUTABLE_PATH ? {executablePath: process.env.PLAYWRIGHT_EXECUTABLE_PATH} : {})})
const errors = []
const contexts = []
async function newContext() {
  const context = await browser.newContext()
  contexts.push(context)
  await context.route('https://**', route => route.abort())
  await context.route(/^https:\/\/youtradecs\.xyz\/api\/web\/v1\/(overview(?:\/(deals|documents))?|steam\/currency)(\?.*)?$/, async route => {
    const url = new URL(route.request().url())
    const reply = await request(url.pathname + url.search)
    await route.fulfill({status: reply.status, contentType: 'application/json',
      headers: {'Access-Control-Allow-Origin': origin}, body: await reply.text()})
  })
  context.on('page', page => page.on('pageerror', error => errors.push(error.message)))
  return context
}
async function dashboard(page) {
  await page.waitForURL('**/dashboard')
  await page.getByRole('heading', {name: 'Вы вошли в Y.CS', exact: true}).waitFor()
}
async function navigate(page, path) {
  await page.evaluate(path => {history.pushState({}, '', path); window.dispatchEvent(new PopStateEvent('popstate'))}, path)
}
async function login(page) {
  await page.goto(origin + '/auth')
  await page.getByRole('button', {name: 'Войти через Telegram'}).click()
  await dashboard(page)
}
async function checkLayout(page, name) {
  await page.locator('#app-loader').waitFor({state: 'detached'})
  for (const width of [375, 1280]) {
    await page.setViewportSize({width, height: 900})
    assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), `${name} must fit at ${width}px`)
    if (process.env.AUTH_SCREENSHOT_DIR) {
      await mkdir(process.env.AUTH_SCREENSHOT_DIR, {recursive: true})
      await page.screenshot({path: resolve(process.env.AUTH_SCREENSHOT_DIR, `${name}-${width}.png`), fullPage: true})
    }
  }
}
try {
  await request('/__fixture__/reset')
  const context = await newContext()
  const page = await context.newPage()
  await page.goto(origin)
  await page.waitForTimeout(250)
  assert.equal(new URL(page.url()).pathname, '/')
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), 1)
  await page.goto(origin + '/auth')
  await page.getByRole('button', {name: 'Войти через Telegram'}).waitFor()
  await page.waitForTimeout(750)
  await checkLayout(page, 'login')
  await login(page)
  await checkLayout(page, 'cabinet')
  assert.equal(await page.locator('.auth-page-content>small').innerText(), 'Профиль #42')
  assert.equal((await state()).calls.some(call => call.path.startsWith('/api/web/v1/user/')), false,
    'the cabinet placeholder must not load trading data')
  assert.equal(await page.evaluate(() => sessionStorage.getItem('youtrade.jwt')), null)
  assert.equal(await page.evaluate(() => localStorage.getItem('youtrade.jwt')), null)
  assert.equal(await page.evaluate(() => document.cookie.includes('refresh')), false)
  const refreshCookies = (await context.cookies()).filter(cookie => cookie.name.includes('refresh'))
  assert.equal(refreshCookies.length, 1)
  assert.equal(refreshCookies[0].httpOnly, true)
  assert.equal(refreshCookies[0].sameSite, 'Strict')
  assert.equal(refreshCookies[0].path, '/api/web/v1/auth')
  let before = count((await state()).calls, '/api/web/v1/auth/refresh')
  await page.reload()
  await dashboard(page)
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), before + 1)
  const second = await context.newPage()
  before = count((await state()).calls, '/api/web/v1/auth/refresh')
  await second.goto(origin + '/dashboard')
  await dashboard(second)
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), before, 'a new tab must reuse the in-memory credentials of its peer')

  before = count((await state()).calls, '/api/web/v1/auth/refresh')
  await page.goto(origin + '/api/web/v1/auth/telegram')
  await dashboard(page)
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), before + 1,
    'the OAuth callback must consume its new cookie instead of reusing a previous login from another tab')

  // Expired access in concurrent tabs: exactly one rotation, then each GET succeeds.
  await request('/__fixture__/expire')
  before = count((await state()).calls, '/api/web/v1/auth/refresh')
  const acceptancesPath = '/api/web/v1/user/p2p/document-acceptances'
  await Promise.all([page.waitForResponse(response => response.url().endsWith(acceptancesPath) && response.status() === 200),
    second.waitForResponse(response => response.url().endsWith(acceptancesPath) && response.status() === 200),
    navigate(page, '/terms'), navigate(second, '/terms')])
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), before + 1)
  await Promise.all([navigate(page, '/dashboard'), navigate(second, '/dashboard')])
  await Promise.all([dashboard(page), dashboard(second)])

  // An expired credential must not cause an automatic replay of an authenticated write.
  await navigate(page, '/terms')
  await page.getByRole('button', {name: 'Принять условия', exact: true}).waitFor()
  await request('/__fixture__/expire')
  const writesBefore = (await state()).calls.filter(call => call.path === acceptancesPath && call.method === 'POST').length
  await page.getByRole('button', {name: 'Принять условия', exact: true}).click()
  await page.getByRole('alert').filter({hasText: 'Сессия обновлена. Повторите действие.'}).waitFor()
  assert.equal((await state()).calls.filter(call => call.path === acceptancesPath && call.method === 'POST').length, writesBefore + 1)
  await navigate(page, '/dashboard')
  await dashboard(page)

  // Expired access before logout is renewed once; logout revokes the browser session in both tabs.
  await request('/__fixture__/expire')
  before = count((await state()).calls, '/api/web/v1/auth/refresh')
  await page.getByRole('button', {name: 'Выйти', exact: true}).click()
  await Promise.all([page.waitForURL('**/auth'), second.waitForURL('**/auth')])
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), before + 1)
  assert.equal(count((await state()).calls, '/api/web/v1/auth/logout'), 2)
  await page.reload()
  await page.getByRole('button', {name: 'Войти через Telegram'}).waitFor()
  assert.equal(new URL(page.url()).pathname, '/auth')

  // New login is also recognized by an already-open guest tab.
  await login(page)
  await page.waitForTimeout(200)
  await dashboard(second)
  const otherDevice = await newContext()
  const other = await otherDevice.newPage()
  await login(other)

  // The backend rotates refresh but the reply is lost: the frontend must not send it again.
  await request('/__fixture__/mode?value=lost-response')
  await request('/__fixture__/expire')
  before = count((await state()).calls, '/api/web/v1/auth/refresh')
  await navigate(page, '/terms')
  await page.waitForTimeout(300)
  await navigate(page, '/dashboard')
  await page.waitForURL('**/auth')
  await page.waitForTimeout(350)
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), before + 1, 'ambiguous refresh must never be replayed')
  await request('/__fixture__/mode?value=ok')
  await Promise.all([other.waitForResponse(response => response.url().endsWith(acceptancesPath) && response.status() === 200),
    navigate(other, '/terms')])

  const failedContext = await newContext()
  const failed = await failedContext.newPage()
  await failed.goto(origin + '/auth')
  await failed.getByRole('button', {name: 'Войти через Telegram'}).waitFor()
  await request('/__fixture__/mode?value=unavailable')
  before = count((await state()).calls, '/api/web/v1/auth/refresh')
  await failed.getByRole('button', {name: 'Войти через Telegram'}).click()
  await failed.getByRole('alert').filter({hasText: 'Не удалось восстановить вход'}).waitFor()
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), before + 1)
  await request('/__fixture__/mode?value=blocked')
  await failed.getByRole('button', {name: 'Войти через Telegram'}).click()
  await failed.getByRole('alert').filter({hasText: 'Вход недоступен'}).waitFor()
  assert.deepEqual(errors, [])
  const calls = (await state()).calls
  assert.ok(!calls.some(call => call.path.startsWith('/api/telegram/') || call.path === '/auth/telegram'))
  console.log(JSON.stringify({checks: 'passed', cases: ['guest', 'OIDC callback', 'memory-only access', 'HttpOnly cookie',
    'reload', 'parallel tabs', 'new callback with an existing peer', 'expired access', 'no automatic write replay', 'server logout', 'new login in peer', 'lost refresh reply', 'independent devices', '503/403 messages', 'mobile layout'],
    refreshRequests: count(calls, '/api/web/v1/auth/refresh'), pageErrors: errors}))
} finally {
  for (const context of contexts) await context.close()
  await browser.close()
  server.closeAllConnections()
  await new Promise(resolve => server.close(resolve))
}

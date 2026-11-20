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
const docsRoot = fileURLToPath(new URL('../../user-docs/.vitepress/dist', import.meta.url))
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
    const docs = path.startsWith('/__docs__/')
    const staticRoot = docs ? docsRoot : root
    const docsPath = docs ? path.slice('/__docs__'.length) : ''
    const file = resolve(staticRoot, docs ? '.' + (docsPath === '/' ? '/index.html' : extname(docsPath) ? docsPath : docsPath + '.html')
      : path.startsWith('/assets/') || path === '/app-loader.js' ? '.' + path : 'app.html')
    if (!file.startsWith(staticRoot + sep)) throw new Error('Invalid fixture path')
    res.writeHead(200, {'Content-Type': {'.html': 'text/html', '.js': 'text/javascript', '.css': 'text/css', '.png': 'image/png', '.svg': 'image/svg+xml', '.json': 'application/json', '.woff2': 'font/woff2'}[extname(file)]})
    res.end(await readFile(file))
  } catch {res.writeHead(502); res.end('Fixture connection failed')}
})
await new Promise(resolve => server.listen(4192, '127.0.0.1', resolve))
const browser = await chromium.launch({headless: true,
  ...(process.env.PLAYWRIGHT_EXECUTABLE_PATH ? {executablePath: process.env.PLAYWRIGHT_EXECUTABLE_PATH} : {})})
const errors = []
const contexts = []
async function newContext(options = {}) {
  const context = await browser.newContext(options)
  contexts.push(context)
  await context.route('https://**', route => route.abort())
  await context.route('https://youtradecs.xyz/__fixture__/**', async route => {
    if (route.request().url().endsWith('/avatar.svg')) return route.fulfill({contentType: 'image/svg+xml',
      body: '<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40"><rect width="40" height="40" fill="#b8323a"/><circle cx="20" cy="15" r="6" fill="white"/></svg>'})
    return route.fulfill({status: 404, body: ''})
  })
  await context.route('https://docs.youtradecs.xyz/**', async route => {
    const url = new URL(route.request().url())
    const reply = url.pathname === '/api/web/v1/steam/currency' ? await request(url.pathname)
      : await fetch(origin + '/__docs__' + url.pathname + url.search)
    await route.fulfill({status: reply.status, headers: Object.fromEntries(reply.headers), body: Buffer.from(await reply.arrayBuffer())})
  })
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
async function openProfile(page) {
  await page.mouse.move(0, 200)
  await page.locator('.site-profile-trigger').hover()
  await page.locator('.site-profile[open]').waitFor()
}
async function checkControlStyles(page) {
  const sample = locator => locator.evaluate(element => {
    const style = getComputedStyle(element)
    const surface = getComputedStyle(document.querySelector('.site-header'))
    return {background: style.backgroundColor, color: style.color, border: style.borderTopWidth,
      radius: style.borderTopLeftRadius, width: element.getBoundingClientRect().width,
      height: element.getBoundingClientRect().height, outline: style.outlineWidth,
      surface: surface.backgroundColor}
  })
  const contrast = (foreground, background) => {
    const luminance = color => color.match(/[\d.]+/g).slice(0, 3).map(Number)
      .map(value => value / 255).map(value => value <= .04045 ? value / 12.92 : ((value + .055) / 1.055) ** 2.4)
      .reduce((sum, value, index) => sum + value * [.2126, .7152, .0722][index], 0)
    const a = luminance(foreground), b = luminance(background)
    return (Math.max(a, b) + .05) / (Math.min(a, b) + .05)
  }
  await page.setViewportSize({width: 1280, height: 900})
  for (const theme of ['dark', 'light']) {
    if (await page.evaluate(() => document.documentElement.dataset.siteTheme) !== theme) await page.locator('.site-theme-toggle').click()
    await page.mouse.move(0, 200)
    await page.waitForTimeout(180)
    const icon = page.locator('.site-theme-toggle')
    const idle = await sample(icon)
    assert.equal(idle.border, '0px')
    assert.equal(idle.background, 'rgba(0, 0, 0, 0)')
    assert.equal(idle.radius, '10px')
    assert.ok(idle.width >= 34 && idle.height >= 34)
    assert.ok(contrast(idle.color, idle.surface) >= 3)
    const login = page.locator('.site-account-link')
    const loginStyle = await sample(login)
    assert.ok(contrast(loginStyle.color, loginStyle.background) >= 4.5)
    await icon.hover()
    await page.waitForTimeout(180)
    const hover = await sample(icon)
    assert.notEqual(hover.background, idle.background)
    assert.ok(contrast(hover.color, hover.background) >= 3)
    if (process.env.AUTH_SCREENSHOT_DIR) await page.screenshot({path: resolve(process.env.AUTH_SCREENSHOT_DIR, `controls-hover-${theme}.png`), clip: {x: 0, y: 0, width: 1280, height: 90}})
    await page.mouse.down()
    await page.waitForTimeout(180)
    assert.notEqual((await sample(icon)).background, hover.background)
    await page.mouse.move(0, 200)
    await page.mouse.up()
    await icon.focus()
    await page.keyboard.press('Tab')
    await page.keyboard.press('Shift+Tab')
    assert.equal((await sample(icon)).outline, '2px')
    if (process.env.AUTH_SCREENSHOT_DIR) await page.screenshot({path: resolve(process.env.AUTH_SCREENSHOT_DIR, `controls-${theme}.png`), clip: {x: 0, y: 0, width: 1280, height: 90}})
    await page.keyboard.press('Tab')
  }
}
async function checkLayout(page, name) {
  await page.locator('#app-loader').waitFor({state: 'detached'})
  await page.locator('.site-header .ticker-item em').first().waitFor()
  for (const width of [320, 375, 768, 960, 1280]) {
    await page.setViewportSize({width, height: 900})
    assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), `${name} must fit at ${width}px`)
    const header = await page.evaluate(() => {
      const root = document.querySelector('.site-header').getBoundingClientRect()
      const ticker = document.querySelector('.currency-ticker').getBoundingClientRect()
      const nav = document.querySelector('.site-header-inner').getBoundingClientRect()
      return {height: root.height, top: root.top, tickerTop: ticker.top, tickerBottom: ticker.bottom,
        navTop: nav.top, duration: getComputedStyle(document.querySelector('.ticker-track')).animationDuration}
    })
    assert.ok(header.height <= (width <= 620 ? 61 : 65), `${name} header must remain slim at ${width}px`)
    assert.equal(header.tickerTop, header.top)
    assert.equal(header.tickerBottom, header.navTop)
    assert.equal(header.duration, '60s')
    const formButton = page.locator('.auth-page-login')
    if (await formButton.count()) assert.ok(await formButton.evaluate(element => element.getBoundingClientRect().height >= 44), `login form action must remain easy to tap at ${width}px`)
    if (process.env.AUTH_SCREENSHOT_DIR && [375, 1280].includes(width)) {
      await mkdir(process.env.AUTH_SCREENSHOT_DIR, {recursive: true})
      await page.screenshot({path: resolve(process.env.AUTH_SCREENSHOT_DIR, `${name}-${width}.png`), fullPage: true})
    }
  }
  await page.evaluate(() => window.scrollTo(0, 500))
  assert.equal(Math.round(await page.locator('.site-header').evaluate(element => element.getBoundingClientRect().top)), 0)
  await page.evaluate(() => window.scrollTo(0, 0))
}
try {
  await request('/__fixture__/reset')
  const restoringContext = await newContext()
  await restoringContext.route('**/api/web/v1/auth/refresh', async route => {
    await new Promise(resolve => setTimeout(resolve, 1500))
    await route.continue()
  })
  const restoring = await restoringContext.newPage()
  await restoring.goto(origin + '/dashboard')
  await restoring.locator('#app-loader').waitFor({state: 'detached'})
  await restoring.locator('.site-header').waitFor({state: 'visible'})
  await restoring.locator('.site-section-wait').waitFor({state: 'visible'})
  assert.equal(await restoring.locator('.site-header .site-session-status').count(), 1)
  await restoring.waitForURL('**/auth')
  await restoringContext.close()
  contexts.pop()
  await request('/__fixture__/reset')
  const context = await newContext()
  const page = await context.newPage()
  await page.goto(origin)
  await page.waitForTimeout(250)
  assert.equal(new URL(page.url()).pathname, '/')
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), 1)
  await page.locator('#app-loader').waitFor({state: 'detached'})
  await page.evaluate(() => {window.__siteDocument = 'preserved'; document.querySelector('.site-header').dataset.preserved = 'yes'})
  await page.getByRole('link', {name: 'Начать работу'}).click()
  await page.waitForURL('**/auth')
  assert.equal(await page.evaluate(() => window.__siteDocument), 'preserved')
  assert.equal(await page.locator('.site-header').getAttribute('data-preserved'), 'yes')
  assert.equal(await page.locator('#app-loader').count(), 0)
  await page.getByRole('button', {name: 'Включить светлую тему'}).click()
  await page.locator('.site-header-inner>.site-nav').getByRole('link', {name: 'Документы', exact: true}).click()
  await page.waitForURL('**/terms')
  assert.equal(await page.evaluate(() => document.documentElement.dataset.siteTheme), 'light')
  assert.equal(await page.locator('.site-header').getAttribute('data-preserved'), 'yes')
  const learningHref = await page.locator('.site-header-inner>.site-nav').getByRole('link', {name: 'Обучение', exact: true}).getAttribute('href')
  assert.equal(new URL(learningHref).searchParams.get('theme'), 'light')
  await page.getByRole('link', {name: 'Y.CS — на главную', exact: true}).click()
  await page.getByRole('link', {name: 'Начать работу'}).click()
  await page.getByRole('button', {name: 'Войти через Telegram'}).waitFor()
  await page.waitForTimeout(750)
  await checkLayout(page, 'login')
  await checkControlStyles(page)
  const guestNav = await page.locator('.site-header-inner>.site-nav').boundingBox()
  const guestBrand = await page.locator('.site-brand').boundingBox()
  assert.ok(guestNav.x - guestBrand.x - guestBrand.width <= 24, 'navigation must remain beside the brand')
  await login(page)
  await checkLayout(page, 'cabinet')
  const userNav = await page.locator('.site-header-inner>.site-nav').boundingBox()
  assert.equal(userNav.x, guestNav.x, 'signing in must not move the site navigation')
  await openProfile(page)
  await page.getByText('Привет, Пользователь', {exact: true}).waitFor()
  assert.equal(await page.locator('.site-avatar-placeholder').count(), 1)
  assert.ok(!await page.locator('.site-profile-trigger').evaluate(element => getComputedStyle(element, '::after').content.includes('⌄')))
  await page.keyboard.press('Escape')
  assert.equal(await page.locator('.site-profile').getAttribute('open'), null)
  await page.locator('.site-theme-toggle').focus()
  await page.keyboard.press('Tab')
  await page.getByRole('link', {name: 'Личный кабинет', exact: true}).waitFor()
  await page.keyboard.press('Escape')
  await page.locator('h1').click()

  await request('/__fixture__/profile?value=avatar')
  await page.evaluate(() => window.dispatchEvent(new Event('focus')))
  await page.locator('.site-avatar').waitFor()
  await page.waitForFunction(() => document.querySelector('.site-avatar')?.naturalWidth > 0)
  await openProfile(page)
  await page.getByText('Привет, Alex', {exact: true}).waitFor()
  await page.getByRole('link', {name: 'Личный кабинет', exact: true}).hover()
  assert.equal(await page.locator('.site-profile').getAttribute('open'), '')
  await page.keyboard.press('Escape')
  await request('/__fixture__/profile?value=broken')
  const profilePath = '/api/web/v1/user/profile'
  const brokenBefore = count((await state()).calls, profilePath)
  await page.evaluate(() => window.dispatchEvent(new Event('focus')))
  await page.locator('.site-avatar-placeholder').waitFor()
  await page.waitForTimeout(350)
  assert.equal(count((await state()).calls, profilePath), brokenBefore + 2,
    'a broken image must cause one profile reload without an infinite retry loop')

  // Signed image URLs are used unchanged; the profile owns nickname and avatar separately from auth/me.
  for (const mode of ['uploaded', 'renew', 'expiry']) {
    await request('/__fixture__/profile?value=' + mode)
    const before = count((await state()).calls, profilePath)
    await page.evaluate(() => window.dispatchEvent(new Event('focus')))
    await page.waitForFunction(() => document.querySelector('.site-avatar')?.naturalWidth > 0)
    await page.waitForTimeout(350)
    assert.equal(count((await state()).calls, profilePath), before + (mode === 'uploaded' ? 1 : 2))
    const signedUrl = await page.locator('.site-avatar').getAttribute('src')
    assert.ok(signedUrl.startsWith('/api/web/v1/user/profile/avatar/file/fixture-id?signature=valid&generation='))
  }

  for (const mode of ['missing', 'unavailable']) {
    await request('/__fixture__/profile?value=' + mode)
    await page.evaluate(() => window.dispatchEvent(new Event('focus')))
    await openProfile(page)
    await page.locator('.site-profile-error').filter({hasText: mode === 'missing' ? 'войдите через Telegram ещё раз' : 'Не удалось загрузить профиль'}).waitFor()
    assert.equal(new URL(page.url()).pathname, '/dashboard', 'profile availability is independent of authentication')
    if (mode === 'missing') await page.getByRole('button', {name: 'Войти заново', exact: true}).waitFor()
    await page.keyboard.press('Escape')
  }
  await request('/__fixture__/profile?value=nickname')
  await page.evaluate(() => window.dispatchEvent(new Event('focus')))
  await openProfile(page)
  await page.getByText('Привет, alex', {exact: true}).waitFor()
  await page.keyboard.press('Escape')
  await request('/__fixture__/profile?value=legacy')
  await page.evaluate(() => window.dispatchEvent(new Event('focus')))
  assert.equal(await page.locator('.auth-page-content>small').innerText(), 'Профиль #42')
  assert.equal((await state()).calls.some(call => call.path.startsWith('/api/web/v1/user/') && !call.path.startsWith(profilePath)), false,
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
  await openProfile(page)
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
  await failed.waitForFunction(() => !document.querySelector('.auth-page-login').disabled)
  await request('/__fixture__/mode?value=unavailable')
  before = count((await state()).calls, '/api/web/v1/auth/refresh')
  await failed.getByRole('button', {name: 'Войти через Telegram'}).click()
  await failed.getByRole('alert').filter({hasText: 'Не удалось восстановить вход'}).waitFor()
  assert.equal(count((await state()).calls, '/api/web/v1/auth/refresh'), before + 1)
  await request('/__fixture__/mode?value=blocked')
  await failed.getByRole('button', {name: 'Войти через Telegram'}).click()
  await failed.getByRole('alert').filter({hasText: 'Вход недоступен'}).waitFor()

  const docs = await otherDevice.newPage()
  await docs.goto(learningHref)
  await docs.waitForFunction(() => document.documentElement.dataset.siteTheme === 'light')
  assert.equal(await docs.locator('.site-header').count(), 1)
  assert.equal(await docs.locator('.VPNav').isVisible(), false)
  await checkLayout(docs, 'learning')
  assert.equal((await docs.locator('.site-header-inner>.site-nav').boundingBox()).x, userNav.x,
    'the learning and main headers must use the same navigation alignment')
  await docs.evaluate(() => {document.querySelector('.site-header').dataset.preserved = 'yes'})
  await docs.locator('a[href="/start/checklist"]').first().click()
  await docs.waitForURL('**/start/checklist')
  assert.equal(await docs.locator('.site-header').getAttribute('data-preserved'), 'yes')
  await checkLayout(docs, 'learning-article')
  await docs.setViewportSize({width: 375, height: 900})
  await docs.getByRole('button', {name: 'Включить тёмную тему'}).click()
  await docs.locator('.site-mobile-nav>summary').click()
  await docs.locator('.site-mobile-nav').getByRole('link', {name: 'Документы', exact: true}).waitFor()
  await docs.keyboard.press('Escape')
  assert.equal(await docs.locator('.site-mobile-nav').getAttribute('open'), null)
  const cabinetHref = await docs.getByRole('link', {name: 'Кабинет', exact: true}).getAttribute('href')
  assert.equal(new URL(cabinetHref).searchParams.get('theme'), 'dark')
  await docs.goto(cabinetHref.replace('https://youtradecs.xyz', origin))
  await dashboard(docs)
  assert.equal(await docs.evaluate(() => document.documentElement.dataset.siteTheme), 'dark')

  await request('/__fixture__/mode?value=ok')
  const touchContext = await newContext({hasTouch: true, isMobile: true, viewport: {width: 375, height: 812}})
  const touch = await touchContext.newPage()
  await login(touch)
  await touch.locator('.site-profile-trigger').tap()
  await touch.getByRole('link', {name: 'Личный кабинет', exact: true}).waitFor()
  await touch.locator('h1').tap()
  assert.equal(await touch.locator('.site-profile').getAttribute('open'), null)
  assert.deepEqual(errors, [])
  const calls = (await state()).calls
  assert.ok(!calls.some(call => call.path.startsWith('/api/telegram/') || call.path === '/auth/telegram'))
  assert.ok(!calls.some(call => call.path === '/api/web/v1/auth/session'))
  console.log(JSON.stringify({checks: 'passed', cases: ['guest', 'OIDC callback', 'memory-only access', 'HttpOnly cookie',
    'button hover/pressed/focus', 'button contrast in both themes', 'stable header alignment', 'separate authenticated profile', 'signed avatar URL', 'avatar error and expiry renewal', 'missing profile without logout', 'nickname fallback', 'hover and keyboard profile menu', 'touch profile menu',
    'header visible during session restoration', 'SPA shell persists', 'shared theme', 'learning header', 'cross-domain theme handoff', 'reload', 'parallel tabs', 'new callback with an existing peer', 'expired access', 'no automatic write replay', 'server logout', 'new login in peer', 'lost refresh reply', 'independent devices', '503/403 messages', 'mobile layout'],
    refreshRequests: count(calls, '/api/web/v1/auth/refresh'), pageErrors: errors}))
} finally {
  for (const context of contexts) await context.close()
  await browser.close()
  server.closeAllConnections()
  await new Promise(resolve => server.close(resolve))
}

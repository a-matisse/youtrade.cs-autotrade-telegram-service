// Run after npm run build. Exercises real HTTP caching without request interception.
import {createServer} from 'node:http'
import {readFile} from 'node:fs/promises'
import {resolve, extname, sep} from 'node:path'
import {fileURLToPath} from 'node:url'
import {createRequire} from 'node:module'
import assert from 'node:assert/strict'

const require = createRequire(import.meta.url)
const {chromium} = require(process.env.PLAYWRIGHT_MODULE || 'playwright')
const root = fileURLToPath(new URL('../dist', import.meta.url))
const manifest = JSON.parse(await readFile(resolve(root, '.vite/manifest.json'), 'utf8'))
const entry = '/' + manifest['index.html'].file
const origin = 'http://127.0.0.1:4194'
let bundleRequests = 0
let documentRequests = 0
const server = createServer(async (req, res) => {
  try {
    const path = new URL(req.url, origin).pathname
    if (path.startsWith('/api/')) {res.writeHead(401); res.end('{}'); return}
    const asset = path.startsWith('/assets/') || path === '/app-loader.js'
    const file = resolve(root, asset ? '.' + path : path === '/' ? 'index.html' : 'app.html')
    if (!file.startsWith(root + sep)) throw new Error('Invalid preview path')
    if (path === entry) {bundleRequests++; await new Promise(resolve => setTimeout(resolve, 900))}
    if (req.headers['sec-fetch-dest'] === 'document') documentRequests++
    res.writeHead(200, {'Content-Type': {'.html': 'text/html', '.js': 'text/javascript', '.css': 'text/css', '.png': 'image/png', '.svg': 'image/svg+xml'}[extname(file)],
      'Cache-Control': path.startsWith('/assets/') ? 'public, max-age=31536000, immutable' : 'no-store'})
    res.end(await readFile(file))
  } catch {res.writeHead(404); res.end()}
})
await new Promise(resolve => server.listen(4194, '127.0.0.1', resolve))
const browser = await chromium.launch({headless: true,
  ...(process.env.PLAYWRIGHT_EXECUTABLE_PATH ? {executablePath: process.env.PLAYWRIGHT_EXECUTABLE_PATH} : {})})
try {
  const context = await browser.newContext()
  const page = await context.newPage()
  const cdp = await context.newCDPSession(page)
  await cdp.send('Network.enable')
  await cdp.send('Network.setBlockedURLs', {urls: ['https://*']})
  await page.addInitScript(() => {
    window.bootEvents = []
    new MutationObserver(records => {
      for (const record of records) {
        if (record.target === document.documentElement && document.documentElement.classList.contains('app-loading')) window.bootEvents.push('loading')
        if (record.target.id === 'app-loader' && record.target.dataset.state === 'ready') window.bootEvents.push('ready')
      }
    }).observe(document, {subtree: true, attributes: true, attributeFilter: ['class', 'data-state']})
  })
  await page.goto(origin, {waitUntil: 'commit'})
  await page.waitForFunction(() => document.documentElement.classList.contains('app-loading'))
  assert.equal(await page.locator('#root').getAttribute('inert'), '')
  await page.locator('#app-loader').waitFor({state: 'detached'})
  assert.ok(await page.evaluate(() => window.bootEvents.includes('loading') && window.bootEvents.includes('ready')))
  assert.equal(bundleRequests, 1)
  await page.reload()
  await page.locator('#app-loader').waitFor({state: 'detached'})
  assert.equal(bundleRequests, 1, 'warm reload must reuse the cached bundle')
  assert.equal(await page.evaluate(() => window.bootEvents.includes('loading')), false, 'warm reload must skip the startup screen')
  const before = documentRequests
  await page.getByRole('link', {name: 'Начать работу'}).click()
  await page.waitForURL('**/auth')
  await page.locator('.site-header-inner>.site-nav').getByRole('link', {name: 'Документы', exact: true}).click()
  await page.waitForURL('**/terms')
  await page.evaluate(() => {history.pushState({}, '', '/missing'); dispatchEvent(new PopStateEvent('popstate'))})
  await page.locator('.error-page-in-site').waitFor()
  await page.getByRole('link', {name: 'На главную', exact: true}).click()
  await page.waitForURL(origin + '/')
  assert.equal(documentRequests, before)
  assert.equal(await page.locator('#app-loader').count(), 0)
  console.log(JSON.stringify({checks: 'passed', cases: ['cold website startup', 'ready transition', 'cached reload', 'SPA navigation without startup screen']}))
} finally {
  await browser.close()
  server.closeAllConnections()
  await new Promise(resolve => server.close(resolve))
}

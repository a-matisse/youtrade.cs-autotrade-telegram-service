import {mkdir, readFile, writeFile} from 'node:fs/promises'
import {fileURLToPath} from 'node:url'
import {dirname, join} from 'node:path'
import {createElement, Fragment} from 'react'
import {renderToStaticMarkup, renderToString} from 'react-dom/server'
import {createServer} from 'vite'

const root = dirname(dirname(fileURLToPath(import.meta.url)))
const output = join(root, 'dist')
const vite = await createServer({
  root,
  server: {middlewareMode: true},
  appType: 'custom',
  logLevel: 'error',
  ssr: {noExternal: ['react-router-dom', 'react-router'], resolve: {conditions: ['module', 'module-sync', 'node', 'import']}},
})

try {
  const {LandingPage} = await vite.ssrLoadModule('/src/pages/LandingPage.tsx')
  const {SiteHeader} = await vite.ssrLoadModule('/src/components/layout/SiteHeader.tsx')
  const {SiteFooter} = await vite.ssrLoadModule('/src/components/layout/SiteFooter.tsx')
  const {SiteThemeProvider} = await vite.ssrLoadModule('/src/components/layout/SiteTheme.tsx')
  const manifest = JSON.parse(await readFile(join(output, '.vite/manifest.json'), 'utf8'))
  const logo = manifest['src/assets/brand/youtrade-mark-transparent.png']?.file
  if (!logo) throw new Error('Built logo is missing from the Vite manifest')
  const icons = manifest['../shared/site/icons.svg']?.file
  if (!icons) throw new Error('Built site icons are missing from the Vite manifest')
  const {default: sourceIcons} = await vite.ssrLoadModule(join(root, '../shared/site/icons.svg') + '?no-inline')
  const renderPage = node => renderToString(node)
    .replaceAll('/src/assets/brand/youtrade-mark-transparent.png', `/${logo}`)
    .replaceAll(sourceIcons, `/${icons}`)
  const entry = manifest['index.html']?.file
  if (!entry) throw new Error('Built entry is missing from the Vite manifest')

  const markup = renderPage(createElement(SiteThemeProvider, null,
    createElement(SiteHeader), createElement(LandingPage), createElement(SiteFooter)))
  if (!markup.includes('<h1>') || markup.includes('/src/') || markup.includes('/@fs/'))
    throw new Error('The public landing page was not rendered completely')

  const path = join(output, 'index.html')
  const html = (await readFile(path, 'utf8'))
    .replace('data-entry="/src/main.tsx"', `data-entry="/${entry}"`)
    .replaceAll(`content="/${logo}"`, `content="https://youtradecs.xyz/${logo}"`)
  const placeholder = '<div id="root"></div>'
  if (!html.includes(placeholder)) throw new Error('The landing page root is missing')
  await writeFile(join(output, 'app.html'), html)

  const termsTitle = 'Оферты и условия Y.CS — документы сервиса'
  const termsDescription = 'Условия использования Y.CS, оплаты услуг, возврата средств и обработки персональных данных. Актуальные редакции документов сервиса.'
  const termsHtml = html
    .replace(/<title>[^<]*<\/title>/, `<title>${termsTitle}</title>`)
    .replace(/<meta name="description" content="[^"]*" \/>/, `<meta name="description" content="${termsDescription}" />`)
    .replace(/<link rel="canonical" href="[^"]*" \/>/, '<link rel="canonical" href="https://youtradecs.xyz/terms" />')
    .replace(/<meta property="og:title" content="[^"]*" \/>/, `<meta property="og:title" content="${termsTitle}" />`)
    .replace(/<meta property="og:description" content="[^"]*" \/>/, `<meta property="og:description" content="${termsDescription}" />`)
    .replace(/<meta property="og:url" content="[^"]*" \/>/, '<meta property="og:url" content="https://youtradecs.xyz/terms" />')
    .replace(/<meta name="twitter:title" content="[^"]*" \/>/, `<meta name="twitter:title" content="${termsTitle}" />`)
    .replace(/<meta name="twitter:description" content="[^"]*" \/>/, `<meta name="twitter:description" content="${termsDescription}" />`)
    .replace(placeholder, `<div id="root">${renderPage(createElement(SiteHeader, {pathname: '/terms'}))}<main class="offers-page"><h1>Оферты и условия Y.CS</h1><p>Актуальные документы сервиса загружаются на этой странице.</p></main>${renderPage(createElement(SiteFooter))}</div>`)
  if (!termsHtml.includes('rel="canonical" href="https://youtradecs.xyz/terms"'))
    throw new Error('The terms page is missing its canonical URL')
  await writeFile(join(output, 'terms.html'), termsHtml)

  const {ErrorPage} = await vite.ssrLoadModule('/src/pages/ErrorPage.tsx')
  const errorStyles = await readFile(join(root, 'src/pages/ErrorPage.css'), 'utf8')
  const sharedStyles = await Promise.all(['buttons', 'currency', 'header', 'footer'].map(name =>
    readFile(join(root, `../shared/site/${name}.css`), 'utf8')))
  const shellStyles = sharedStyles.join('\n').replace(/^@import.*$/gm, '')
  const errorLogo = `data:image/png;base64,${(await readFile(join(root, 'public/favicon-64.png'))).toString('base64')}`
  const embeddedIcons = (await readFile(join(root, '../shared/site/icons.svg'), 'utf8'))
    .replace('<svg ', '<svg aria-hidden="true" style="display:none" ')
  const errors = join(output, 'errors')
  await mkdir(errors, {recursive: true})
  for (const status of [400, 401, 403, 404, 405, 408, 410, 413, 414, 415, 422, 429, 500, 501, 502, 503, 504]) {
    const errorMarkup = renderToStaticMarkup(createElement(Fragment, null,
      createElement(SiteHeader, {standalone: true}), createElement(ErrorPage, {status}), createElement(SiteFooter)))
      .replaceAll('/src/assets/brand/youtrade-mark-transparent.png', errorLogo)
      .replaceAll(`${sourceIcons}#`, '#')
    const errorHtml = `<!doctype html>
<html lang="ru"><head>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta name="robots" content="noindex, nofollow"/>
<meta name="theme-color" content="#111412"/>
<meta http-equiv="Content-Security-Policy" content="default-src 'none'; img-src data:; style-src 'unsafe-inline'; base-uri 'none'; form-action 'none'"/>
<title>Ошибка ${status} — Y.CS</title>
<style>html,body{margin:0;min-width:320px}body{font-family:var(--site-header-font)}${shellStyles}${errorStyles}</style>
</head><body>${embeddedIcons}${errorMarkup}</body></html>`
    await writeFile(join(errors, `${status}.html`), errorHtml)
  }

  const website = JSON.stringify({
    '@context': 'https://schema.org',
    '@type': 'WebSite',
    name: 'Y.CS',
    url: 'https://youtradecs.xyz/',
  })
  const landingHtml = html
    .replace('</head>', `  <script type="application/ld+json">${website}</script>\n  </head>`)
    .replace(placeholder, `<div id="root">${markup}</div>`)
  await writeFile(path, landingHtml)
} finally {
  await vite.close()
}

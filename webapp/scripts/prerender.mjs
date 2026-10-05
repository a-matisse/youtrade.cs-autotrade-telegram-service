import {readFile, writeFile} from 'node:fs/promises'
import {fileURLToPath} from 'node:url'
import {dirname, join} from 'node:path'
import {createElement} from 'react'
import {renderToString} from 'react-dom/server'
import {createServer} from 'vite'

const root = dirname(dirname(fileURLToPath(import.meta.url)))
const output = join(root, 'dist')
const vite = await createServer({
  root,
  server: {middlewareMode: true},
  appType: 'custom',
  logLevel: 'error',
})

try {
  const {LandingPage} = await vite.ssrLoadModule('/src/pages/LandingPage.tsx')
  const manifest = JSON.parse(await readFile(join(output, '.vite/manifest.json'), 'utf8'))
  const logo = manifest['src/assets/brand/youtrade-mark-transparent.png']?.file
  if (!logo) throw new Error('Built logo is missing from the Vite manifest')

  const markup = renderToString(createElement(LandingPage))
    .replaceAll('/src/assets/brand/youtrade-mark-transparent.png', `/${logo}`)
  if (!markup.includes('<h1>') || markup.includes('/src/'))
    throw new Error('The public landing page was not rendered completely')

  const path = join(output, 'index.html')
  const html = await readFile(path, 'utf8')
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
    .replace(placeholder, '<div id="root"><main class="offers-page"><h1>Оферты и условия Y.CS</h1><p>Актуальные документы сервиса загружаются на этой странице.</p></main></div>')
  if (!termsHtml.includes('rel="canonical" href="https://youtradecs.xyz/terms"'))
    throw new Error('The terms page is missing its canonical URL')
  await writeFile(join(output, 'terms.html'), termsHtml)

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

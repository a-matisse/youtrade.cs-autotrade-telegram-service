import { defineConfig } from 'vitepress'

export default defineConfig({
  lang: 'ru-RU',
  title: 'Y.CS — Документация',
  description: 'Пошаговый запуск автоматической торговли предметами CS2',
  cleanUrls: true,
  lastUpdated: true,
  sitemap: { hostname: 'https://docs.youtradecs.xyz' },
  appearance: {initialValue: 'dark', storageKey: 'ycs-theme'},
  transformPageData(pageData) {
    const path = pageData.relativePath.replace(/index\.md$/, '').replace(/\.md$/, '')
    pageData.frontmatter.head ??= []
    pageData.frontmatter.head.push(['link', {
      rel: 'canonical',
      href: `https://docs.youtradecs.xyz/${path}`
    }])
  },
  head: [
    ['script', {}, `try{const u=new URL(location.href);const t=u.searchParams.get('theme');if(t==='dark'||t==='light'){localStorage.setItem('ycs-theme',t);u.searchParams.delete('theme');history.replaceState(history.state,'',u)}document.documentElement.dataset.siteTheme=localStorage.getItem('ycs-theme')==='light'?'light':'dark'}catch{}`],
    ['link', { rel: 'icon', type: 'image/png', href: '/favicon.png' }],
    ['meta', { name: 'theme-color', content: '#111412' }]
  ],
  themeConfig: {
    logo: '/logo.png',
    siteTitle: 'Y.CS / DOCS',
    outline: { label: 'На этой странице', level: [2, 3] },
    lastUpdated: { text: 'Обновлено' },
    docFooter: { prev: 'Назад', next: 'Дальше' },
    returnToTopLabel: 'Наверх',
    sidebarMenuLabel: 'Разделы',
    darkModeSwitchLabel: 'Тема',
    sidebar: [
      {
        text: 'Начало',
        items: [
          { text: 'Что делает Y.CS', link: '/start/what-is-ycs' },
          { text: 'Чек-лист запуска', link: '/start/checklist' },
          { text: 'Куда вы переводите деньги', link: '/start/money' }
        ]
      },
      {
        text: 'Подготовка CSFloat',
        items: [
          { text: 'Аккаунт и верификация', link: '/csfloat/account' },
          { text: 'Пополнение CSFloat', link: '/csfloat/funding' },
          { text: 'Ключ покупки CSFloat', link: '/csfloat/keys' }
        ]
      },
      {
        text: 'Подключение к Y.CS',
        items: [
          { text: 'Подключить покупку', link: '/connect/account' },
          { text: 'Подключить автопродажу', link: '/connect/autosell' },
          { text: 'Подключить Y.CS Worker™', link: '/connect/worker' },
          { text: 'QuickConfig™', link: '/connect/quickconfig' },
          { text: 'Получить доступ к Y.CS Bargain™', link: '/connect/bargain' },
          { text: 'Запустить торговлю', link: '/connect/launch' }
        ]
      },
      {
        text: 'После запуска',
        items: [
          { text: 'Ожидание и история', link: '/observe/history' },
          { text: 'Задержки Steam', link: '/observe/trade-lock' },
          { text: 'Если что-то не работает', link: '/help/troubleshooting' },
          { text: 'Безопасность', link: '/help/security' }
        ]
      }
    ],
    socialLinks: [
      { icon: 'telegram', link: 'https://t.me/youtradecs_bot' }
    ],
    footer: {
      message: '<a href="/faq">FAQ — вопросы и ответы →</a><br>Y.CS не связан с Valve Corporation. Торговля связана с рыночным риском.',
      copyright: 'YouTrade.CS'
    }
  }
})

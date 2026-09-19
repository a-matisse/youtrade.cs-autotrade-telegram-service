import {useEffect, useMemo, useRef, useState} from 'react'
import type {PointerEvent as ReactPointerEvent} from 'react'
import '../landing.css'
import {landingApi} from '../api/services'
import type {LandingDailyStatistics, LandingDeal, LandingOverview, SteamCurrencyRate} from '../api/types'
import logo from '../assets/brand/youtrade-mark-transparent.png'

const botUrl = 'https://t.me/youtradecs_bot'
const supportUrl = 'https://t.me/youtradecs_sup'

const agentFeePercent = Number(import.meta.env.VITE_AGENT_FEE_PERCENT || 3)
const marketFeePercent = Number(import.meta.env.VITE_MARKET_FEE_PERCENT || 5)
const agentFeeRate = agentFeePercent / 100
const marketFeeRate = marketFeePercent / 100

const money = new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD', maximumFractionDigits: 0})
const exactMoney = new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD', minimumFractionDigits: 2})
const number = new Intl.NumberFormat('ru-RU', {maximumFractionDigits: 0})
const marketNames: Record<string, string> = {
  CSFLOAT: 'CSFloat',
  MARKET_CSGO: 'Market.CS',
  LIS_SKINS: 'LIS-Skins',
  STEAM: 'Steam',
}

const benefits = [
  ['01', 'Отбор', 'Прибыль, ликвидность и тренд — в одном решении.'],
  ['02', 'Адаптация', 'QuickConfig™ подстраивает диапазоны под капитал.'],
  ['03', 'Контроль', 'Лимиты дублей удерживают баланс портфеля.'],
  ['04', 'Цикл', 'Покупка, ожидание и продажа работают вместе.'],
]

const automationModules = [
  ['01', 'API покупки', 'Y.CS находит предложения, автоматически закупает предметы и распределяет bargain-ордера.', 'ПОКУПКА'],
  ['02', 'API продажи', 'Y.CS выставляет предметы на площадке и сопровождает их до завершённой продажи.', 'ПРОДАЖА'],
  ['03', 'Worker', 'Принимает предметы в инвентарь и передаёт их площадке продажи по её официальному сценарию.', 'ИНВЕНТАРЬ'],
]

const faqs = [
  ['Нужно держать Telegram открытым?', 'Нет. Бот нужен для управления и уведомлений.'],
  ['Что такое QuickConfig™?', 'Готовый профиль, который адаптирует параметры к вашему капиталу.'],
  ['Покупка и продажа включаются отдельно?', 'Да. Каждое направление управляется независимо.'],
  ['Где смотреть результат?', 'В ожидании, истории и уведомлениях бота.'],
  ['Как формируется доходность?', 'Y.CS помогает находить подходящие сделки и распределять капитал по выбранной стратегии. Итоговый результат зависит от движения рынка и может меняться.'],
]

function ArrowIcon() {
  return <svg viewBox="0 0 20 20" aria-hidden="true"><path d="M4 10h11M11 5l5 5-5 5"/></svg>
}

function Cta({href, children, secondary = false, prominent = false, caption}: {href: string; children: string; secondary?: boolean; prominent?: boolean; caption?: string}) {
  return <a className={`landing-cta${secondary ? ' is-secondary' : ''}${prominent ? ' is-prominent' : ''}`} href={href} target={href.startsWith('http') ? '_blank' : undefined} rel="noreferrer">
    <span>{children}{caption && <small>{caption}</small>}</span><ArrowIcon/>
  </a>
}

function CurrencyTicker({rates}: {rates: SteamCurrencyRate[]}) {
  const tickerRef = useRef<HTMLDivElement>(null)
  useEffect(() => {
    const ticker = tickerRef.current
    if (!ticker) return
    let visible = false
    const sync = () => ticker.classList.toggle('is-paused', !visible || document.hidden)
    const observer = new IntersectionObserver(entries => {
      visible = entries[0]?.isIntersecting ?? false
      sync()
    })
    observer.observe(ticker)
    document.addEventListener('visibilitychange', sync)
    return () => {
      observer.disconnect()
      document.removeEventListener('visibilitychange', sync)
    }
  }, [])
  if (!rates.length) return null
  const repeatedRates = Array.from({length: Math.max(1, Math.ceil(12 / rates.length))}, () => rates).flat()
  return <div ref={tickerRef} className="currency-ticker" aria-label="Расчётные курсы Steam">
    <div className="ticker-label"><span/>Steam rate</div>
    <div className="ticker-window"><div className="ticker-track">
      {[0, 1].map(copy => <div className="ticker-group" aria-hidden={copy === 1} key={copy}>
        {repeatedRates.map((item, index) => <span className="ticker-item" key={`${copy}-${item.currency}-${index}`} title={item.source ? `Источник: ${item.source}` : undefined}>
          <b>{item.currency}</b><em>{item.rate.toFixed(2)}</em>
        </span>)}
      </div>)}
    </div></div>
  </div>
}

function StatValue({value, kind}: {value?: number; kind?: 'money'}) {
  if (value === undefined) return <span className="stat-placeholder">—</span>
  return <>{kind === 'money' ? money.format(value) : number.format(value)}</>
}

function HeroDeals({deals}: {deals: LandingDeal[]}) {
  const arenaRef = useRef<HTMLDivElement>(null)
  const cardRefs = useRef<Array<HTMLElement | null>>([])
  const angleRef = useRef(-Math.PI / 2)
  const controlledRef = useRef(false)

  useEffect(() => {
    if (!deals.length) return
    let frame = 0
    let previous = performance.now()
    const positionCards = () => {
      const arena = arenaRef.current
      if (!arena) return
      const width = arena.clientWidth
      const height = arena.clientHeight
      cardRefs.current.forEach((card, index) => {
        if (!card) return
        const phase = angleRef.current + index * Math.PI * 2 / deals.length
        const depth = (Math.sin(phase) + 1) / 2
        const x = width / 2 + Math.cos(phase) * width * .31
        const y = height / 2 + Math.sin(phase) * height * .17
        card.style.left = `${x}px`
        card.style.top = `${y}px`
        card.style.zIndex = `${Math.round(depth * 10) + 2}`
        card.style.opacity = `${.62 + depth * .38}`
        card.style.transform = `translate(-50%, -50%) scale(${.74 + depth * .26})`
      })
    }
    const animate = (time: number) => {
      if (!controlledRef.current) angleRef.current += (time - previous) * .000075
      previous = time
      positionCards()
      frame = requestAnimationFrame(animate)
    }
    frame = requestAnimationFrame(animate)
    window.addEventListener('resize', positionCards)
    return () => {
      cancelAnimationFrame(frame)
      window.removeEventListener('resize', positionCards)
    }
  }, [deals])

  const controlOrbit = (event: ReactPointerEvent<HTMLDivElement>) => {
    const rect = event.currentTarget.getBoundingClientRect()
    const progress = Math.max(0, Math.min(1, (event.clientX - rect.left) / rect.width))
    controlledRef.current = true
    angleRef.current = -Math.PI * .9 + progress * Math.PI * 1.8
  }

  if (!deals.length) return <div className="hero-visual hero-deals-empty"><img src={logo} alt="Y.CS"/></div>

  return <div ref={arenaRef} className="hero-visual hero-deals" aria-label="Крупнейшие сделки дня" onPointerMove={controlOrbit} onPointerLeave={() => controlledRef.current = false}>
    <div className="deal-orbit"/>
    {deals.slice(0, 3).map((deal, index) => {
      const profit3 = deal.revenue * (1 - agentFeeRate) - deal.cost
      const profit5 = deal.revenue * (1 - marketFeeRate) - deal.cost
      return <article className="orbit-deal" tabIndex={0} key={`${deal.itemName}-${deal.soldAt}`} ref={element => {cardRefs.current[index] = element}} onPointerMove={event => event.stopPropagation()} onPointerEnter={() => controlledRef.current = true}>
        <div className="deal-image-wrap">{deal.imageUrl ? <img src={deal.imageUrl} alt={deal.itemName}/> : <img className="deal-fallback" src={logo} alt=""/>}<strong>{profit3 >= 0 ? '+' : ''}{exactMoney.format(profit3)}</strong></div>
        <div className="deal-detail">
          <span>Сделка дня</span><h3>{deal.itemName.replace(/^★\s*/, '')}</h3>
          <div className="deal-route">{marketNames[deal.source] ?? deal.source} <b>→</b> {marketNames[deal.destination] ?? deal.destination}</div>
          <dl><div><dt>Куплено</dt><dd>{exactMoney.format(deal.cost)}</dd></div><div><dt>Продано</dt><dd>{exactMoney.format(deal.revenue)}</dd></div><div><dt>Прибыль</dt><dd>+{exactMoney.format(deal.profit)}</dd></div><div><dt>После {agentFeePercent}%</dt><dd>{exactMoney.format(profit3)}</dd></div><div><dt>После {marketFeePercent}%</dt><dd>{exactMoney.format(profit5)}</dd></div></dl>
        </div>
      </article>
    })}
    <div className="orbit-hint">Двигайте курсор</div>
  </div>
}

function TutorialVideo() {
  const iframeRef = useRef<HTMLIFrameElement>(null)
  const frameRef = useRef<HTMLDivElement>(null)
  const seekingRef = useRef(false)
  const [muted, setMuted] = useState(true)
  const [playing, setPlaying] = useState(true)
  const [currentTime, setCurrentTime] = useState(0)
  const [duration, setDuration] = useState(0)
  const playingRef = useRef(true)
  const autoPausedRef = useRef(false)
  const command = (func: string, args: unknown[] = []) => iframeRef.current?.contentWindow?.postMessage(JSON.stringify({event: 'command', func, args}), 'https://www.youtube-nocookie.com')
  useEffect(() => {
    let visible = false
    const receivePlayerInfo = (event: MessageEvent) => {
      if (event.origin !== 'https://www.youtube-nocookie.com') return
      try {
        const payload = typeof event.data === 'string' ? JSON.parse(event.data) as {event?: string; info?: {currentTime?: number; duration?: number; playerState?: number}} : null
        if (payload?.event !== 'infoDelivery') return
        if (typeof payload.info?.currentTime === 'number' && !seekingRef.current) setCurrentTime(payload.info.currentTime)
        if (typeof payload.info?.duration === 'number') setDuration(payload.info.duration)
        if (typeof payload.info?.playerState === 'number') {
          const isPlaying = payload.info.playerState === 1
          playingRef.current = isPlaying
          setPlaying(isPlaying)
        }
      } catch { /* YouTube also sends non-JSON messages. */ }
    }
    window.addEventListener('message', receivePlayerInfo)
    const send = (func: string) => iframeRef.current?.contentWindow?.postMessage(JSON.stringify({event: 'command', func, args: []}), 'https://www.youtube-nocookie.com')
    const syncPlayback = () => {
      const shouldPause = !visible || document.hidden
      if (shouldPause && playingRef.current) {
        autoPausedRef.current = true
        send('pauseVideo')
      } else if (!shouldPause && autoPausedRef.current) {
        autoPausedRef.current = false
        send('playVideo')
      }
    }
    const visibilityObserver = new IntersectionObserver(entries => {
      visible = entries[0]?.isIntersecting ?? false
      syncPlayback()
    }, {rootMargin: '100px'})
    if (frameRef.current) visibilityObserver.observe(frameRef.current)
    document.addEventListener('visibilitychange', syncPlayback)
    const timer = window.setInterval(() => {
      if (!visible || document.hidden) return
      iframeRef.current?.contentWindow?.postMessage(JSON.stringify({event: 'listening', id: 'ycs-tutorial'}), 'https://www.youtube-nocookie.com')
      command('getCurrentTime')
      command('getDuration')
      command('getPlayerState')
    }, 500)
    return () => {
      window.removeEventListener('message', receivePlayerInfo)
      window.clearInterval(timer)
      visibilityObserver.disconnect()
      document.removeEventListener('visibilitychange', syncPlayback)
    }
  }, [])
  const formatTime = (value: number) => `${Math.floor(value / 60)}:${Math.floor(value % 60).toString().padStart(2, '0')}`
  const togglePlayback = () => {
    command(playing ? 'pauseVideo' : 'playVideo')
    setPlaying(value => !value)
  }
  const toggleSound = () => {
    command(muted ? 'unMute' : 'mute')
    setMuted(value => !value)
  }
  const finishSeeking = (value: string) => {
    const next = Number(value)
    command('seekTo', [next, true])
    seekingRef.current = false
  }
  return <div className="tutorial-video"><div className="video-frame" ref={frameRef}><iframe ref={iframeRef} src="https://www.youtube-nocookie.com/embed/vGPrg0eZCeE?autoplay=1&mute=1&controls=0&enablejsapi=1&playsinline=1&rel=0" title="Видео о торговой площадке" loading="lazy" referrerPolicy="strict-origin-when-cross-origin" allow="autoplay; encrypted-media; picture-in-picture; fullscreen" allowFullScreen/><div className="video-controls"><button type="button" onClick={togglePlayback} aria-label={playing ? 'Поставить на паузу' : 'Продолжить видео'}>{playing ? 'Ⅱ' : '▶'}</button><span className="video-time">{formatTime(currentTime)}</span><input type="range" min="0" max={Math.max(duration, 1)} step="0.1" value={Math.min(currentTime, Math.max(duration, 1))} disabled={!duration} onPointerDown={() => seekingRef.current = true} onInput={event => setCurrentTime(Number(event.currentTarget.value))} onPointerUp={event => finishSeeking(event.currentTarget.value)} onKeyUp={event => finishSeeking(event.currentTarget.value)} aria-label="Перемотать видео"/><span className="video-time">{formatTime(duration)}</span><button type="button" onClick={toggleSound} aria-label={muted ? 'Включить звук' : 'Выключить звук'}>{muted ? 'Звук' : 'Без звука'}</button><button type="button" onClick={() => void frameRef.current?.requestFullscreen()} aria-label="Открыть на весь экран">⛶</button></div></div><p className="video-context">В видео показан пример с CSFloat. Площадка выбрана как наиболее перспективная на данный момент.</p></div>
}

type FeeMode = 'agent' | 'market'

function ProfitChart({daily, feeMode}: {daily: LandingDailyStatistics[]; feeMode: FeeMode}) {
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null)
  const [tooltipX, setTooltipX] = useState(0)
  const svgRef = useRef<SVGSVGElement>(null)
  const selectedFeeRate = feeMode === 'agent' ? agentFeeRate : marketFeeRate
  const selectedFeePercent = feeMode === 'agent' ? agentFeePercent : marketFeePercent
  const chart = useMemo(() => {
    if (!daily.length) return null
    const series = [
      daily.map(day => day.revenue - day.cost),
      daily.map(day => day.revenue * (1 - selectedFeeRate) - day.cost),
    ]
    const values = series.flat()
    const min = Math.floor(Math.min(...values, 0) / 100) * 100
    const max = Math.ceil(Math.max(...values, 0) / 100) * 100 || 100
    const range = max - min || 1
    const points = series.map(line => line.map((value, index) => {
      const x = line.length === 1 ? 50 : index / (line.length - 1) * 100
      const y = 92 - ((value - min) / range) * 78
      return `${x},${y}`
    }).join(' '))
    const zeroY = 92 - ((0 - min) / range) * 78
    return {points, min, max, zeroY, series}
  }, [daily, selectedFeeRate])

  if (!chart) return <div className="chart-empty">График появится после загрузки статистики</div>
  const selectDay = (event: ReactPointerEvent<HTMLDivElement>) => {
    const svgRect = svgRef.current?.getBoundingClientRect()
    if (!svgRect) return
    const chartRect = event.currentTarget.getBoundingClientRect()
    const progress = Math.max(0, Math.min(1, (event.clientX - svgRect.left) / svgRect.width))
    const index = Math.round(progress * (daily.length - 1))
    const snappedProgress = daily.length === 1 ? .5 : index / (daily.length - 1)
    setHoveredIndex(index)
    setTooltipX(svgRect.left - chartRect.left + snappedProgress * svgRect.width)
  }
  const selected = hoveredIndex === null ? null : daily[hoveredIndex]
  return <div className="profit-chart" onPointerMove={selectDay} onPointerLeave={() => setHoveredIndex(null)}>
    <div className="chart-scale"><span>{money.format(chart.max)}</span><span>{money.format(chart.min)}</span></div>
    <svg ref={svgRef} viewBox="0 0 100 100" preserveAspectRatio="none" role="img" aria-label="Результат завершённых продаж за 30 дней">
      <line x1="0" y1={chart.zeroY} x2="100" y2={chart.zeroY} className="zero-line"/>
      {chart.points.map((points, index) => <polyline key={index} points={points} className={`chart-line line-${index}`}/>)}
      {hoveredIndex !== null && <line x1={daily.length === 1 ? 50 : hoveredIndex / (daily.length - 1) * 100} y1="5" x2={daily.length === 1 ? 50 : hoveredIndex / (daily.length - 1) * 100} y2="95" className="chart-cursor"/>}
    </svg>
    {selected && <div className="chart-tooltip" style={{left: tooltipX}}>
      <b>{selected.date}</b><span>{selected.sales} продаж</span><span>После {selectedFeePercent}% <strong>{exactMoney.format(chart.series[1][hoveredIndex!])}</strong></span>
    </div>}
  </div>
}

function PriceRows({prices}: {prices?: Record<string, number>}) {
  const entries = Object.entries(prices ?? {})
  if (!entries.length) return <div className="price-row"><span>Актуальная цена</span><b>В Telegram</b></div>
  return <>{entries.map(([market, price]) => <div className="price-row" key={market}><span>{marketNames[market] ?? market.replaceAll('_', ' ')}</span><b>${price.toFixed(2)}</b></div>)}</>
}

export function LandingPage() {
  const [overview, setOverview] = useState<LandingOverview | null>(null)
  const [rates, setRates] = useState<SteamCurrencyRate[]>([])
  const [deals, setDeals] = useState<LandingDeal[]>([])
  const [feeMode, setFeeMode] = useState<FeeMode>('agent')
  const [darkTheme, setDarkTheme] = useState(() => {
    const saved = localStorage.getItem('ycs-theme')
    return saved ? saved === 'dark' : true
  })

  useEffect(() => {
    void landingApi.overview().then(setOverview).catch(() => undefined)
    void landingApi.steamCurrency().then(setRates).catch(() => undefined)
    void landingApi.deals().then(setDeals).catch(() => undefined)
  }, [])

  useEffect(() => {
    localStorage.setItem('ycs-theme', darkTheme ? 'dark' : 'light')
  }, [darkTheme])

  const stats = overview?.statistics
  const pricing = overview?.pricing

  return <main className="public-landing" data-theme={darkTheme ? 'dark' : 'light'} id="start">
    <CurrencyTicker rates={rates}/>
    <header className="landing-nav">
      <a className="landing-brand" href="#start" aria-label="Y.CS — наверх"><img src={logo} alt=""/><span>Y.CS</span></a>
      <nav aria-label="Навигация по странице"><a href="#how">Как работает</a><a href="#pricing">Тарифы</a><a href="#results">Результаты</a><a href="#faq">FAQ</a></nav>
      <div className="nav-actions"><button className="theme-toggle" type="button" onClick={() => setDarkTheme(value => !value)} aria-label={darkTheme ? 'Включить светлую тему' : 'Включить тёмную тему'} title={darkTheme ? 'Светлая тема' : 'Тёмная тема'}><span aria-hidden="true">{darkTheme ? '☀' : '◐'}</span></button><Cta href={supportUrl} secondary>Поддержка</Cta></div>
    </header>

    <section className="landing-hero landing-container">
      <div className="hero-copy">
        <div className="landing-kicker"><span/>Торговая система CS2</div>
        <h1>Рынок движется.<br/><em>Y.CS работает.</em></h1>
        <p>Отбор, покупка и продажа скинов. Автоматически.</p>
        <div className="hero-actions"><Cta href={botUrl} prominent caption="Запуск и настройка в Telegram">Начать трейдить</Cta><Cta href="#how" secondary>Как это работает</Cta></div>
        <div className="hero-note"><span className="status-dot"/>Настройка один раз · управление в Telegram</div>
      </div>
      <HeroDeals deals={deals}/>
    </section>

    <section className="landing-stats landing-container" aria-label="Статистика сервиса">
      <article><span>Покупок выполнено</span><strong><StatValue value={stats?.purchases}/></strong></article>
      <article><span>Оборот покупок</span><strong><StatValue value={stats?.purchaseVolume} kind="money"/></strong></article>
      <article><span>Завершённых продаж</span><strong><StatValue value={stats?.sales}/></strong></article>
      <article><span>Объём продаж</span><strong><StatValue value={stats?.revenue} kind="money"/></strong></article>
    </section>

    <section className="landing-section landing-container" id="how">
      <div className="section-heading system-heading"><div><span className="section-index">01 / Подключение системы</span><h2>Три модуля для<br/>автоматической торговли</h2></div><p>Добавьте доступы один раз. Дальше Y.CS ведёт полный цикл сделки.</p></div>
      <div className="automation-panel">
        <div className="automation-panel-head"><span>СХЕМА ПОДКЛЮЧЕНИЯ</span><span className="system-online"><i/>СИСТЕМА ГОТОВА</span></div>
        <div className="automation-content">
          <div className="automation-modules">{automationModules.map(([index, title, text, kind]) => <article key={index}><div className="module-meta"><span>{index}</span><b>{kind}</b></div><div className="module-copy"><h3>{title}</h3><p>{text}</p></div><div className="module-status"><i/>Подключается в Telegram</div></article>)}</div>
          <TutorialVideo/>
        </div>
        <div className="trade-safety"><b>Безопасная передача</b><span>Worker использует официальные решения площадок и передаёт предмет только по подтверждённому сценарию площадки продажи.</span></div>
      </div>
    </section>

    <section className="benefits-wrap">
      <div className="landing-section landing-container">
        <div className="section-heading"><div><span className="section-index">02 / Система</span><h2>Сложное — внутри.<br/>Понятное — снаружи.</h2></div><p>Математика рынка без ручной настройки десятков параметров.</p></div>
        <div className="benefit-grid">{benefits.map(([index, title, text]) => <article key={index}><span>{index}</span><h3>{title}</h3><p>{text}</p></article>)}</div>
      </div>
    </section>

    <section className="landing-section landing-container" id="pricing">
      <div className="section-heading"><div><span className="section-index">03 / Тарифы</span><h2>Только нужные<br/>направления.</h2></div><p>Актуальные условия Y.CS.</p></div>
      <div className="pricing-grid">
        <article><div className="price-icon">↙</div><span className="price-kind">Покупка</span><h3>Автопокупка</h3><p>Задайте правила один раз — сервис найдёт и купит подходящие предметы.</p><PriceRows prices={pricing?.buySubPrices}/></article>
        <article className="featured-price"><div className="popular-label">Развитие автопокупки</div><div className="price-icon">⌁</div><span className="price-kind">Автоматический торг</span><h3>Bargain-покупка</h3><p>Флагманский модуль ведёт торг с продавцом и выкупает предмет по лучшей цене.</p><PriceRows prices={pricing?.bargainBuySubPrices}/></article>
        <article><div className="price-icon">↗</div><span className="price-kind">Продажа</span><h3>Автопродажа</h3><p>Система выставляет предметы и сопровождает продажу до завершения.</p><PriceRows prices={pricing?.sellSubPrices}/></article>
        <article><div className="price-icon">◎</div><span className="price-kind">Полная автоматизация</span><h3>Worker</h3><p>Автоматически принимает и передаёт предметы — без ручных действий.</p><div className="price-row"><span>{pricing?.workerPriceData ? `${pricing.workerPriceData.accCount} аккаунт · ${pricing.workerPriceData.periodDays} дней` : 'Актуальная цена'}</span><b>{pricing?.workerPriceData ? `$${pricing.workerPriceData.price.toFixed(2)}` : 'В Telegram'}</b></div></article>
      </div>
    </section>

    <section className="results-wrap" id="results">
      <div className="landing-section landing-container results-grid">
        <div className="results-copy"><span className="section-index">04 / Результат</span><h2>Цифры без<br/>прикрас.</h2><p>Фактическая себестоимость и завершённые продажи.</p>
          <fieldset className="fee-selector"><legend>Комиссия вывода</legend><label className={feeMode === 'agent' ? 'is-selected' : ''}><input type="radio" name="fee-mode" value="agent" checked={feeMode === 'agent'} onChange={() => setFeeMode('agent')}/><span>Посредник</span><b>{agentFeePercent}%</b></label><label className={feeMode === 'market' ? 'is-selected' : ''}><input type="radio" name="fee-mode" value="market" checked={feeMode === 'market'} onChange={() => setFeeMode('market')}/><span>Market.CS</span><b>{marketFeePercent}%</b></label></fieldset>
          <div className="chart-legend"><span className="legend-gross">Без комиссии</span><span className={`${feeMode === 'agent' ? 'legend-three' : 'legend-five'} has-tip`}>После {feeMode === 'agent' ? agentFeePercent : marketFeePercent}%<span className="legend-tip">{feeMode === 'agent' ? <>Работа через <a href={supportUrl} target="_blank" rel="noreferrer">посредника</a></> : 'Комиссия вывода Market.CS'}</span></span></div>
        </div>
        <div className="chart-card"><div className="chart-card-head"><div><span>Последние 30 дней</span><b>Результат продаж</b></div><small>USD</small></div><ProfitChart daily={stats?.daily ?? []} feeMode={feeMode}/><div className="chart-dates"><span>{stats?.daily?.[0]?.date ?? '30 дней назад'}</span><span>{stats?.daily?.at(-1)?.date ?? 'Сегодня'}</span></div></div>
      </div>
    </section>

    <section className="landing-section landing-container faq-section" id="faq">
      <div><span className="section-index">05 / Ответы</span><h2>Вопросы перед стартом</h2><p>Коротко о том, как устроена работа сервиса.</p></div>
      <div className="faq-list">{faqs.map(([question, answer], index) => <details key={question} open={index === 0}><summary>{question}<span>+</span></summary><p>{answer}</p></details>)}</div>
    </section>

    <section className="final-cta landing-container"><div><span className="section-index">Y.CS</span><h2>Настройте один раз.<br/>Наблюдайте.</h2></div><div className="final-actions"><Cta href={botUrl}>Открыть бота</Cta><Cta href={supportUrl} secondary>Написать в поддержку</Cta></div></section>

    <footer className="landing-footer"><div className="landing-container"><a className="landing-brand footer-brand" href="#start"><img src={logo} alt=""/><span>Y.CS</span></a><p>Автоматизация торговли предметами CS2.</p><div className="footer-legal"><small>Y.CS не связан с Valve Corporation. Торговля связана с рыночным риском.</small><a href="/offers">Оферты и условия <span>→</span></a></div></div></footer>
  </main>
}

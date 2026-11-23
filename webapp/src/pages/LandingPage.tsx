import {useContext, useEffect, useMemo, useRef, useState} from 'react'
import type {PointerEvent as ReactPointerEvent} from 'react'
import '../landing.css'
import {landingApi} from '../api/services'
import {useAnimatedNumber} from '../hooks/useAnimatedNumber'
import {AuthStateContext} from '../auth/authState'
import type {LandingDailyStatistics, LandingDeal, LandingOverview} from '../api/types'
import logo from '../assets/brand/youtrade-mark-transparent.png'
import {SiteLink} from '../components/layout/SiteLink'
import {useSiteTheme} from '../components/layout/siteThemeState'
import icons from '../../../shared/site/icons.svg?no-inline'

const supportUrl = 'https://t.me/youtradecs_sup'

const marketFeePercent = Number(import.meta.env.VITE_MARKET_FEE_PERCENT || 5)
const marketFeeRate = marketFeePercent / 100

const money = new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD', maximumFractionDigits: 0})
const exactMoney = new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD', minimumFractionDigits: 2})
const number = new Intl.NumberFormat('ru-RU', {maximumFractionDigits: 0})
const feePercent = new Intl.NumberFormat('ru-RU', {maximumFractionDigits: 2})
const marketNames: Record<string, string> = {
  CSFLOAT: 'CSFloat',
  MARKET_CSGO: 'Market.CS',
  LIS_SKINS: 'LIS-Skins',
  STEAM: 'Steam',
}

const benefits = [
  ['Отбор', 'Прибыль, ликвидность и тренд — в одном решении.'],
  ['Адаптация', 'QuickConfig™ подстраивает диапазоны под капитал.'],
  ['Контроль', 'Лимиты дублей удерживают баланс портфеля.'],
  ['Цикл', 'Покупка, ожидание и продажа работают вместе.'],
]

const automationModules = [
  ['API покупки', 'Y.CS находит предложения, автоматически закупает предметы и распределяет bargain-ордера.', 'ПОКУПКА'],
  ['API продажи', 'Y.CS выставляет предметы на площадке и сопровождает их до завершённой продажи.', 'ПРОДАЖА'],
  ['Worker', 'Принимает предметы в инвентарь и передаёт их площадке продажи по её официальному сценарию.', 'ИНВЕНТАРЬ'],
]

function Cta({href, children, secondary = false, prominent = false, caption}: {href: string; children: string; secondary?: boolean; prominent?: boolean; caption?: string}) {
  return <SiteLink className={`site-control site-control--large site-control--${secondary ? 'quiet' : 'accent'} landing-cta${prominent ? ' is-prominent' : ''}`} href={href} target={href.startsWith('http') ? '_blank' : undefined} rel="noreferrer">
    <span>{children}{caption && <small>{caption}</small>}</span>
  </SiteLink>
}

function StatValue({value, kind}: {value?: number; kind?: 'money'}) {
  if (value === undefined) return <span className="stat-placeholder">—</span>
  return <>{kind === 'money' ? money.format(value) : number.format(value)}</>
}

function HeroDeals({deals, agentFeePercent}: {deals: LandingDeal[]; agentFeePercent: number | null}) {
  const arenaRef = useRef<HTMLDivElement>(null)
  const cardRefs = useRef<Array<HTMLElement | null>>([])
  const angleRef = useRef(-Math.PI / 2)
  const targetAngleRef = useRef(-Math.PI / 2)
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
      const elapsed = time - previous
      if (controlledRef.current) {
        const smoothing = 1 - Math.exp(-elapsed / 190)
        angleRef.current += (targetAngleRef.current - angleRef.current) * smoothing
      } else {
        angleRef.current += elapsed * .000075
        targetAngleRef.current = angleRef.current
      }
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
    const desiredAngle = -Math.PI * .9 + progress * Math.PI * 1.8
    const fullTurn = Math.PI * 2
    controlledRef.current = true
    targetAngleRef.current = desiredAngle + Math.round((angleRef.current - desiredAngle) / fullTurn) * fullTurn
  }

  if (!deals.length) return <div className="hero-visual hero-deals-empty"><img src={logo} alt="Y.CS"/></div>

  return <div ref={arenaRef} className="hero-visual hero-deals" aria-label="Крупнейшие сделки дня" onPointerMove={controlOrbit} onPointerLeave={() => controlledRef.current = false}>
    <div className="deal-orbit"/>
    {deals.slice(0, 3).map((deal, index) => {
      const profitAgent = agentFeePercent === null ? null : deal.revenue * (1 - agentFeePercent / 100) - deal.cost
      const profitMarket = deal.revenue * (1 - marketFeeRate) - deal.cost
      return <article className="orbit-deal" tabIndex={0} key={`${deal.itemName}-${deal.soldAt}`} ref={element => {cardRefs.current[index] = element}} onPointerMove={event => event.stopPropagation()} onPointerEnter={() => controlledRef.current = true}>
        <div className="deal-image-wrap">{deal.imageUrl ? <img src={deal.imageUrl} alt={deal.itemName}/> : <img className="deal-fallback" src={logo} alt=""/>}{profitAgent !== null && <strong>{profitAgent >= 0 ? '+' : ''}{exactMoney.format(profitAgent)}</strong>}</div>
        <div className="deal-detail">
          <span>Сделка дня</span><h3>{deal.itemName.replace(/^★\s*/, '')}</h3>
          <div className="deal-route">{marketNames[deal.source] ?? deal.source} <b>→</b> {marketNames[deal.destination] ?? deal.destination}</div>
          <dl><div><dt>Куплено</dt><dd>{exactMoney.format(deal.cost)}</dd></div><div><dt>Продано</dt><dd>{exactMoney.format(deal.revenue)}</dd></div><div><dt>Прибыль</dt><dd>+{exactMoney.format(deal.profit)}</dd></div><div><dt>{agentFeePercent === null ? 'Y.CS' : `После ${agentFeePercent}%`}</dt><dd>{profitAgent === null ? '—' : exactMoney.format(profitAgent)}</dd></div><div><dt>После {marketFeePercent}%</dt><dd>{exactMoney.format(profitMarket)}</dd></div></dl>
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
  return <div className="tutorial-video"><div className="video-frame" ref={frameRef}><iframe ref={iframeRef} src="https://www.youtube-nocookie.com/embed/vGPrg0eZCeE?autoplay=1&mute=1&controls=0&enablejsapi=1&playsinline=1&rel=0" title="Видео о торговой площадке" loading="lazy" referrerPolicy="strict-origin-when-cross-origin" allow="autoplay; encrypted-media; picture-in-picture; fullscreen" allowFullScreen/><div className="video-controls site-control-surface--dark"><button className="site-control site-control--quiet site-control--icon site-control--large" type="button" onClick={togglePlayback} aria-label={playing ? 'Поставить на паузу' : 'Продолжить видео'}><svg aria-hidden="true"><use href={`${icons}#${playing ? 'pause' : 'play'}`}/></svg></button><span className="video-time">{formatTime(currentTime)}</span><input type="range" min="0" max={Math.max(duration, 1)} step="0.1" value={Math.min(currentTime, Math.max(duration, 1))} disabled={!duration} onPointerDown={() => seekingRef.current = true} onInput={event => setCurrentTime(Number(event.currentTarget.value))} onPointerUp={event => finishSeeking(event.currentTarget.value)} onKeyUp={event => finishSeeking(event.currentTarget.value)} aria-label="Перемотать видео"/><span className="video-time">{formatTime(duration)}</span><button className="site-control site-control--quiet site-control--icon site-control--large" type="button" onClick={toggleSound} aria-label={muted ? 'Включить звук' : 'Выключить звук'}><svg aria-hidden="true"><use href={`${icons}#${muted ? 'volume-off' : 'volume'}`}/></svg></button><button className="site-control site-control--quiet site-control--icon site-control--large" type="button" onClick={() => void frameRef.current?.requestFullscreen()} aria-label="Открыть на весь экран"><svg aria-hidden="true"><use href={`${icons}#fullscreen`}/></svg></button></div></div><p className="video-context">В видео показан пример с CSFloat. Площадка выбрана как наиболее перспективная на данный момент.</p></div>
}

function ProfitChart({daily, selectedFeePercent, maxFeePercent}: {daily: LandingDailyStatistics[]; selectedFeePercent: number | null; maxFeePercent: number}) {
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null)
  const [tooltipX, setTooltipX] = useState(0)
  const svgRef = useRef<SVGSVGElement>(null)
  const animatedFeePercent = useAnimatedNumber(selectedFeePercent)
  const chart = useMemo(() => {
    if (!daily.length) return null
    const series = [daily.map(day => day.revenue - day.cost)]
    if (animatedFeePercent !== null)
      series.push(daily.map(day => day.revenue * (1 - animatedFeePercent / 100) - day.cost))
    const values = [...series[0], ...daily.map(day => day.revenue * (1 - maxFeePercent / 100) - day.cost)]
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
  }, [daily, animatedFeePercent, maxFeePercent])

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
    <svg ref={svgRef} viewBox="0 0 100 100" preserveAspectRatio="none" role="img" aria-label="Результат завершённых продаж за период">
      <line x1="0" y1={chart.zeroY} x2="100" y2={chart.zeroY} className="zero-line"/>
      {chart.points.map((points, index) => <polyline key={index} points={points} className={`chart-line line-${index}`}/>)}
      {hoveredIndex !== null && <line x1={daily.length === 1 ? 50 : hoveredIndex / (daily.length - 1) * 100} y1="5" x2={daily.length === 1 ? 50 : hoveredIndex / (daily.length - 1) * 100} y2="95" className="chart-cursor"/>}
    </svg>
    {selected && <div className="chart-tooltip" style={{left: tooltipX}}>
      <b>{selected.date}</b><span>{selected.sales} продаж</span>{selectedFeePercent === null
        ? <span>Расчёт временно недоступен</span>
        : <span>После {feePercent.format(selectedFeePercent)}% <strong>{exactMoney.format(selected.revenue * (1 - selectedFeePercent / 100) - selected.cost)}</strong></span>}
    </div>}
  </div>
}

function TradingResults({daily, agentFeePercent}: {daily: LandingDailyStatistics[]; agentFeePercent: number | null}) {
  const [feeOffset, setFeeOffset] = useState(0)
  const [dragging, setDragging] = useState(false)
  const minFee = agentFeePercent ?? marketFeePercent
  const maxFee = Math.max(minFee, marketFeePercent)
  const rawFee = Math.min(maxFee, minFee + feeOffset)
  const selectedFee = agentFeePercent === null ? null
    : rawFee >= maxFee ? maxFee : Math.min(maxFee, Number((minFee + Math.round((rawFee - minFee) / .05) * .05).toFixed(4)))
  const progress = maxFee === minFee ? 0 : (rawFee - minFee) / (maxFee - minFee) * 100
  const total = selectedFee === null || !daily.length ? null
    : daily.reduce((sum, day) => sum + day.revenue * (1 - selectedFee / 100) - day.cost, 0)
  const animatedTotal = useAnimatedNumber(total, {delay: 100, duration: 650, paused: dragging})
  const direction = dragging || total === null || animatedTotal === null || Math.abs(total - animatedTotal) < .005
    ? '' : total > animatedTotal ? 'is-increasing' : 'is-decreasing'
  const sliderDisabled = agentFeePercent === null || maxFee === minFee

  return <section className="results-wrap" id="results">
    <div className="landing-section landing-container results-grid">
      <div className="results-copy"><h2>Доходы<br/>сервиса</h2><p>Доходность проекта за последний месяц на основе всех сделок через Y.CS.</p>
        <div className={`fee-slider${sliderDisabled ? ' is-disabled' : ''}`}>
          <div className="fee-slider-head"><label htmlFor="withdrawal-fee">Комиссия вывода</label><output htmlFor="withdrawal-fee">{selectedFee === null ? '—' : `${feePercent.format(selectedFee)}%`}</output></div>
          <input id="withdrawal-fee" type="range" min={minFee} max={maxFee} step="any" value={rawFee} disabled={sliderDisabled}
            style={{backgroundImage: `linear-gradient(to right, var(--red) ${progress}%, var(--fee-track) ${progress}%)`}}
            aria-valuetext={selectedFee === null ? 'Расчёт недоступен' : `${feePercent.format(selectedFee)}%`}
            onChange={event => setFeeOffset(Number(event.currentTarget.value) - minFee)}
            onPointerDown={event => {event.currentTarget.setPointerCapture(event.pointerId); setDragging(true)}}
            onPointerUp={() => setDragging(false)} onPointerCancel={() => setDragging(false)} onBlur={() => setDragging(false)}
            onKeyDown={event => {
              const changes: Record<string, number> = {ArrowLeft: -.05, ArrowDown: -.05, ArrowRight: .05, ArrowUp: .05, PageDown: -.5, PageUp: .5}
              if (!(event.key in changes) && event.key !== 'Home' && event.key !== 'End') return
              event.preventDefault()
              const next = event.key === 'Home' ? minFee : event.key === 'End' ? maxFee : (selectedFee ?? minFee) + changes[event.key]
              setFeeOffset(Math.max(0, Math.min(maxFee, next) - minFee))
            }}/>
          <div className="fee-slider-bounds"><span><b>Y.CS</b>{agentFeePercent === null ? '—' : `${feePercent.format(agentFeePercent)}%`}</span><span><b>Market.CS</b>{feePercent.format(marketFeePercent)}%</span></div>
        </div>
        <div className="chart-legend"><span className="legend-gross">До комиссии вывода</span>{selectedFee !== null && <span className="legend-three">После {feePercent.format(selectedFee)}%</span>}</div>
        {agentFeePercent === null && <p className="fee-note">Расчёт комиссии Y.CS временно недоступен.</p>}
      </div>
      <div className="chart-card"><div className="chart-card-head"><div><span>Последний месяц</span><b>Результат продаж</b></div><small>USD</small></div><ProfitChart daily={daily} selectedFeePercent={selectedFee} maxFeePercent={maxFee}/><div className="chart-dates"><span>{daily[0]?.date ?? 'Месяц назад'}</span><span>{daily.at(-1)?.date ?? 'Сегодня'}</span></div></div>
      <div className="results-total">
        <div><span className="results-total-label">Прибыль за период</span><p>Все сделки на графике · после комиссии вывода{selectedFee === null ? '' : ` ${feePercent.format(selectedFee)}%`}</p></div>
        <output className={`results-total-value ${direction}`} aria-live="off">{animatedTotal === null ? '—' : exactMoney.format(Math.abs(animatedTotal) < .005 ? 0 : animatedTotal)}</output>
      </div>
    </div>
  </section>
}

function PriceRows({prices}: {prices?: Record<string, number>}) {
  const entries = Object.entries(prices ?? {})
  return <>
    <div className="price-basis">Комиссия на каждые $1,000 оборота</div>
    {!entries.length
      ? <div className="price-row"><span>Актуальная цена</span><b>В Telegram</b></div>
      : entries.map(([market, price]) => <div className="price-row" key={market}><span>{marketNames[market] ?? market.replaceAll('_', ' ')}</span><b>${price.toFixed(2)}</b></div>)}
  </>
}

export function LandingPage() {
  const auth = useContext(AuthStateContext)
  const cabinetUrl = auth?.user ? '/dashboard' : '/auth'
  const [overview, setOverview] = useState<LandingOverview | null>(null)
  const [deals, setDeals] = useState<LandingDeal[]>([])
  const {theme} = useSiteTheme()

  useEffect(() => {
    const refresh = () => {
      void landingApi.overview().then(setOverview).catch(() => undefined)
      void landingApi.deals().then(setDeals).catch(() => undefined)
    }
    refresh()
    const interval = window.setInterval(refresh, 5 * 60 * 1000)
    return () => window.clearInterval(interval)
  }, [])

  const stats = overview?.statistics
  const pricing = overview?.pricing
  const balanceMarketPricing = overview?.balanceMarketPricing
  const agentFeePercent = balanceMarketPricing?.available
    && typeof balanceMarketPricing.totalFeePercent === 'number'
    && Number.isFinite(balanceMarketPricing.totalFeePercent)
    ? balanceMarketPricing.totalFeePercent : null

  return <main className="public-landing" data-theme={theme} id="start">

    <section className="landing-hero landing-container">
      <div className="hero-copy">
        <h1>Рынок движется<br/><em>Y.CS работает</em></h1>
        <p>Отбор, покупка и продажа скинов. Автоматически.</p>
        <div className="hero-actions"><Cta href={cabinetUrl} prominent caption="Один профиль для сайта и Telegram">{auth?.user ? 'Зайти в кабинет' : 'Начать работу'}</Cta></div>
      </div>
      <HeroDeals deals={deals} agentFeePercent={agentFeePercent}/>
    </section>

    <section className="landing-stats landing-container" aria-label="Статистика сервиса">
      <article><span>Покупок выполнено</span><strong><StatValue value={stats?.purchases}/></strong></article>
      <article><span>Оборот покупок</span><strong><StatValue value={stats?.purchaseVolume} kind="money"/></strong></article>
      <article><span>Завершённых продаж</span><strong><StatValue value={stats?.sales}/></strong></article>
      <article><span>Объём продаж</span><strong><StatValue value={stats?.revenue} kind="money"/></strong></article>
    </section>

    <section className="landing-section landing-container" id="how">
      <div className="section-heading system-heading"><h2>Три модуля для<br/>автоматической торговли</h2><p>Добавьте доступы один раз. Дальше Y.CS ведёт полный цикл сделки.</p></div>
      <div className="automation-panel">
        <div className="automation-panel-head"><span>СХЕМА ПОДКЛЮЧЕНИЯ</span><span className="system-online"><i/>СИСТЕМА ГОТОВА</span></div>
        <div className="automation-content">
          <div className="automation-modules">{automationModules.map(([title, text, kind]) => <article key={title}><b className="module-meta">{kind}</b><div><h3>{title}</h3><p>{text}</p></div><div className="module-status"><i/>Подключается в Telegram</div></article>)}</div>
          <TutorialVideo/>
        </div>
        <div className="trade-safety"><b>Безопасная передача</b><span>Worker использует официальные решения площадок и передаёт предмет только по подтверждённому сценарию площадки продажи.</span></div>
      </div>
    </section>

    <section className="benefits-wrap">
      <div className="landing-section landing-container">
        <div className="section-heading"><h2>Сложное — внутри<br/>Понятное — снаружи</h2><p>Математика рынка без ручной настройки десятков параметров.</p></div>
        <div className="benefit-grid">{benefits.map(([title, text]) => <article key={title}><h3>{title}</h3><p>{text}</p></article>)}</div>
      </div>
    </section>

    <section className="landing-section landing-container" id="pricing">
      <div className="section-heading"><h2>Актуальные условия <span className="site-wordmark">Y.CS</span></h2></div>
      <div className="pricing-grid">
        <article><div className="price-icon">↙</div><span className="price-kind">Покупка</span><h3>Автопокупка</h3><p>Задайте правила один раз — сервис найдёт и купит подходящие предметы.</p><PriceRows prices={pricing?.buySubPrices}/></article>
        <article className="featured-price"><div className="popular-label">Развитие автопокупки</div><div className="price-icon">⌁</div><span className="price-kind">Автоматический торг</span><h3>Bargain-покупка</h3><p>Флагманский модуль ведёт торг с продавцом и выкупает предмет по лучшей цене.</p><PriceRows prices={pricing?.bargainBuySubPrices}/></article>
        <article><div className="price-icon">↗</div><span className="price-kind">Продажа</span><h3>Автопродажа</h3><p>Система выставляет предметы и сопровождает продажу до завершения.</p><PriceRows prices={pricing?.sellSubPrices}/></article>
        <article><div className="price-icon">◎</div><span className="price-kind">Полная автоматизация</span><h3>Worker</h3><p>Автоматически принимает и передаёт предметы — без ручных действий.</p><div className="price-basis">Плата за аккаунт каждые 30 дней</div><div className="price-row"><span>{pricing?.workerPriceData ? `${pricing.workerPriceData.accCount} аккаунт · ${pricing.workerPriceData.periodDays} дней` : 'Актуальная цена'}</span><b>{pricing?.workerPriceData ? `$${pricing.workerPriceData.price.toFixed(2)}` : 'В Telegram'}</b></div></article>
      </div>
    </section>

    <TradingResults daily={stats?.daily ?? []} agentFeePercent={agentFeePercent}/>

    <div className="landing-close site-control-surface--dark">
      <section className="final-cta landing-container">
        <span className="final-cta-label">Y.CS</span>
        <h2>Настройте один раз<br/>Наблюдайте</h2>
        <div className="final-actions">
          <Cta href={supportUrl} secondary>Написать в поддержку</Cta>
        </div>
      </section>
    </div>
  </main>
}

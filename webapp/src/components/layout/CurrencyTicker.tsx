import {useEffect, useRef, useState} from 'react'
import {bindTickerVisibility, observeSteamRates, repeatTickerRates, type SteamCurrencyRate} from '../../../../shared/site/currency'

export function CurrencyTicker() {
    const ticker = useRef<HTMLDivElement>(null)
    const [rates, setRates] = useState<SteamCurrencyRate[]>([])
    useEffect(() => observeSteamRates(setRates), [])
    useEffect(() => {if (ticker.current) return bindTickerVisibility(ticker.current)}, [])
    const repeatedRates = repeatTickerRates(rates)
    return <div ref={ticker} className="currency-ticker" aria-label="Расчётные курсы Steam">
        <div className="ticker-label">Steam rate</div>
        <div className="ticker-window">{rates.length ? <div className="ticker-track">
            {[0, 1].map(copy => <div className="ticker-group" aria-hidden={copy === 1} key={copy}>
                {repeatedRates.map((item, index) => <span className="ticker-item" key={`${item.currency}-${index}`} title={item.source ? `Источник: ${item.source}` : undefined}>
                    <b>{item.currency}</b><em>{item.rate.toFixed(2)}</em>
                </span>)}
            </div>)}
        </div> : <span className="ticker-empty">—</span>}</div>
    </div>
}

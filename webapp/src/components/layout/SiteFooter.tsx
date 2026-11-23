import logo from '../../assets/brand/youtrade-mark-transparent.png'
import icons from '../../../../shared/site/icons.svg?no-inline'
import {siteFooterDescription, siteFooterDisclaimer, siteFooterLinks, telegramBotUrl, telegramGroupUrl} from '../../../../shared/site/navigation'
import {SiteLink} from './SiteLink'
import '../../../../shared/site/footer.css'

export function SiteFooter() {
    return <footer className="site-footer site-control-surface--dark">
        <div className="site-footer-inner">
            <div className="site-footer-identity">
                <SiteLink className="site-footer-brand" href="/#start" aria-label="Главная Y.CS">
                    <img src={logo} alt=""/>
                </SiteLink>
                <a className="site-control site-control--quiet site-control--icon" href={telegramGroupUrl}
                    aria-label="Группа Y.CS в Telegram" title="Группа Y.CS в Telegram">
                    <svg aria-hidden="true"><use href={`${icons}#telegram`}/></svg>
                </a>
            </div>
            <div className="site-footer-bot">
                <a className="site-footer-link" href={telegramBotUrl}>Telegram-бот</a>
            </div>
            <div className="site-footer-legal">
                {siteFooterLinks.map(({href, label}) => <SiteLink className="site-footer-link" key={href} href={href}>{label}</SiteLink>)}
            </div>
            <div className="site-footer-notes">
                <p>{siteFooterDescription}</p>
                <small>{siteFooterDisclaimer}</small>
            </div>
        </div>
    </footer>
}

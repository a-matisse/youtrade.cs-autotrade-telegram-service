import icons from '../../../../shared/site/icons.svg?no-inline'
import './Toast.css'

export function Toast({title, message, onClose}: {title: string; message: string; onClose(): void}) {
    return <div className="site-toast" role="alert" aria-atomic="true" onKeyDown={event => {
        if (event.key === 'Escape') onClose()
    }}>
        <svg className="site-toast-status" aria-hidden="true"><use href={`${icons}#alert`}/></svg>
        <div><strong className="site-toast-title">{title}</strong><p className="site-toast-message">{message}</p></div>
        <button className="site-control site-control--quiet site-control--icon" type="button" aria-label="Закрыть уведомление" onClick={onClose}>
            <svg aria-hidden="true"><use href={`${icons}#close`}/></svg>
        </button>
    </div>
}

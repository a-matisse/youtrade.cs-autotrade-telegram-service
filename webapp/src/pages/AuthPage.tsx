import {useAuth} from '../auth/authState'
import {useLocation} from 'react-router-dom'
import {useEffect} from 'react'
import {useNotifications} from '../components/ui/notifications'
import telegramLogo from '../assets/brand/telegram.svg'
import {AuthOperationPreview} from '../components/auth/AuthOperationPreview'
import './AuthPage.css'

export function AuthPage({completing = false}: {completing?: boolean}) {
    const {login, error, loading} = useAuth()
    const {state, key} = useLocation()
    const loginError = error || (typeof state?.loginError === 'string' ? state.loginError : null)
    const notifications = useNotifications()
    useEffect(() => {
        if (!completing && loginError) notifications.error(loginError, 'Не удалось войти')
    }, [completing, loginError, key, notifications])
    return <main className="auth-page auth-page--split">
        <AuthOperationPreview/>
        <section className="auth-page-sign-in" aria-labelledby="auth-page-title">
            <div className="auth-page-form">
                <h1 id="auth-page-title">Войти в <span className="auth-page-wordmark">Y<span>.</span>CS</span></h1>
                <button className="site-control site-control--solid site-control--large auth-page-login" type="button" disabled={loading || completing} onClick={login}>
                    <img src={telegramLogo} width="24" height="24" alt=""/>
                    {completing ? 'Завершаем вход…' : 'Telegram'}
                </button>
                {completing && <div className="auth-page-progress" role="status"><div className="site-wait-line" aria-hidden="true"/><p>Проверяем вход и открываем ваш кабинет…</p></div>}
            </div>
        </section>
    </main>
}

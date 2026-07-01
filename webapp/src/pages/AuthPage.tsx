import {useCallback, useState} from 'react'
import {Link, useNavigate} from 'react-router-dom'
import {useAuth} from '../auth/authState'
import {TelegramLoginButton} from '../auth/TelegramLoginButton'
import type {TelegramAuthData} from '../api/types'

export function AuthPage() {
    const {login} = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);
    const [busy, setBusy] = useState(false)
    const onAuth = useCallback(async (data: TelegramAuthData) => {
        setBusy(true);
        setError(null);
        try {
            await login(data);
            navigate('/dashboard', {replace: true})
        } catch (e) {
            setError(e instanceof Error ? e.message : 'Ошибка авторизации')
        } finally {
            setBusy(false)
        }
    }, [login, navigate])
    return <main className="auth-screen"><Link className="brand floating" to="/"><span>YT</span>YouTrade</Link>
        <section className="auth-card">
            <div className="icon">↗</div>
            <h1>Вход в YouTrade</h1><p>Авторизуйтесь через официальный Telegram Login Widget. Backend проверит подпись и
            выдаст JWT.</p>{busy ? <div className="state">
            <div className="loader small"/>
            Выполняется вход…</div> : <TelegramLoginButton onAuth={onAuth}/>}{error &&
            <div className="notice error">{error}</div>}<small>Мы не получаем пароль от Telegram.</small></section>
    </main>
}

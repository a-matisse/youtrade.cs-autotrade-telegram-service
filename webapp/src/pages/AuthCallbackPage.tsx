import {Navigate} from 'react-router-dom'
import {useAuth} from '../auth/authState'
import {AuthPage} from './AuthPage'

export function AuthCallbackPage() {
  const {loading, user, token, error} = useAuth()
  if (loading) return <AuthPage completing/>
  return user && token ? <Navigate to="/dashboard" replace/>
    : <Navigate to="/auth" replace state={{loginError: error || 'Не удалось завершить вход. Начните вход через Telegram заново.'}}/>
}

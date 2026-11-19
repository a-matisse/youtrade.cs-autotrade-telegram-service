import {Navigate} from 'react-router-dom'
import {useAuth} from '../auth/authState'
import {AuthPage} from './AuthPage'
import {FullScreenLoading} from '../components/ui/States'

export function AuthCallbackPage() {
  const {loading, user, token} = useAuth()
  if (loading) return <FullScreenLoading/>
  return user && token ? <Navigate to="/dashboard" replace/> : <AuthPage/>
}

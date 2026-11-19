import { Navigate, Route, Routes } from 'react-router-dom'
import { PublicOnly, RequireAuth } from './routes/Guards'
import { LandingPage } from './pages/LandingPage'
import { AuthPage } from './pages/AuthPage'
import { AuthCallbackPage } from './pages/AuthCallbackPage'
import { DashboardPage } from './pages/DashboardPage'
import { NotFoundPage } from './pages/NotFoundPage'
import { OffersPage } from './pages/OffersPage'

export default function App() {
  return <Routes>
    <Route path="/" element={<LandingPage />} />
    <Route path="/terms" element={<OffersPage />} />
    <Route path="/offers" element={<Navigate to="/terms" replace />} />
    <Route element={<PublicOnly />}><Route path="/auth" element={<AuthPage />} /></Route>
    <Route path="/auth/callback" element={<AuthCallbackPage />} />
    <Route path="/connect-bot" element={<Navigate to="/dashboard" replace/>} />
    <Route element={<RequireAuth />}>
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/accounts" element={<Navigate to="/dashboard" replace />} />
      <Route path="/portfolio" element={<Navigate to="/dashboard" replace />} />
      <Route path="/parameters" element={<Navigate to="/dashboard" replace />} />
      <Route path="/dictionaries" element={<Navigate to="/dashboard" replace />} />
    </Route>
    <Route path="/home" element={<Navigate to="/dashboard" replace />} />
    <Route path="*" element={<NotFoundPage />} />
  </Routes>
}

import { Navigate, Route, Routes } from 'react-router-dom'
import { PublicOnly, RequireAuth, RequireLinkedBot } from './routes/Guards'
import { LandingPage } from './pages/LandingPage'
import { AuthPage } from './pages/AuthPage'
import { ConnectBotPage } from './pages/ConnectBotPage'
import { DashboardPage } from './pages/DashboardPage'
import { AccountsPage, DictionariesPage, ParametersPage, PortfolioPage } from './pages/DataPages'
import { NotFoundPage } from './pages/NotFoundPage'

export default function App() {
  return <Routes>
    <Route path="/" element={<LandingPage />} />
    <Route element={<PublicOnly />}><Route path="/auth" element={<AuthPage />} /></Route>
    <Route element={<RequireAuth />}><Route path="/connect-bot" element={<ConnectBotPage />} /></Route>
    <Route element={<RequireLinkedBot />}>
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/accounts" element={<AccountsPage />} />
      <Route path="/portfolio" element={<PortfolioPage />} />
      <Route path="/parameters" element={<ParametersPage />} />
      <Route path="/dictionaries" element={<DictionariesPage />} />
    </Route>
    <Route path="/home" element={<Navigate to="/dashboard" replace />} />
    <Route path="*" element={<NotFoundPage />} />
  </Routes>
}

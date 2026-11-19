import { StrictMode, useEffect } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import App from './App'
import './styles.css'
import {SiteLayout} from './components/layout/SiteLayout'
import {SiteThemeProvider} from './components/layout/SiteTheme'

function AppReady() {
  useEffect(() => { document.dispatchEvent(new Event('ycs:app-ready')) }, [])
  return null
}

createRoot(document.getElementById('root')!).render(<StrictMode><AppReady/><BrowserRouter><SiteThemeProvider><AuthProvider><SiteLayout><App /></SiteLayout></AuthProvider></SiteThemeProvider></BrowserRouter></StrictMode>)

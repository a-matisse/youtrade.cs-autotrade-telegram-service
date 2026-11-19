import { StrictMode, useEffect } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import App from './App'
import './styles.css'

function AppReady() {
  useEffect(() => { document.dispatchEvent(new Event('ycs:app-ready')) }, [])
  return null
}

createRoot(document.getElementById('root')!).render(<StrictMode><AppReady/><BrowserRouter><AuthProvider><App /></AuthProvider></BrowserRouter></StrictMode>)

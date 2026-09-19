import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
export default defineConfig(({mode}) => {
  const env = loadEnv(mode, '.', '')
  return {
    plugins: [react()],
    server: env.VITE_DEV_PROXY_TARGET ? {
      proxy: {'/api': {target: env.VITE_DEV_PROXY_TARGET, changeOrigin: true}},
    } : undefined,
  }
})

import { createLogger, defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
export default defineConfig(({mode}) => {
  const env = loadEnv(mode, '.', '')
  const target = env.VITE_DEV_PROXY_TARGET || 'https://youtradecs.xyz'
  return {
    plugins: [react()],
    build: {manifest: true},
    server: {
      proxy: {'/api': {
        target,
        changeOrigin: true,
        configure(proxy) {
          const logger = createLogger()
          proxy.on('proxyRes', (response, request) => {
            const path = request.url?.split('?')[0]
            logger.info(`[api] ${request.method} ${path} -> ${target} (${response.statusCode})`)
          })
        },
      }},
    },
  }
})

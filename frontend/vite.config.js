import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  define: { global: {} },
  server: {
    proxy: {
      '/ws-progress': {
        target: 'http://localhost:9002',
        ws: true,
        changeOrigin: true,
      }
    }
  }
})

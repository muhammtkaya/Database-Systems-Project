import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,      // backend CORS'ta izinli port
    strictPort: true, // 5173 doluysa baska porta kaymasin (CORS kirilmasin)
  },
})

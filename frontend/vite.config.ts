import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true
      }
    },
    hmr: {
      overlay: false
    },
    watch: {
      // 忽略特定的请求模式
      ignored: [
        '**/chrome-extension/**',
        '**/mfbcdcnpokpoajjciilocoachedjkima/**'
      ]
    }
  }
})

/*       target: 'http://localhost:5173', 
      configure: (proxy, options) => {
  // Additional proxy configuration if needed
}  */
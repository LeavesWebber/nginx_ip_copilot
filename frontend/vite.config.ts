import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5174,  // 指定固定端口
    strictPort: true,  // 如果端口被占用，不会自动尝试下一个可用端口
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true,
        secure: false,
        ws: true
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
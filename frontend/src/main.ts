import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import store from './store'

// 实际开发时，需要注释掉以下代码
/* if (import.meta.env.DEV) {
  await import('./api/mock')
} */

const app = createApp(App)

// Register Element Plus Icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.use(store)

// 添加控制台错误过滤
const originalConsoleError = console.error;
console.error = function(...args) {
    // 过滤掉Chrome扩展相关的错误
    const errorMessage = args.join(' ');
    if (errorMessage.includes('chrome-extension://') || 
        errorMessage.includes('mfbcdcnpokpoajjciilocoachedjkima')) {
        return;
    }
    originalConsoleError.apply(console, args);
};

app.mount('#app')
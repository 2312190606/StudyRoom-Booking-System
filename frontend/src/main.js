import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'

// 导入 Vant 核心组件和样式
import Vant from 'vant'
import 'vant/lib/index.css'

async function enableMocking() {
  if (process.env.NODE_ENV !== 'development') {
    return
  }
  const { worker } = await import('./mocks/browser')
  return worker.start({ onUnhandledRequest: 'bypass' })
}

enableMocking().then(() => {
  const app = createApp(App)
  app.use(router)
  app.use(Vant)
  app.mount('#app')
})

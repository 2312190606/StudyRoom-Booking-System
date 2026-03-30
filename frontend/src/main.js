import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

async function enableMocking() {
  if (process.env.NODE_ENV !== 'development') {
    return
  }
  const { worker } = await import('./mocks/browser')
  return worker.start({ onUnhandledRequest: 'bypass' })
}

enableMocking().then(() => {
  createApp(App).mount('#app')
})

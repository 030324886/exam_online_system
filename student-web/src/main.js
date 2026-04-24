import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 全局注册所有Element Plus图标（避免单个导入出错）
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from './router'
import App from './App.vue'

const app = createApp(App)

// ========== 新增：全局注册工具方法 ==========
// 1. 倒计时格式化方法（秒 → 时:分:秒）
app.config.globalProperties.$formatCountdown = (seconds) => {
  const h = Math.floor(seconds / 3600).toString().padStart(2, '0')
  const m = Math.floor((seconds % 3600) / 60).toString().padStart(2, '0')
  const s = (seconds % 60).toString().padStart(2, '0')
  return `${h}:${m}:${s}`
}

// 2. 防抖函数（交卷/提交防重复请求）
app.config.globalProperties.$debounce = (fn, delay = 500) => {
  let timer = null
  return (...args) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}
// ========== 新增结束 ==========

// 注册所有图标（核心：覆盖所有Element Plus图标，无需单独导入）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')

console.log('✅ 学生端系统启动成功 - Vue3 + Element Plus + Vite')
console.log('📌 当前环境：', import.meta.env.MODE)
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 全局注册所有Element Plus图标（避免单个导入出错）
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from './router'
import App from './App.vue'

const app = createApp(App)

// 注册所有图标（核心：覆盖所有Element Plus图标，无需单独导入）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')

console.log('✅ 学生端系统启动成功 - Vue3 + Element Plus + Vite')
console.log('📌 当前环境：', import.meta.env.MODE)
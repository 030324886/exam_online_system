import { createRouter, createWebHistory } from 'vue-router'
// 静态导入，确保路径100%正确
import Login from '@/views/student/Login.vue'
import Home from '@/views/student/Home.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/student/home', component: Home }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
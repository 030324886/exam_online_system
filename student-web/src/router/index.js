import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件（修正路径和文件名）
import Login from '../views/student/Login.vue' // 登录页
import StudentHome from '../views/student/Home.vue' // 学生主页面
import QuestionBank from '../views/student/QuestionBank.vue' // 题库列表
import DoExercise from '../views/student/DoExercise.vue' // 刷题页
import WrongQuestion from '../views/student/WrongQuestion.vue' // 错题本

// 定义路由规则
const routes = [
  {
    path: '/',
    redirect: '/login' // 默认跳转到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/student/home',
    name: 'StudentHome',
    component: StudentHome
  },
  {
    path: '/question/bank',
    name: 'QuestionBank',
    component: QuestionBank
  },
  {
    path: '/exercise/do/:chapterId?',
    name: 'DoExercise',
    component: DoExercise
  },
  {
    path: '/wrong/question',
    name: 'WrongQuestion',
    component: WrongQuestion
  },
  // 404页面（可选）
  {
    path: '/:pathMatch(.*)*',
    redirect: '/student/home'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 简单的路由守卫（可选，验证是否登录）
router.beforeEach((to, from, next) => {
  // 模拟登录状态
  const isLogin = localStorage.getItem('student_token') || true

  // 如果不是登录页且未登录，跳转到登录页
  if (to.path !== '/login' && !isLogin) {
    next('/login')
  } else {
    next()
  }
})

export default router
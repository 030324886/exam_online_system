import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

// 路由配置
const routes = [
  // 登录页（白名单，无需登录）
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录', isPublic: true }
  },
  // 全局布局容器（所有需登录页面的父路由）
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/Index.vue'),
    // 登录后自动跳对应角色首页
    redirect: () => {
      const userStore = useUserStore()
      return userStore.isTeacher ? '/teacher/home' : '/admin/home'
    },
    children: [
      // 教师端首页
      {
        path: '/teacher/home',
        name: 'TeacherHome',
        component: () => import('@/views/teacher/Home.vue'),
        meta: { title: '教师首页', role: 'teacher' }
      },
      // 教务端首页
      {
        path: '/admin/home',
        name: 'AdminHome',
        component: () => import('@/views/admin/Home.vue'),
        meta: { title: '教务首页', role: 'admin' }
      }
    ]
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在', isPublic: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫：权限拦截核心
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 1. 白名单页面直接放行
  if (to.meta.isPublic) {
    // 已登录用户访问登录页，自动跳对应首页
    if (to.path === '/login' && userStore.isLoggedIn) {
      next(userStore.isTeacher ? '/teacher/home' : '/admin/home')
    }
    next()
    return
  }

  // 2. 非白名单页面：检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }

  // 3. 检查角色权限：仅对应角色可访问对应页面
  if (to.meta.role && to.meta.role !== userStore.role) {
    ElMessage.error('无权限访问该页面')
    next(userStore.isTeacher ? '/teacher/home' : '/admin/home')
    return
  }

  // 4. 所有检查通过，放行
  next()
})

export default router
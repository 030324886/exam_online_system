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
      },
      // 试题管理
      {
        path: '/teacher/question',
        name: 'QuestionList',
        component: () => import('@/views/teacher/question/QuestionList.vue'),
        meta: { title: '试题管理', role: 'teacher' }
      },
      {
        path: '/teacher/question/add',
        name: 'QuestionAdd',
        component: () => import('@/views/teacher/question/QuestionAdd.vue'),
        meta: { title: '新增试题', role: 'teacher' }
      },
      {
        path: '/teacher/question/edit/:id',
        name: 'QuestionEdit',
        component: () => import('@/views/teacher/question/QuestionEdit.vue'),
        meta: { title: '编辑试题', role: 'teacher' }
      },
      // 手动组卷
      {
        path: '/teacher/paper/manual',
        name: 'ManualPaper',
        component: () => import('@/views/teacher/paper/ManualPaper.vue'),
        meta: { title: '手动组卷', role: 'teacher' }
      },
      // 试卷管理列表
      {
        path: '/teacher/paper',
        name: 'PaperList',
        component: () => import('@/views/teacher/paper/PaperList.vue'),
        meta: { title: '试卷管理', role: 'teacher' }
      },
      // 试卷预览详情
      {
        path: '/teacher/paper/detail/:id',
        name: 'PaperDetail',
        component: () => import('@/views/teacher/paper/PaperDetail.vue'),
        meta: { title: '试卷预览', role: 'teacher' }
      },
      // 考试记录
      {
        path: '/teacher/exam/record',
        name: 'ExamRecordList',
        component: () => import('@/views/teacher/exam/ExamRecordList.vue'),
        meta: { title: '考试记录', role: 'teacher' }
      },
      // 阅卷/考试详情
      {
        path: '/teacher/exam/detail/:id',
        name: 'ExamDetail',
        component: () => import('@/views/teacher/exam/ExamDetail.vue'),
        meta: { title: '阅卷', role: 'teacher' }
      },
      // 成绩统计
      {
        path: '/teacher/exam/stats',
        name: 'ScoreStats',
        component: () => import('@/views/teacher/exam/ScoreStats.vue'),
        meta: { title: '成绩统计', role: 'teacher' }
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
import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 导入组件（保留不变）
import Login from '../views/student/Login.vue'
import StudentHome from '../views/student/Home.vue'
import QuestionBank from '../views/student/QuestionBank.vue'
import DoExercise from '../views/student/DoExercise.vue'
import WrongQuestion from '../views/student/WrongQuestion.vue'
import LearningStats from '../views/student/LearningStats.vue'
import ExamPage from '../views/student/ExamPage.vue'
import ExamList from '../views/student/ExamList.vue'
import ScorePage from '../views/student/ScorePage.vue'

// 路由规则（保留不变）
const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '学生登录' }
  },
  {
    path: '/student/home',
    name: 'StudentHome',
    component: StudentHome,
    meta: { title: '学生首页' }
  },
  {
    path: '/question/bank',
    name: 'QuestionBank',
    component: QuestionBank,
    meta: { title: '题库列表' }
  },
  {
    path: '/exercise/do/:chapterId?',
    name: 'DoExercise',
    component: DoExercise,
    meta: { title: '在线刷题' }
  },
  {
    path: '/wrong/question',
    name: 'WrongQuestion',
    component: WrongQuestion,
    meta: { title: '我的错题本' }
  },
  {
    path: '/learning/stats',
    name: 'LearningStats',
    component: LearningStats,
    meta: { title: '学习数据统计' }
  },
  {
    path: '/student/exam/:id?',
    name: 'ExamPage',
    component: ExamPage,
    meta: { title: '在线模考' }
  },
  {
    path: '/student/exam-list',
    name: 'ExamList',
    component: ExamList,
    meta: { title: '选择模考' }
  },
  {
    path: '/student/score/:id?',
    name: 'ScorePage',
    component: ScorePage,
    meta: { title: '考试成绩' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: (to) => {
      const isLogin = localStorage.getItem('student_token') || false
      return isLogin ? '/student/home' : '/login'
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 核心修改：移除开发环境的默认登录逻辑，严格验证token
router.beforeEach((to, from, next) => {
  // 所有环境都严格验证student_token，没有token就是未登录
  const isLogin = localStorage.getItem('student_token') || false

  // 未登录拦截（仅允许访问登录页）
  if (!isLogin && to.name !== 'Login') {
    ElMessage.warning('请先登录后再访问！')
    next({ name: 'Login' })
    return
  }

  // 已登录时，禁止回退到登录页
  if (isLogin && to.name === 'Login') {
    ElMessage.info('您已登录，自动跳转到首页')
    next({ name: 'StudentHome' })
    return
  }

  // 设置页面标题
  document.title = to.meta.title
    ? `${to.meta.title} - 在线刷题系统`
    : '在线刷题系统'

  next()
})

export default router
import { defineStore } from 'pinia'
import router from '@/router'

export const useUserStore = defineStore('user', {
  // 状态定义
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    role: localStorage.getItem('role') || '' // 角色：teacher/admin
  }),
  // 操作方法
  actions: {
    // 登录：保存状态并持久化
    login(token, userInfo, role) {
      this.token = token
      this.userInfo = userInfo
      this.role = role
      // 持久化到localStorage，刷新不丢失
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      localStorage.setItem('role', role)
    },
    // 退出登录：清除状态
    logout() {
      this.token = ''
      this.userInfo = {}
      this.role = ''
      localStorage.clear()
      router.push('/login')
    }
  },
  // 计算属性
  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!state.token,
    // 是否为教师
    isTeacher: (state) => state.role === 'teacher',
    // 是否为教务
    isAdmin: (state) => state.role === 'admin'
  }
})
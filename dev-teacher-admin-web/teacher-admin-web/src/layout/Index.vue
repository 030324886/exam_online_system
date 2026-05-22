<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">在线考试系统</div>
      <el-menu :default-active="activeMenu" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item v-for="menu in filteredMenus" :key="menu.path" :index="menu.path">
          <el-icon><component :is="menu.icon || 'House'" /></el-icon>
          <span>{{ menu.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left"><h2>在线考试系统</h2></div>
        <div class="header-right">
          <span>{{ userStore.userInfo?.name || userStore.userInfo?.username || '用户' }}</span>
          <el-tag size="small" type="info">{{ userStore.role === 'teacher' ? '教师' : '教务' }}</el-tag>
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox, ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()

const teacherMenus = [
  { path: '/teacher/home', title: '首页', icon: 'House' },
  { path: '/teacher/question', title: '试题管理', icon: 'Document' },
  { path: '/teacher/paper/manual', title: '手动组卷', icon: 'Edit' },
  { path: '/teacher/paper', title: '试卷管理', icon: 'List' },
  { path: '/teacher/exam/record', title: '考试记录', icon: 'Notebook' },
  { path: '/teacher/exam/stats', title: '成绩统计', icon: 'DataAnalysis' }
]

const adminMenus = [
  { path: '/admin/home', title: '首页', icon: 'House' }
]

const filteredMenus = computed(() => {
  return userStore.isTeacher ? teacherMenus : adminMenus
})

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    .then(() => { userStore.logout(); ElMessage.success('已退出') })
    .catch(() => {})
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.aside { background-color: #304156; overflow-y: auto; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: 600; background: rgba(0,0,0,0.2); }
.header { background: #fff; box-shadow: 0 1px 4px rgba(0,21,41,0.08); display: flex; justify-content: space-between; align-items: center; padding: 0 20px; }
.header-left h2 { font-size: 18px; color: #303133; }
.header-right { display: flex; align-items: center; gap: 12px; }
.main { background-color: #f0f2f5; min-height: calc(100vh - 60px); }
</style>

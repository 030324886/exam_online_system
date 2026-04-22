<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="aside">
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <!-- 动态生成菜单，按角色过滤 -->
        <el-menu-item
          v-for="menu in filteredMenus"
          :key="menu.path"
          :index="menu.path"
        >
          <el-icon>
            <component :is="menu.meta.icon || 'House'" />
          </el-icon>
          <span>{{ menu.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <h2>在线考试系统</h2>
        </div>
        <div class="header-right">
          <span>欢迎，{{ userStore.userInfo.username || '用户' }}</span>
          <el-button type="primary" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <!-- 页面内容 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import router from '@/router'

const route = useRoute()
const userStore = useUserStore()

// 所有菜单配置（手动定义，精准控制显示）
const allMenus = computed(() => [
  { path: '/teacher/home', meta: { title: '首页', icon: 'House', role: 'teacher' }},
  { path: '/teacher/question', meta: { title: '试题管理', icon: 'Document', role: 'teacher' }},
  { path: '/teacher/paper/manual', meta: { title: '手动组卷', icon: 'Edit', role: 'teacher' }},
  { path: '/admin/home', meta: { title: '首页', icon: 'House', role: 'admin' }}
])

// 按角色过滤菜单
const filteredMenus = computed(() => {
  return allMenus.value.filter(
    item =>
      item.meta?.role === userStore.role && // 仅显示当前角色菜单
      item.path && // 过滤空路径
      !item.path.includes(':pathMatch') // 过滤404页面
  )
})

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #304156;
}
.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}
.main {
  background-color: #f5f5f5;
}
</style>
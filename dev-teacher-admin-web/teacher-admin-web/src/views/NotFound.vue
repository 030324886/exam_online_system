<template>
  <div class="not-found-box">
    <h1>404</h1>
    <h2>页面不存在</h2>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const goHome = () => {
  if (userStore.isTeacher) {
    router.push('/teacher/home')
  } else if (userStore.isAdmin) {
    router.push('/admin/home')
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
.not-found-box {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
}
</style>
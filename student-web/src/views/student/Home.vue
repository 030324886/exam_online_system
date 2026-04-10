<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">学生端管理系统</div>
      <div class="header-right">
        <el-button type="primary" text @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="main-content">
      <h1 class="welcome-title">欢迎回来，同学！</h1>
      <p class="welcome-desc">第一周任务：学生端骨架搭建完成 ✅</p>

      <!-- 功能入口卡片 -->
      <el-row :gutter="20" class="card-row">
        <el-col :span="8">
          <el-card shadow="hover" class="func-card">
            <div class="card-title">题库刷题</div>
            <div class="card-desc">进入题库，开始刷题练习</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="func-card">
            <div class="card-title">在线模考</div>
            <div class="card-desc">参加模拟考试，检测学习水平</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="func-card">
            <div class="card-title">我的错题本</div>
            <div class="card-desc">查看错题，针对性复习巩固</div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 退出登录逻辑
const handleLogout = () => {
  localStorage.removeItem('token')
  ElMessage.success('退出登录成功')
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
}

.header {
  height: 60px;
  line-height: 60px;
  padding: 0 20px;
  background: #1989fa;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  font-size: 18px;
  font-weight: bold;
}

.main-content {
  flex: 1;
  padding: 30px;
  background: #f5f7fa;
}

.welcome-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.welcome-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
}

.card-row {
  margin-top: 20px;
}

.func-card {
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.func-card:hover {
  transform: translateY(-5px);
}

.card-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.card-desc {
  color: #666;
  font-size: 14px;
}
</style>
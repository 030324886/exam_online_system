<template>
  <div class="student-home-page">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="title">在线刷题系统</h1>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">学生用户</span>
          <el-button
            type="text"
            icon="el-icon-switch-button"
            class="logout-btn"
            @click="logout"
          >退出登录</el-button>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 功能入口卡片 -->
      <el-row :gutter="20" class="card-row">
        <!-- 题库刷题卡片 -->
        <el-col :span="8">
          <el-card
            class="function-card"
            shadow="hover"
            @click="goToQuestionBank"
          >
            <div class="card-icon">
              <el-icon size="48"><Document /></el-icon>
            </div>
            <div class="card-title">题库刷题</div>
            <div class="card-desc">选择章节，开始练习</div>
          </el-card>
        </el-col>

        <!-- 错题本卡片 -->
        <el-col :span="8">
          <el-card
            class="function-card"
            shadow="hover"
            @click="goToWrongQuestion"
          >
            <div class="card-icon">
              <el-icon size="48"><CircleClose /></el-icon>
            </div>
            <div class="card-title">我的错题本</div>
            <div class="card-desc">查看和复习错题</div>
          </el-card>
        </el-col>

        <!-- 学习统计卡片 -->
        <el-col :span="8">
          <el-card class="function-card" shadow="hover">
            <div class="card-icon">
              <el-icon size="48"><DataBoard /></el-icon>
            </div>
            <div class="card-title">学习统计</div>
            <div class="card-desc">查看学习记录和进度</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 学习进度统计 -->
      <div class="stats-container">
        <el-card class="stats-card" shadow="hover">
          <h3 class="stats-title">学习进度</h3>
          <el-row :gutter="30" class="stats-row">
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-value">{{ totalQuestions }}</div>
                <div class="stat-label">总答题数</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-value">{{ correctRate }}%</div>
                <div class="stat-label">正确率</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-value">{{ wrongCount }}</div>
                <div class="stat-label">错题数</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>
    </div>

    <!-- 背景装饰（和登录页一致） -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, CircleClose, DataBoard } from '@element-plus/icons-vue'

const router = useRouter()

// 模拟学习数据
const totalQuestions = ref(128)
const correctRate = ref(85)
const wrongCount = ref(19)

// 跳转题库页面
const goToQuestionBank = () => {
  router.push('/question/bank')
}

// 跳转错题本页面
const goToWrongQuestion = () => {
  router.push('/wrong/question')
}

// 退出登录
const logout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      ElMessage.success('退出登录成功！')
      // 实际项目中清除token并跳转登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      router.push('/student/login')
    }).catch(() => {
      ElMessage.info('已取消退出登录')
    })
  } catch (error) {
    ElMessage.error('操作失败，请重试！')
    console.error('退出登录错误：', error)
  }
}
</script>

<style scoped>
/* 基础布局 - 对齐登录页渐变背景 */
.student-home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 背景装饰（和登录页完全一致） */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  animation: float 8s infinite ease-in-out;
  z-index: 0;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  bottom: 50%;
  right: 30%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(30px) rotate(180deg);
  }
}

/* 顶部导航 - 适配登录页风格 */
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  color: #303133;
  padding: 0 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 10;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 36px;
  height: 36px;
  --el-avatar-bg-color: #e0f7ff;
  --el-avatar-text-color: #409eff;
}

.username {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.logout-btn {
  color: #409eff;
  font-weight: 500;
  transition: color 0.3s;
}

.logout-btn:hover {
  color: #66b1ff;
}

/* 主内容区 */
.main-content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  position: relative;
  z-index: 10;
}

.card-row {
  margin-bottom: 30px;
}

/* 功能卡片 - 对齐登录页卡片样式 */
.function-card {
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.function-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.card-icon {
  color: #409eff;
  margin-bottom: 15px;
  transition: color 0.3s;
}

.function-card:hover .card-icon {
  color: #66b1ff;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 5px;
  color: #303133;
}

.card-desc {
  font-size: 14px;
  color: #909399;
}

/* 统计卡片 - 对齐登录页样式 */
.stats-container {
  margin-top: 20px;
}

.stats-card {
  padding: 20px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stats-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px 0;
  color: #303133;
}

.stats-row {
  text-align: center;
}

.stat-item {
  padding: 10px;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 适配Element Plus组件样式穿透 */
:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-button--text) {
  font-size: 14px;
}
</style>
<template>
  <div class="score-page">
    <!-- 顶部导航（和考试页/题库页一致，新增返回按钮） -->
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <span class="back-btn" @click="goBack">返回</span>
          <h1 class="title">在线刷题系统 - 考试成绩</h1>
        </div>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">学生用户</span>
        </div>
      </div>
    </div>

    <!-- 主要内容（复用首页布局） -->
    <div class="main-content">
      <!-- 加载状态提示（复用全局样式） -->
      <div class="loading-tip stats-card" v-if="isLoading">加载成绩数据中...</div>

      <!-- 非加载状态：统一包裹，避免v-else孤立 -->
      <div v-else>
        <!-- 成绩卡片（复用首页功能卡片样式） -->
        <div class="score-card function-card">
          <div class="score-header">考试成绩详情</div>
          <div class="score-item">
            <span class="score-label">考试名称</span>
            <span class="score-value">{{ scoreData.examName }}</span>
          </div>
          <div class="score-item">
            <span class="score-label">得分</span>
            <span class="score-value">{{ scoreData.score }} 分</span>
          </div>
          <div class="score-item">
            <span class="score-label">总分</span>
            <span class="score-value">{{ scoreData.totalScore }} 分</span>
          </div>
          <div class="score-item">
            <span class="score-label">正确率</span>
            <span class="score-value">{{ scoreData.correctRate }}%</span>
          </div>
          <div class="score-item">
            <span class="score-label">考试用时</span>
            <span class="score-value">{{ scoreData.useTime }}</span>
          </div>
          <div class="score-item">
            <span class="score-label">考试时间</span>
            <span class="score-value">{{ scoreData.examTime }}</span>
          </div>
        </div>

        <!-- 答题统计卡片（复用首页功能卡片样式） -->
        <div class="score-card function-card">
          <div class="score-header">答题统计</div>
          <div class="score-item">
            <span class="score-label">答对题目数</span>
            <span class="score-value">{{ scoreData.correctNum }} 道</span>
          </div>
          <div class="score-item">
            <span class="score-label">答错题目数</span>
            <span class="score-value">{{ scoreData.errorNum }} 道</span>
          </div>
          <div class="score-item">
            <span class="score-label">未答题目数</span>
            <span class="score-value">{{ scoreData.unansweredNum }} 道</span>
          </div>
        </div>

        <!-- 返回按钮（复用首页按钮风格，和考试页/题库页一致） -->
        <div class="btn-group">
          <el-button
            class="back-btn"
            @click="goBack"
            style="width: 200px; height: 40px; font-size: 16px; border-radius: 16px;"
          >
            返回首页
          </el-button>
        </div>
      </div>
    </div>

    <!-- 背景装饰（和首页/考试页完全一致） -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isLoading = ref(true)

// 初始化默认模拟数据（优先使用，彻底避免空值）
const scoreData = ref({
  examName: 'Vue3在线刷题考试',
  score: 85,
  totalScore: 100,
  correctRate: 85.0,
  useTime: '28分30秒',
  examTime: new Date().toLocaleString(),
  correctNum: 17,
  errorNum: 2,
  unansweredNum: 1
})

// 获取成绩数据（强制使用模拟数据，屏蔽真实接口）
onMounted(async () => {
  try {
    isLoading.value = true
    // 1. 强制使用模拟数据（注释所有真实接口请求）
    console.log('使用模拟成绩数据，接口未开发')

    // ========== 真实接口（后端完成后启用）==========
    // const examId = route.params.examId || '1' // 强制赋默认值，避免参数无效
    // const res = await window.$axios.get(`/api/score/detail/${examId}`)
    // if (res.data.code === 200) {
    //   // 格式化正确率（避免NaN）
    //   const data = res.data.data
    //   data.correctRate = data.correctRate ? (data.correctRate * 100).toFixed(1) : 0.0
    //   scoreData.value = data
    // }
    // =============================================
  } catch (error) {
    console.error('成绩接口请求失败（预期内，接口未开发）：', error)
    ElMessage.info('接口暂未开发，已显示模拟成绩数据')
  } finally {
    isLoading.value = false
  }
})

// 返回首页：确保路由name正确
const goBack = () => {
  // 优先用name跳转，避免路径错误；若StudentHome不对，改为你实际的首页name
  router.push({ name: 'StudentHome' }).catch(err => {
    // 兜底：路径跳转
    router.push('/student/home')
  })
}
</script>

<style scoped>
/* 基础布局 - 完全复用首页渐变背景+布局 */
.score-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 背景装饰 - 和首页/考试页完全一致 */
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

/* 顶部导航 - 完全复用考试页/题库页样式 */
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

/* 左侧返回按钮+标题布局，和考试页/题库页对齐 */
.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 返回按钮样式和题库页/考试页完全一致 */
.back-btn {
  color: #409eff;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.3s;
}

.back-btn:hover {
  color: #66b1ff;
  text-decoration: underline;
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

/* 主内容区 - 复用首页样式 */
.main-content {
  max-width: 800px;
  margin: 30px auto;
  padding: 0 20px;
  position: relative;
  z-index: 10;
}

/* 加载提示 - 复用全局stats-card样式 */
.loading-tip {
  text-align: center;
  padding: 50px;
  color: #999;
  font-size: 16px;
  margin-bottom: 20px;
}

/* 成绩卡片 - 复用首页function-card样式 */
.score-card {
  padding: 20px;
  margin-bottom: 20px;
  height: auto;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  cursor: default;
  transition: all 0.3s ease;
}

.score-header {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  width: 100%;
}

.score-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  width: 100%;
}

.score-item:last-child {
  border-bottom: none;
}

.score-label {
  color: #666;
  font-size: 14px;
}

.score-value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

/* 按钮容器 - 复用全局样式 */
.btn-group {
  margin-top: 20px;
  text-align: center;
  position: relative;
  z-index: 10;
}

/* 复用全局卡片/按钮样式（穿透） */
:deep(.function-card) {
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.stats-card) {
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.el-button) {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
  transition: all 0.3s;
}

:deep(.el-button:hover) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

/* 响应式适配 - 完全复用首页/考试页规则 */
@media (max-width: 768px) {
  .score-page .main-content {
    padding: 0 10px;
    max-width: 100%;
  }
  .score-card {
    width: 100% !important;
    margin: 10px 0 !important;
  }
  .header-left .title {
    font-size: 16px;
  }
  .back-btn {
    font-size: 14px;
  }
  .score-header {
    font-size: 16px;
  }
  .score-label, .score-value {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .btn-group .el-button {
    width: 100% !important;
    margin-bottom: 10px !important;
  }
  .header-content .title {
    font-size: 14px;
  }
}
</style>
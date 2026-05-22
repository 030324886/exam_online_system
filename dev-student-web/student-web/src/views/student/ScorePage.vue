<template>
  <div class="score-page">
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <span class="back-btn" @click="goBack">返回</span>
          <h1 class="title">在线刷题系统 - 考试成绩</h1>
        </div>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">{{ username }}</span>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="loading-tip stats-card" v-if="isLoading">加载成绩数据中...</div>

      <div v-else>
        <div class="score-card function-card">
          <div class="score-header">考试成绩详情</div>
          <div class="score-item"><span class="score-label">考试名称</span><span class="score-value">{{ scoreData.examName }}</span></div>
          <div class="score-item"><span class="score-label">得分</span><span class="score-value highlight">{{ scoreData.score }} 分</span></div>
          <div class="score-item"><span class="score-label">总分</span><span class="score-value">{{ scoreData.totalScore }} 分</span></div>
          <div class="score-item"><span class="score-label">正确率</span><span class="score-value">{{ scoreData.correctRate }}%</span></div>
          <div class="score-item"><span class="score-label">考试用时</span><span class="score-value">{{ scoreData.useTime }}</span></div>
          <div class="score-item"><span class="score-label">考试时间</span><span class="score-value">{{ scoreData.examTime }}</span></div>
        </div>

        <div class="score-card function-card" v-if="scoreData.correctNum > 0 || scoreData.errorNum > 0 || scoreData.unansweredNum > 0">
          <div class="score-header">答题统计</div>
          <div class="score-item"><span class="score-label">答对题目数</span><span class="score-value success">{{ scoreData.correctNum }} 道</span></div>
          <div class="score-item"><span class="score-label">答错题目数</span><span class="score-value danger">{{ scoreData.errorNum }} 道</span></div>
          <div class="score-item"><span class="score-label">未答题目数</span><span class="score-value">{{ scoreData.unansweredNum }} 道</span></div>
        </div>

        <div class="btn-group">
          <el-button class="back-btn" @click="goBack"
            style="width: 200px; height: 40px; font-size: 16px; border-radius: 16px;">
            返回首页
          </el-button>
        </div>
      </div>
    </div>

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
import request from '../../api/request'

const route = useRoute()
const router = useRouter()
const username = ref(localStorage.getItem('username') || '学生用户')
const isLoading = ref(true)

const scoreData = ref({
  examName: '在线模考', score: 0, totalScore: 0, correctRate: 0,
  useTime: '0分0秒', examTime: '', correctNum: 0, errorNum: 0, unansweredNum: 0
})

onMounted(async () => {
  try {
    isLoading.value = true
    const submissionId = route.params.id || ''

    // 如果有 submissionId，直接请求后端获取详情
    if (submissionId) {
      try {
        const detailRes = await request.get(`/exam/record/detail/${submissionId}`)
        if (detailRes.success !== false) {
          const detail = detailRes.data || detailRes
          const record = detail.record || {}
          const answers = detail.answers || []

          scoreData.value.examName = record.paperName || (JSON.parse(localStorage.getItem('lastExamResult') || '{}')).examName || '在线模考'
          scoreData.value.score = record.totalScore || record.objectiveScore || 0
          scoreData.value.totalScore = record.totalScore || 100
          scoreData.value.useTime = (JSON.parse(localStorage.getItem('lastExamResult') || '{}')).useTime || '0分0秒'
          scoreData.value.examTime = new Date().toLocaleString()

          const totalQuestions = answers.length
          if (totalQuestions > 0) {
            const correctCount = answers.filter(a => a.isCorrect === 1 || a.isCorrect === true).length
            const answeredCount = answers.filter(a => a.answerText && a.answerText !== '').length
            const unansweredCount = totalQuestions - answeredCount
            scoreData.value.correctNum = correctCount
            scoreData.value.errorNum = answeredCount - correctCount
            scoreData.value.unansweredNum = Math.max(0, unansweredCount)
            scoreData.value.correctRate = totalQuestions > 0 ? ((correctCount / totalQuestions) * 100).toFixed(1) : 0
          }
          localStorage.removeItem('lastExamResult')
          return
        }
      } catch { /* fallback to cache */ }
    }

    // 降级：从 localStorage 读取
    const cached = localStorage.getItem('lastExamResult')
    if (cached) {
      const parsed = JSON.parse(cached)
      scoreData.value.examName = parsed.examName || '在线模考'
      scoreData.value.score = parsed.score || 0
      scoreData.value.totalScore = parsed.totalScore || 100
      scoreData.value.correctRate = parsed.totalScore > 0 ? ((parsed.score / parsed.totalScore) * 100).toFixed(1) : 0
      scoreData.value.useTime = parsed.useTime || '0分0秒'
      scoreData.value.examTime = new Date().toLocaleString()
      localStorage.removeItem('lastExamResult')
    }
  } catch (error) {
    ElMessage.error('获取成绩数据失败')
  } finally {
    isLoading.value = false
  }
})

const goBack = () => router.push({ name: 'StudentHome' }).catch(() => router.push('/student/home'))
</script>

<style scoped>
.score-page { min-height: 100vh; background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%); position: relative; overflow: hidden; }
.bg-decoration { position: absolute; width: 100%; height: 100%; top: 0; left: 0; z-index: 0; pointer-events: none; }
.circle { position: absolute; border-radius: 50%; background: rgba(255, 255, 255, 0.4); animation: float 8s infinite ease-in-out; z-index: 0; }
.circle-1 { width: 300px; height: 300px; top: -100px; left: -100px; animation-delay: 0s; }
.circle-2 { width: 200px; height: 200px; bottom: -50px; right: -50px; animation-delay: 2s; }
.circle-3 { width: 150px; height: 150px; bottom: 50%; right: 30%; animation-delay: 4s; }
@keyframes float { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(30px) rotate(180deg); } }
.header { background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); padding: 0 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); position: relative; z-index: 10; }
.header-content { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; height: 60px; }
.header-left { display: flex; align-items: center; gap: 15px; }
.back-btn { color: #409eff; font-size: 16px; cursor: pointer; }
.back-btn:hover { color: #66b1ff; text-decoration: underline; }
.title { font-size: 20px; font-weight: 600; margin: 0; color: #409eff; }
.user-info { display: flex; align-items: center; gap: 10px; }
.avatar { width: 36px; height: 36px; --el-avatar-bg-color: #e0f7ff; --el-avatar-text-color: #409eff; }
.username { font-size: 14px; color: #606266; font-weight: 500; }
.main-content { max-width: 800px; margin: 30px auto; padding: 0 20px; position: relative; z-index: 10; }
.loading-tip { text-align: center; padding: 50px; color: #999; font-size: 16px; margin-bottom: 20px; }
.score-card { padding: 20px; margin-bottom: 20px; height: auto; display: flex; flex-direction: column; align-items: flex-start; cursor: default; transition: all 0.3s ease; }
.score-header { font-size: 18px; font-weight: 600; margin-bottom: 15px; color: #333; padding-bottom: 10px; border-bottom: 1px solid #eee; width: 100%; }
.score-item { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f5f5f5; width: 100%; }
.score-item:last-child { border-bottom: none; }
.score-label { color: #666; font-size: 14px; }
.score-value { color: #333; font-size: 14px; font-weight: 500; }
.highlight { color: #409eff; font-size: 18px; font-weight: 700; }
.success { color: #67c23a; }
.danger { color: #f56c6c; }
.btn-group { margin-top: 20px; text-align: center; position: relative; z-index: 10; }
:deep(.function-card) { border-radius: 16px; backdrop-filter: blur(10px); background: rgba(255, 255, 255, 0.95); box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); border: 1px solid rgba(255, 255, 255, 0.2); }
@media (max-width: 768px) { .score-page .main-content { padding: 0 10px; max-width: 100%; } }
</style>

<template>
  <div class="learning-stats-page">
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <span class="back-btn" @click="goBack">返回</span>
          <h1 class="title">在线刷题系统 - 学习统计</h1>
        </div>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">{{ username }}</span>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="filter-card stats-card">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
              start-placeholder="开始日期" end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD"
              style="width: 100%;"></el-date-picker>
          </el-col>
          <el-col :span="12">
            <el-button type="primary" @click="fetchStats" style="width: 100%; border-radius: 8px;">
              刷新数据
            </el-button>
          </el-col>
        </el-row>
      </div>

      <div class="core-stats stats-card">
        <h3 class="stats-title">核心学习数据</h3>
        <el-row :gutter="30">
          <el-col :span="6">
            <div class="core-item">
              <div class="core-value">{{ totalQuestions }}</div>
              <div class="core-label">系统总题数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="core-item">
              <div class="core-value">{{ knowledgeCoverage }}</div>
              <div class="core-label">知识点覆盖</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="core-item">
              <div class="core-value">{{ totalWrong }}</div>
              <div class="core-label">累计错题</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="core-item">
              <div class="core-value">{{ passedExams }}</div>
              <div class="core-label">考试成绩</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <div class="chart-card function-card">
            <h4 class="chart-title">各知识点错题分布</h4>
            <div id="wrongDistributionChart" class="chart-container"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="chart-card function-card">
            <h4 class="chart-title">薄弱知识点</h4>
            <div id="weakKnowledgeChart" class="chart-container"></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import request2 from '../../api/request2'
import request from '../../api/request'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '学生用户')

const dateRange = ref([])
const totalQuestions = ref(0)
const knowledgeCoverage = ref(0)
const totalWrong = ref(0)
const passedExams = ref(0)

let wrongChart = null
let weakChart = null

const fetchStats = async () => {
  try {
    const userId = localStorage.getItem('userId')

    const [questionRes, wrongRes, weakRes] = await Promise.allSettled([
      request2.get('/question/list'),
      userId ? request2.get(`/wrong/stats/knowledge/${userId}`) : Promise.resolve({ value: { code: 500 } }),
      request.get('/statistics/weak-knowledge')
    ])

    if (questionRes.status === 'fulfilled' && questionRes.value?.code === 200) {
      const questions = questionRes.value.data || []
      totalQuestions.value = questions.length
      const points = new Set(questions.filter(q => q.knowledgePoint).map(q => q.knowledgePoint))
      knowledgeCoverage.value = points.size
    }

    if (wrongRes.status === 'fulfilled' && wrongRes.value?.code === 200) {
      const stats = wrongRes.value.data || []
      totalWrong.value = stats.reduce((s, i) => s + (i.wrongCount || 0), 0)

      const wrongDom = document.getElementById('wrongDistributionChart')
      if (wrongDom && wrongChart) {
        wrongChart.setOption({
          series: [{
            data: stats.map(s => ({
              value: s.wrongCount || 0,
              name: s.knowledgePoint || '未知'
            }))
          }]
        })
      }
    }

    if (weakRes.status === 'fulfilled' && weakRes.value?.success !== false) {
      const data = weakRes.value?.data
      if (data?.items) {
        const weakDom = document.getElementById('weakKnowledgeChart')
        if (weakDom && weakChart) {
          weakChart.setOption({
            xAxis: { data: data.items.map(i => i.knowledgePoint || '未知') },
            series: [{ data: data.items.map(i => (i.wrongRate || 0).toFixed(1)) }]
          })
        }
      }
    }

    ElMessage.success('数据刷新成功')
  } catch (err) {
    console.error('获取统计数据失败：', err)
  }
}

const initCharts = () => {
  const wrongDom = document.getElementById('wrongDistributionChart')
  if (wrongDom) {
    wrongChart = echarts.init(wrongDom)
    wrongChart.setOption({
      title: { show: false },
      tooltip: { trigger: 'item', formatter: '{b}：{c} 次' },
      legend: { bottom: 0, left: 'center', textStyle: { color: '#606266' } },
      series: [{
        name: '错题数',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: [{ value: 0, name: '暂无数据' }],
        color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
      }]
    })
  }

  const weakDom = document.getElementById('weakKnowledgeChart')
  if (weakDom) {
    weakChart = echarts.init(weakDom)
    weakChart.setOption({
      title: { show: false },
      tooltip: { trigger: 'axis', formatter: '{b}：{c}%' },
      grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
      xAxis: { type: 'category', data: [], axisLabel: { color: '#606266', rotate: 30 } },
      yAxis: { type: 'value', axisLabel: { color: '#606266', formatter: '{value}%' }, splitLine: { lineStyle: { color: '#eee' } } },
      series: [{
        name: '错误率',
        type: 'bar',
        data: [],
        itemStyle: { color: '#f56c6c' },
        barWidth: '40%'
      }]
    })
  }
}

const goBack = () => {
  router.push({ name: 'StudentHome' })
}

onMounted(() => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 30)
  dateRange.value = [start.toISOString().split('T')[0], end.toISOString().split('T')[0]]

  initCharts()
  fetchStats()

  window.addEventListener('resize', () => {
    if (wrongChart) wrongChart.resize()
    if (weakChart) weakChart.resize()
  })
})

onUnmounted(() => {
  if (wrongChart) wrongChart.dispose()
  if (weakChart) weakChart.dispose()
})
</script>

<style scoped>
.learning-stats-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

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

.circle-1 { width: 300px; height: 300px; top: -100px; left: -100px; animation-delay: 0s; }
.circle-2 { width: 200px; height: 200px; bottom: -50px; right: -50px; animation-delay: 2s; }
.circle-3 { width: 150px; height: 150px; bottom: 50%; right: 30%; animation-delay: 4s; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(30px) rotate(180deg); }
}

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

.header-left { display: flex; align-items: center; gap: 15px; }
.back-btn { color: #409eff; font-size: 16px; cursor: pointer; transition: color 0.3s; }
.back-btn:hover { color: #66b1ff; text-decoration: underline; }
.title { font-size: 20px; font-weight: 600; margin: 0; color: #409eff; }
.user-info { display: flex; align-items: center; gap: 10px; }
.avatar { width: 36px; height: 36px; --el-avatar-bg-color: #e0f7ff; --el-avatar-text-color: #409eff; }
.username { font-size: 14px; color: #606266; font-weight: 500; }

.main-content { max-width: 1200px; margin: 30px auto; padding: 0 20px; position: relative; z-index: 10; }

.filter-card { margin-bottom: 20px; padding: 20px; }

.core-stats { margin-bottom: 30px; padding: 20px; }
.stats-title { font-size: 18px; font-weight: 600; margin: 0 0 20px 0; color: #303133; }
.core-item { text-align: center; padding: 10px; }
.core-value { font-size: 36px; font-weight: 700; color: #409eff; margin-bottom: 5px; }
.core-label { font-size: 14px; color: #909399; }

.chart-row { margin-bottom: 20px; }
.chart-card { padding: 20px; height: 400px; margin-bottom: 20px; display: flex; flex-direction: column; }
.chart-title { font-size: 16px; font-weight: 600; color: #303133; margin: 0 0 15px 0; }
.chart-container { flex: 1; width: 100%; height: calc(100% - 30px); }

:deep(.stats-card) { border-radius: 16px; backdrop-filter: blur(10px); background: rgba(255, 255, 255, 0.95); box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); border: 1px solid rgba(255, 255, 255, 0.2); }
:deep(.function-card) { border-radius: 16px; backdrop-filter: blur(10px); background: rgba(255, 255, 255, 0.95); box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); border: 1px solid rgba(255, 255, 255, 0.2); }

@media (max-width: 768px) {
  .main-content { padding: 0 10px; }
  .core-value { font-size: 24px; }
  .chart-card { height: 300px; }
}

@media (max-width: 480px) {
  .header-left .title { font-size: 16px; }
  .chart-card { height: 250px; }
}
</style>

<template>
  <div class="learning-stats-page">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <span class="back-btn" @click="goBack">返回</span>
          <h1 class="title">在线刷题系统 - 学习统计</h1>
        </div>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">学生用户</span>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 时间筛选 -->
      <div class="filter-card stats-card">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            ></el-date-picker>
          </el-col>
          <el-col :span="12">
            <el-button type="primary" @click="refreshChart" style="width: 100%; border-radius: 8px;">
              刷新数据
            </el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 核心数据概览 -->
      <div class="core-stats stats-card">
        <h3 class="stats-title">核心学习数据</h3>
        <el-row :gutter="30">
          <el-col :span="6">
            <div class="core-item">
              <div class="core-value">{{ totalAnswer }}</div>
              <div class="core-label">总答题数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="core-item">
              <div class="core-value">{{ correctRate }}%</div>
              <div class="core-label">平均正确率</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="core-item">
              <div class="core-value">{{ dailyAvg }}</div>
              <div class="core-label">日均答题数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="core-item">
              <div class="core-value">{{ totalExam }}</div>
              <div class="core-label">参加考试数</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 图表区域 -->
      <el-row :gutter="20" class="chart-row">
        <!-- 答题趋势图 -->
        <el-col :span="12">
          <div class="chart-card function-card">
            <h4 class="chart-title">每日答题数趋势</h4>
            <div id="answerTrendChart" class="chart-container"></div>
          </div>
        </el-col>

        <!-- 正确率分布图 -->
        <el-col :span="12">
          <div class="chart-card function-card">
            <h4 class="chart-title">各科目正确率分布</h4>
            <div id="correctRateChart" class="chart-container"></div>
          </div>
        </el-col>

        <!-- 题型答题统计 -->
        <el-col :span="24">
          <div class="chart-card function-card">
            <h4 class="chart-title">各题型答题情况</h4>
            <div id="questionTypeChart" class="chart-container"></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 背景装饰 -->
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
// 引入ECharts
import * as echarts from 'echarts'

const router = useRouter()

// 时间筛选
const dateRange = ref([])
// 核心数据
const totalAnswer = ref(528)
const correctRate = ref(82.5)
const dailyAvg = ref(18)
const totalExam = ref(8)

// ECharts实例
let answerTrendChart = null
let correctRateChart = null
let questionTypeChart = null

// 初始化图表
const initCharts = () => {
  // 1. 每日答题数趋势图
  const trendDom = document.getElementById('answerTrendChart')
  answerTrendChart = echarts.init(trendDom)
  answerTrendChart.setOption({
    title: { show: false },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}：{c} 道'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLabel: {
        color: '#606266'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#606266'
      },
      splitLine: {
        lineStyle: {
          color: '#eee'
        }
      }
    },
    series: [
      {
        name: '答题数',
        type: 'line',
        data: [65, 78, 92, 85, 105, 103],
        smooth: true,
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      }
    ]
  })

  // 2. 各科目正确率分布
  const rateDom = document.getElementById('correctRateChart')
  correctRateChart = echarts.init(rateDom)
  correctRateChart.setOption({
    title: { show: false },
    tooltip: {
      trigger: 'item',
      formatter: '{b}：{c}%'
    },
    legend: {
      bottom: 0,
      left: 'center',
      textStyle: {
        color: '#606266'
      }
    },
    series: [
      {
        name: '正确率',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 85, name: 'Vue3' },
          { value: 78, name: 'JavaScript' },
          { value: 88, name: 'HTML/CSS' },
          { value: 75, name: 'TypeScript' }
        ],
        color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c']
      }
    ]
  })

  // 3. 各题型答题情况
  const typeDom = document.getElementById('questionTypeChart')
  questionTypeChart = echarts.init(typeDom)
  questionTypeChart.setOption({
    title: { show: false },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}：{c} 道'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['单选题', '多选题', '判断题', '填空题', '简答题'],
      axisLabel: {
        color: '#606266'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#606266'
      },
      splitLine: {
        lineStyle: {
          color: '#eee'
        }
      }
    },
    series: [
      {
        name: '答对',
        type: 'bar',
        data: [185, 126, 98, 75, 42],
        itemStyle: {
          color: '#67c23a'
        },
        barWidth: '30%'
      },
      {
        name: '答错',
        type: 'bar',
        data: [25, 34, 12, 15, 8],
        itemStyle: {
          color: '#f56c6c'
        },
        barWidth: '30%'
      }
    ]
  })
}

// 刷新图表数据
const refreshChart = () => {
  ElMessage.success('数据刷新成功')
  // 模拟数据更新
  totalAnswer.value = Math.floor(Math.random() * 200) + 400
  correctRate.value = (Math.random() * 10 + 75).toFixed(1)
  dailyAvg.value = Math.floor(Math.random() * 10) + 15

  // 更新趋势图数据
  if (answerTrendChart) {
    const newData = [
      Math.floor(Math.random() * 50) + 50,
      Math.floor(Math.random() * 50) + 60,
      Math.floor(Math.random() * 50) + 70,
      Math.floor(Math.random() * 50) + 80,
      Math.floor(Math.random() * 50) + 90,
      Math.floor(Math.random() * 50) + 100
    ]
    answerTrendChart.setOption({
      series: [{ data: newData }]
    })
  }
}

// 返回首页
const goBack = () => {
  router.push({ name: 'StudentHome' })
}

// 生命周期
onMounted(() => {
  // 初始化默认日期范围（近30天）
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 30)
  dateRange.value = [
    start.toISOString().split('T')[0],
    end.toISOString().split('T')[0]
  ]

  // 初始化图表
  initCharts()

  // 监听窗口大小变化，自适应图表
  window.addEventListener('resize', () => {
    if (answerTrendChart) answerTrendChart.resize()
    if (correctRateChart) correctRateChart.resize()
    if (questionTypeChart) questionTypeChart.resize()
  })
})

onUnmounted(() => {
  // 销毁图表实例，释放内存
  if (answerTrendChart) answerTrendChart.dispose()
  if (correctRateChart) correctRateChart.dispose()
  if (questionTypeChart) questionTypeChart.dispose()
})
</script>

<style scoped>
/* 基础布局 */
.learning-stats-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 背景装饰 */
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

/* 顶部导航 */
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

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

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

/* 主内容区 */
.main-content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  position: relative;
  z-index: 10;
}

/* 筛选栏 */
.filter-card {
  margin-bottom: 20px;
  padding: 20px;
}

/* 核心数据概览 */
.core-stats {
  margin-bottom: 30px;
  padding: 20px;
}

.stats-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px 0;
  color: #303133;
}

.core-item {
  text-align: center;
  padding: 10px;
}

.core-value {
  font-size: 36px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 5px;
}

.core-label {
  font-size: 14px;
  color: #909399;
}

/* 图表区域 */
.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  padding: 20px;
  height: 400px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 15px 0;
}

.chart-container {
  flex: 1;
  width: 100%;
  height: calc(100% - 30px);
}

/* 穿透样式 */
:deep(.stats-card) {
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.function-card) {
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .main-content {
    padding: 0 10px;
  }
  .core-value {
    font-size: 24px;
  }
  .chart-card {
    height: 300px;
  }
  .chart-row .el-col {
    margin-bottom: 20px;
  }
}

@media (max-width: 480px) {
  .header-left .title {
    font-size: 16px;
  }
  .back-btn {
    font-size: 14px;
  }
  .chart-card {
    height: 250px;
  }
}
</style>
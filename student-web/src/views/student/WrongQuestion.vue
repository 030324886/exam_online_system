<template>
  <div class="wrong-question-page">
    <!-- 顶部导航（和考试页/成绩页一致） -->
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <span class="back-btn" @click="goBack">返回</span>
          <h1 class="title">在线刷题系统 - 我的错题本</h1>
        </div>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">学生用户</span>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 筛选栏 -->
      <div class="filter-card stats-card">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-select v-model="filterSubject" placeholder="请选择科目" clearable>
              <el-option label="Vue3" value="vue3"></el-option>
              <el-option label="JavaScript" value="js"></el-option>
              <el-option label="HTML/CSS" value="htmlcss"></el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-select v-model="filterType" placeholder="请选择题型" clearable>
              <el-option label="单选题" value="single"></el-option>
              <el-option label="多选题" value="multiple"></el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="filterWrongQuestions" style="width: 100%; border-radius: 8px;">
              筛选
            </el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 错题统计概览 -->
      <div class="stats-overview stats-card">
        <h3 class="overview-title">错题统计</h3>
        <el-row :gutter="30">
          <el-col :span="8">
            <div class="overview-item">
              <div class="overview-value">{{ totalWrong }}</div>
              <div class="overview-label">总错题数</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="overview-item">
              <div class="overview-value">{{ wrongRate }}%</div>
              <div class="overview-label">错题率</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="overview-item">
              <div class="overview-value">{{ reviewedNum }}</div>
              <div class="overview-label">已复习数</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 错题列表 -->
      <div class="wrong-list" v-if="wrongQuestionList.length > 0">
        <div
          class="wrong-item function-card"
          v-for="(item, index) in wrongQuestionList"
          :key="item.id"
        >
          <div class="question-header">
            <span class="question-index">{{ index + 1 }}.</span>
            <span class="question-title">{{ item.title }}</span>
            <el-tag :type="item.reviewed ? 'success' : 'warning'" size="small">
              {{ item.reviewed ? '已复习' : '未复习' }}
            </el-tag>
          </div>

          <!-- 选项展示 -->
          <div class="question-options">
            <div
              class="option-item"
              v-for="(opt, optIdx) in item.options"
              :key="optIdx"
              :class="{ 'user-answer': optIdx === item.userAnswer, 'correct-answer': optIdx === item.correctAnswer }"
            >
              {{ String.fromCharCode(65 + optIdx) }}. {{ opt }}
            </div>
          </div>

          <!-- 错题解析 -->
          <div class="question-analysis">
            <div class="analysis-label">解析：</div>
            <div class="analysis-content">{{ item.analysis }}</div>
          </div>

          <!-- 操作按钮 -->
          <div class="question-actions">
            <el-button
              type="primary"
              size="small"
              @click="markReviewed(item.id)"
              v-if="!item.reviewed"
              style="border-radius: 8px;"
            >
              标记已复习
            </el-button>
            <el-button
              type="success"
              size="small"
              disabled
              v-else
              style="border-radius: 8px;"
            >
              已复习
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteWrongQuestion(item.id)"
              style="border-radius: 8px; margin-left: 10px;"
            >
              删除错题
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空数据提示 -->
      <div class="empty-tip stats-card" v-else>
        {{ isLoading ? '加载错题数据中...' : '暂无错题记录' }}
      </div>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const isLoading = ref(true)

// 筛选条件
const filterSubject = ref('')
const filterType = ref('')

// 模拟错题数据
const wrongQuestionList = ref([
  {
    id: 1,
    title: 'Vue3中setup语法糖的特点是？',
    type: 'single',
    subject: 'vue3',
    options: ['无this指向', '需要return暴露变量', '不支持组合式API', '只能用选项式API'],
    userAnswer: 1, // 用户选错的选项索引
    correctAnswer: 0, // 正确选项索引
    analysis: 'Vue3的setup语法糖（<script setup>）中没有this指向，无需return即可暴露变量和方法，完美支持组合式API。',
    reviewed: false
  },
  {
    id: 2,
    title: '以下属于Element Plus组件的是？',
    type: 'multiple',
    subject: 'vue3',
    options: ['el-button', 'el-table', 'vant-button', 'el-dialog'],
    userAnswer: [0, 2], // 用户选错的选项索引
    correctAnswer: [0, 1, 3], // 正确选项索引
    analysis: 'el-button、el-table、el-dialog是Element Plus的组件，vant-button是Vant UI的组件（移动端）。',
    reviewed: true
  }
])

// 统计数据
const totalWrong = ref(wrongQuestionList.value.length)
const wrongRate = ref(15) // 模拟错题率
const reviewedNum = ref(wrongQuestionList.value.filter(item => item.reviewed).length)

// 加载错题数据
onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 500)
})

// 返回首页
const goBack = () => {
  router.push({ name: 'StudentHome' })
}

// 筛选错题
const filterWrongQuestions = () => {
  isLoading.value = true
  setTimeout(() => {
    let filteredList = [...wrongQuestionList.value]
    // 科目筛选
    if (filterSubject.value) {
      filteredList = filteredList.filter(item => item.subject === filterSubject.value)
    }
    // 题型筛选
    if (filterType.value) {
      filteredList = filteredList.filter(item => item.type === filterType.value)
    }
    wrongQuestionList.value = filteredList
    totalWrong.value = filteredList.length
    isLoading.value = false
    ElMessage.success('筛选完成')
  }, 300)
}

// 标记已复习
const markReviewed = (id) => {
  const index = wrongQuestionList.value.findIndex(item => item.id === id)
  if (index > -1) {
    wrongQuestionList.value[index].reviewed = true
    reviewedNum.value = wrongQuestionList.value.filter(item => item.reviewed).length
    ElMessage.success('标记为已复习成功')
  }
}

// 删除错题
const deleteWrongQuestion = (id) => {
  ElMessageBox.confirm(
    '确定要删除这道错题吗？删除后无法恢复',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    wrongQuestionList.value = wrongQuestionList.value.filter(item => item.id !== id)
    totalWrong.value = wrongQuestionList.value.length
    reviewedNum.value = wrongQuestionList.value.filter(item => item.reviewed).length
    ElMessage.success('删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}
</script>

<style scoped>
/* 基础布局 - 复用全局渐变背景 */
.wrong-question-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 背景装饰 - 复用全局样式 */
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

/* 顶部导航 - 复用全局样式 */
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

/* 统计概览 */
.stats-overview {
  margin-bottom: 30px;
  padding: 20px;
}

.overview-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px 0;
  color: #303133;
}

.overview-item {
  text-align: center;
  padding: 10px;
}

.overview-value {
  font-size: 36px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 5px;
}

.overview-label {
  font-size: 14px;
  color: #909399;
}

/* 错题列表 */
.wrong-list {
  margin-bottom: 20px;
}

.wrong-item {
  height: auto;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  cursor: default;
}

/* 题目头部 */
.question-header {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.question-index {
  font-weight: 600;
  color: #409eff;
}

.question-title {
  flex: 1;
  font-size: 16px;
  color: #303133;
}

/* 选项样式 */
.question-options {
  width: 100%;
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item {
  padding: 8px 10px;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
}

/* 标记用户答案和正确答案 */
.user-answer {
  background-color: #fef0f0;
  border: 1px solid #fbc4c4;
  color: #f56c6c;
}

.correct-answer {
  background-color: #f0f9ff;
  border: 1px solid #91d5ff;
  color: #409eff;
}

/* 解析样式 */
.question-analysis {
  width: 100%;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.analysis-label {
  font-weight: 600;
  color: #409eff;
  margin-bottom: 5px;
  display: block;
}

.analysis-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

/* 操作按钮 */
.question-actions {
  width: 100%;
  text-align: right;
}

/* 空数据提示 */
.empty-tip {
  text-align: center;
  padding: 50px;
  color: #909399;
  font-size: 16px;
  margin-bottom: 20px;
}

/* 穿透样式 - 复用全局卡片样式 */
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

:deep(.el-select), :deep(.el-button) {
  width: 100%;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .main-content {
    padding: 0 10px;
  }
  .overview-value {
    font-size: 24px;
  }
  .question-title {
    font-size: 14px;
  }
  .option-item {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .header-left .title {
    font-size: 16px;
  }
  .back-btn {
    font-size: 14px;
  }
  .stats-overview .el-col {
    margin-bottom: 15px;
  }
}
</style>
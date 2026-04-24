<template>
  <div class="exam-page">
    <!-- 顶部导航（和首页一致） -->
    <div class="header">
      <div class="header-content">
        <h1 class="title">在线刷题系统 - 在线模考</h1>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">学生用户</span>
          <!-- 考试中隐藏退出登录，避免误操作 -->
        </div>
      </div>
    </div>

    <!-- 主要内容（复用首页布局） -->
    <div class="main-content">
      <!-- 倒计时区域（融入首页风格） -->
      <div class="countdown-card stats-card">
        <div class="countdown">剩余考试时间：{{ formattedTime }}</div>
      </div>

      <!-- 题目列表区域 -->
      <div class="question-container" v-if="questionList.length > 0">
        <div
          class="question-item function-card"
          v-for="(q, index) in questionList"
          :key="q.id"
        >
          <h4 class="question-title">{{ index + 1 }}. {{ q.title }}</h4>
          <!-- 单选示例 -->
          <div class="option-item" v-if="q.type === 'single'">
            <label v-for="(opt, optIdx) in q.options" :key="optIdx" class="option-label">
              <input
                type="radio"
                :name="`q_${q.id}`"
                v-model="userAnswers[index]"
                :value="optIdx"
                @change="handleAnswerChange(index, optIdx)"
                class="option-input"
              >
              {{ opt }}
            </label>
          </div>
          <!-- 多选示例 -->
          <div class="option-item" v-if="q.type === 'multiple'">
            <label v-for="(opt, optIdx) in q.options" :key="optIdx" class="option-label">
              <input
                type="checkbox"
                v-model="userAnswers[index]"
                :value="optIdx"
                @change="handleAnswerChange(index, userAnswers[index])"
                class="option-input"
              >
              {{ opt }}
            </label>
          </div>
        </div>
      </div>

      <!-- 加载/空数据提示（复用首页样式） -->
      <div class="empty-tip stats-card" v-else>
        {{ isLoading ? '加载题目中...' : '暂无考试题目' }}
      </div>

      <!-- 交卷按钮（复用首页按钮风格） -->
      <div class="submit-btn">
        <el-button
          type="primary"
          @click="handleSubmit"
          :disabled="isSubmitting"
          style="width: 200px; height: 40px; font-size: 16px; border-radius: 16px;"
        >
          {{ isSubmitting ? '交卷中...' : '提交试卷' }}
        </el-button>
      </div>
    </div>

    <!-- 背景装饰（和首页完全一致） -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

// 1. 基础变量
const router = useRouter()
const route = useRoute()
const examId = route.params.examId // 从路由获取考试ID
const app = getCurrentInstance().appContext.app // 获取全局方法

// 2. 状态管理
const questionList = ref([]) // 题目列表
const userAnswers = ref([]) // 用户答案
const isLoading = ref(true) // 加载状态
const isSubmitting = ref(false) // 交卷状态
const totalSeconds = ref(30 * 60) // 总时长30分钟（可从接口获取）
const formattedTime = ref('') // 格式化后的倒计时
let countdownTimer = null // 倒计时定时器

// 3. 初始化倒计时（调用App.vue的全局方法）
const initCountdown = () => {
  // 初始格式化
  formattedTime.value = app.config.globalProperties.$formatCountdown(totalSeconds.value)
  // 启动倒计时
  countdownTimer = setInterval(() => {
    if (totalSeconds.value <= 0) {
      clearInterval(countdownTimer)
      ElMessage.info('考试时间结束，自动交卷中...')
      handleSubmit() // 时间到自动交卷
      return
    }
    totalSeconds.value--
    formattedTime.value = app.config.globalProperties.$formatCountdown(totalSeconds.value)
  }, 1000)
}

// 4. 加载考试题目（模拟接口，替换为真实接口）
const loadExamQuestions = async () => {
  try {
    isLoading.value = true
    // 模拟接口请求（替换成你的真实接口）
    // const res = await getExamQuestionsApi(examId)
    // questionList.value = res.data
    // 模拟题目数据（测试用）
    questionList.value = [
      {
        id: 1,
        title: 'Vue3中setup语法糖的特点是？',
        type: 'single',
        options: ['无this指向', '需要return暴露变量', '不支持组合式API', '只能用选项式API']
      },
      {
        id: 2,
        title: '以下属于Element Plus组件的是？',
        type: 'multiple',
        options: ['el-button', 'el-table', 'vant-button', 'el-dialog']
      }
    ]
    // 初始化答案数组
    userAnswers.value = Array(questionList.value.length).fill('')
  } catch (err) {
    ElMessage.error('加载题目失败，请刷新重试')
    console.error('加载题目错误:', err)
  } finally {
    isLoading.value = false
  }
}

// 5. 答案变更处理（自动保存）
const handleAnswerChange = (index, value) => {
  userAnswers.value[index] = value
  // 防抖自动保存（调用App.vue的全局防抖方法）
  app.config.globalProperties.$debounce(() => {
    // 模拟自动保存接口（替换为真实接口）
    console.log('自动保存答案：', { examId, questionId: questionList.value[index].id, answer: value })
  }, 1000)()
}

// 6. 交卷处理
const handleSubmit = async () => {
  // 确认交卷
  try {
    await ElMessageBox.confirm('确定要交卷吗？交卷后无法修改答案', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return // 取消交卷
  }

  try {
    isSubmitting.value = true
    // 模拟交卷接口（替换为真实接口）
    // const res = await submitExamApi({ examId, answers: userAnswers.value })
    ElMessage.success('交卷成功！')
    // 跳转到成绩页
    router.push({ name: 'ScorePage', params: { examId } })
  } catch (err) {
    ElMessage.error('交卷失败，请重试')
    console.error('交卷错误:', err)
  } finally {
    isSubmitting.value = false
  }
}

// 7. 生命周期
onMounted(() => {
  loadExamQuestions() // 加载题目
  initCountdown() // 启动倒计时
  // 监听页面离开，清除定时器
  window.addEventListener('beforeunload', () => {
    clearInterval(countdownTimer)
  })
})

onUnmounted(() => {
  clearInterval(countdownTimer) // 销毁定时器
})
</script>

<style scoped>
/* 基础布局 - 完全复用首页渐变背景+布局 */
.exam-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 背景装饰 - 和首页完全一致 */
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

/* 顶部导航 - 完全复用首页样式 */
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

/* 主内容区 - 复用首页样式 */
.main-content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  position: relative;
  z-index: 10;
}

/* 倒计时卡片 - 复用首页统计卡片样式 */
.countdown-card {
  margin-bottom: 30px;
  padding: 20px;
  text-align: center;
}

.countdown {
  color: #e64340;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

/* 题目容器 */
.question-container {
  margin-bottom: 40px;
}

/* 题目卡片 - 完全复用首页功能卡片样式 */
.question-item {
  height: auto;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* 题目左对齐 */
  justify-content: flex-start;
  cursor: default; /* 取消点击手势 */
  transition: all 0.3s ease;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 题目标题 - 适配首页文字风格 */
.question-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  width: 100%;
}

/* 选项样式 - 适配首页文字风格 */
.option-item {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.option-label {
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.option-input {
  cursor: pointer;
}

/* 空数据提示 - 复用首页统计卡片样式 */
.empty-tip {
  text-align: center;
  padding: 50px;
  color: #909399;
  font-size: 16px;
  margin-bottom: 20px;
}

/* 交卷按钮 - 居中+适配首页圆角风格 */
.submit-btn {
  text-align: center;
  margin-top: 20px;
  position: relative;
  z-index: 10;
}

/* 复用首页的卡片样式类（穿透） */
:deep(.stats-card) {
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.el-button--primary) {
  background-color: #409eff;
  border-color: #409eff;
  transition: all 0.3s;
}

:deep(.el-button--primary:hover) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

:deep(.el-button--primary:disabled) {
  background-color: #a0cfff;
  border-color: #a0cfff;
}

/* 响应式适配 - 完全复用首页规则 */
@media (max-width: 768px) {
  .exam-page .main-content {
    padding: 0 10px;
  }
  .question-item {
    width: 100% !important;
    margin: 10px 0 !important;
  }
  .countdown {
    font-size: 14px !important;
  }
  .question-title {
    font-size: 14px;
  }
  .option-label {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .submit-btn .el-button {
    width: 100% !important;
    margin-bottom: 10px !important;
  }
  .header-content .title {
    font-size: 16px;
  }
}
</style>
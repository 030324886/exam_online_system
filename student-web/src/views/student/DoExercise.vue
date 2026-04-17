<template>
  <div class="do-exercise-page">
    <!-- 自定义返回头 -->
    <div class="custom-page-header">
      <el-button
        icon="el-icon-arrow-left"
        type="text"
        @click="goBack"
        class="back-btn"
      >返回</el-button>
      <h2 class="page-title">章节刷题</h2>
    </div>

    <!-- 刷题内容容器 -->
    <div class="exercise-container">
      <el-card class="question-card" shadow="hover">
        <!-- 题目序号和内容 -->
        <div class="question-header">
          <span class="question-index">{{ currentIndex + 1 }}.</span>
          <span class="question-content">{{ currentQuestion.content }}</span>
        </div>

        <!-- 选项区域 -->
        <div class="options-area">
          <el-radio-group
            v-model="userAnswer"
            class="radio-group"
          >
            <div class="option-item" v-for="(option, index) in currentQuestion.options" :key="index">
              <!-- 修复：将</el-option>改为</el-radio> -->
              <el-radio :label="option.value">{{ option.label }}. {{ option.content }}</el-radio>
            </div>
          </el-radio-group>
        </div>

        <!-- 操作按钮 -->
        <div class="btn-group">
          <el-button
            type="default"
            @click="prevQuestion"
            :disabled="currentIndex === 0"
          >上一题</el-button>
          <el-button
            type="primary"
            @click="nextQuestion"
            :disabled="currentIndex === questionList.length - 1"
          >下一题</el-button>
          <el-button
            type="success"
            @click="submitAnswer"
          >提交答案</el-button>
        </div>
      </el-card>

      <!-- 答题进度 -->
      <div class="progress-container">
        <el-progress
          :percentage="progress"
          :text-inside="true"
          stroke-width="20"
          class="progress-bar"
        ></el-progress>
        <div class="progress-text">
          {{ currentIndex + 1 }} / {{ questionList.length }} 题
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 章节ID（从路由参数获取）
const chapterId = ref(route.params.chapterId)

// 答题相关数据
const currentIndex = ref(0) // 当前题目索引
const userAnswer = ref('') // 用户答案
const progress = ref(0) // 答题进度

// 模拟题目数据
const questionList = ref([
  {
    id: 1,
    content: 'Vue3中用于创建响应式数据的API是？',
    options: [
      { label: 'A', value: 'ref', content: 'ref' },
      { label: 'B', value: 'reactive', content: 'reactive' },
      { label: 'C', value: 'both', content: '以上都是' },
      { label: 'D', value: 'none', content: '以上都不是' }
    ],
    answer: 'both' // 正确答案
  },
  {
    id: 2,
    content: 'Vue3的组合式API中，setup函数的执行时机是？',
    options: [
      { label: 'A', value: 'beforeCreate', content: 'beforeCreate之前' },
      { label: 'B', value: 'created', content: 'created之后' },
      { label: 'C', value: 'mounted', content: 'mounted之后' },
      { label: 'D', value: 'updated', content: 'updated之后' }
    ],
    answer: 'beforeCreate'
  }
])

// 当前题目
const currentQuestion = ref(questionList.value[0])

// 计算答题进度
const calculateProgress = () => {
  progress.value = Math.round(((currentIndex.value + 1) / questionList.value.length) * 100)
}

// 返回题库页面
const goBack = () => {
  router.push('/question/bank')
}

// 上一题
const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
    currentQuestion.value = questionList.value[currentIndex.value]
    // 恢复用户上一题的答案（如果有）
    userAnswer.value = ''
    calculateProgress()
  }
}

// 下一题
const nextQuestion = () => {
  if (currentIndex.value < questionList.value.length - 1) {
    currentIndex.value++
    currentQuestion.value = questionList.value[currentIndex.value]
    userAnswer.value = ''
    calculateProgress()
  }
}

// 提交答案
const submitAnswer = async () => {
  if (!userAnswer.value) {
    ElMessage.warning('请选择答案后再提交！')
    return
  }

  try {
    // 模拟提交答案到后端
    const isCorrect = userAnswer.value === currentQuestion.value.answer

    if (isCorrect) {
      ElMessage.success('回答正确！')
    } else {
      ElMessage.error(`回答错误，正确答案是：${currentQuestion.value.answer}`)
      // 模拟将错题加入错题本
      await ElMessageBox.confirm(
        '是否将这道题加入错题本？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        // 模拟加入错题本的API请求
        ElMessage.success('已加入错题本！')
      }).catch(() => {
        ElMessage.info('已取消加入错题本')
      })
    }

    // 自动跳转到下一题（如果不是最后一题）
    if (currentIndex.value < questionList.value.length - 1) {
      nextQuestion()
    } else {
      ElMessage.info('已完成所有题目！')
    }
  } catch (error) {
    ElMessage.error('提交答案失败，请重试！')
    console.error('提交答案错误：', error)
  }
}

// 初始化
onMounted(() => {
  calculateProgress()
  console.log(`开始刷章节 ${chapterId.value} 的题目`)
})
</script>

<style scoped>
.do-exercise-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.custom-page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.back-btn {
  font-size: 18px;
  margin-right: 15px;
  color: #409eff;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.exercise-container {
  max-width: 800px;
  margin: 0 auto;
}

.question-card {
  margin-bottom: 30px;
  padding: 20px;
}

.question-header {
  display: flex;
  margin-bottom: 20px;
  font-size: 18px;
  line-height: 1.6;
}

.question-index {
  font-weight: bold;
  color: #409eff;
  margin-right: 10px;
}

.question-content {
  flex: 1;
}

.options-area {
  margin-bottom: 30px;
}

.radio-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.option-item {
  font-size: 16px;
  padding: 10px;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.option-item:hover {
  background-color: #f0f7ff;
}

.btn-group {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.progress-container {
  max-width: 600px;
  margin: 0 auto;
}

.progress-bar {
  margin-bottom: 10px;
}

.progress-text {
  text-align: center;
  font-size: 16px;
  color: #666;
}
</style>
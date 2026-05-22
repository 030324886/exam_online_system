<template>
  <div class="do-exercise-page">
    <div class="custom-page-header">
      <el-button icon="el-icon-arrow-left" type="text" @click="goBack" class="back-btn">返回</el-button>
      <h2 class="page-title">章节刷题</h2>
    </div>

    <div class="exercise-container">
      <el-card class="question-card" shadow="hover" v-if="questionList.length > 0">
        <div class="question-header">
          <span class="question-index">{{ currentIndex + 1 }}.</span>
          <span class="question-type-tag" :class="typeClass(currentQuestion)">{{ typeLabel(currentQuestion) }}</span>
          <span class="question-content-text">{{ currentQuestion.content }}</span>
        </div>

        <div class="options-area" v-if="isSingleOrJudge(currentQuestion)">
          <el-radio-group v-model="userAnswer" class="radio-group">
            <div class="option-item" v-for="(option, index) in parsedOptions" :key="index">
              <el-radio :value="option.value" size="large">
                <span class="opt-label">{{ option.label }}.</span>
                <span class="opt-text">{{ option.content }}</span>
              </el-radio>
            </div>
          </el-radio-group>
        </div>

        <div class="options-area" v-else-if="isMulti(currentQuestion)">
          <el-checkbox-group v-model="multiAnswer" class="radio-group">
            <div class="option-item" v-for="(option, index) in parsedOptions" :key="index">
              <el-checkbox :value="option.value" size="large">
                <span class="opt-label">{{ option.label }}.</span>
                <span class="opt-text">{{ option.content }}</span>
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </div>

        <div class="fill-area" v-else>
          <el-input v-model="userAnswer" type="textarea" :rows="4" placeholder="请输入你的答案" class="fill-input"></el-input>
        </div>

        <div class="btn-group">
          <el-button type="default" @click="prevQuestion" :disabled="currentIndex === 0">上一题</el-button>
          <el-button type="primary" @click="nextQuestion" :disabled="currentIndex === questionList.length - 1">下一题</el-button>
          <el-button type="success" @click="submitAnswer" :disabled="submitting">{{ submitting ? '提交中...' : '提交答案' }}</el-button>
        </div>

        <div class="answer-result" v-if="answerResult !== null">
          <span v-if="answerResult" class="result-correct">&#10003; 回答正确！</span>
          <span v-else class="result-wrong">&#10007; 回答错误，正确答案：{{ currentQuestion.answer }}</span>
        </div>
      </el-card>

      <div class="empty-tip" v-else>
        {{ isLoading ? '加载题目中...' : '暂无题目数据' }}
      </div>

      <div class="progress-container" v-if="questionList.length > 0">
        <el-progress :percentage="progress" :text-inside="true" stroke-width="20" class="progress-bar"></el-progress>
        <div class="progress-text">{{ currentIndex + 1 }} / {{ questionList.length }} 题</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request2 from '../../api/request2'

const router = useRouter()
const route = useRoute()

const knowledgePoint = ref(route.params.chapterId || '')
const currentIndex = ref(0)
const userAnswer = ref('')
const multiAnswer = ref([])
const progress = ref(0)
const questionList = ref([])
const isLoading = ref(true)
const submitting = ref(false)
const answerResult = ref(null)

const typeLabel = (q) => {
  const map = { '1': '单选', '2': '多选', '3': '判断', '4': '填空', 'SINGLE': '单选', 'MULTIPLE': '多选', 'JUDGE': '判断', 'FILL': '填空', 'SHORT': '简答' }
  return map[String(q.type)] || '其他'
}

const typeClass = (q) => {
  const map = { '1': 'type-single', '2': 'type-multi', '3': 'type-judge', 'SINGLE': 'type-single', 'MULTIPLE': 'type-multi', 'JUDGE': 'type-judge', 'FILL': 'type-fill', 'SHORT': 'type-short' }
  return map[String(q.type)] || 'type-other'
}

const isSingleOrJudge = (q) => {
  return ['1', '3', 'SINGLE', 'JUDGE'].includes(String(q.type))
}

const isMulti = (q) => {
  return ['2', 'MULTIPLE'].includes(String(q.type))
}

const parsedOptions = computed(() => {
  const q = currentQuestion.value
  if (!q || !q.options) return []
  if (Array.isArray(q.options)) {
    return q.options.map((opt, idx) => {
      if (typeof opt === 'string') return { label: String.fromCharCode(65 + idx), value: String.fromCharCode(65 + idx), content: opt }
      return opt
    })
  }
  try {
    const parsed = typeof q.options === 'string' ? JSON.parse(q.options) : q.options
    if (Array.isArray(parsed)) {
      return parsed.map((opt, idx) => {
        if (typeof opt === 'string') return { label: String.fromCharCode(65 + idx), value: String.fromCharCode(65 + idx), content: opt }
        return opt
      })
    }
    return Object.entries(parsed).map(([key, val], idx) => ({
      label: String.fromCharCode(65 + idx), value: key, content: String(val)
    }))
  } catch {
    return []
  }
})

const currentQuestion = computed(() => {
  return questionList.value[currentIndex.value] || { content: '', options: [], type: '' }
})

const calculateProgress = () => {
  progress.value = Math.round(((currentIndex.value + 1) / questionList.value.length) * 100)
}

const goBack = () => {
  router.push('/question/bank')
}

const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
    resetAnswer()
    calculateProgress()
  }
}

const nextQuestion = () => {
  if (currentIndex.value < questionList.value.length - 1) {
    currentIndex.value++
    resetAnswer()
    calculateProgress()
  }
}

const resetAnswer = () => {
  userAnswer.value = ''
  multiAnswer.value = []
  answerResult.value = null
}

const submitAnswer = async () => {
  const isMultiQ = isMulti(currentQuestion.value)
  let finalAnswer = ''

  if (isMultiQ) {
    finalAnswer = [...multiAnswer.value].sort().join(',')
  } else {
    finalAnswer = userAnswer.value
  }

  if (!finalAnswer) {
    ElMessage.warning('请选择答案后再提交！')
    return
  }

  try {
    submitting.value = true
    const userId = localStorage.getItem('userId')
    const res = await request2.post('/answer/submit', {
      userId: userId ? parseInt(userId) : 0,
      paperId: 0,
      answers: [{ questionId: currentQuestion.value.id, userAnswer: finalAnswer }]
    })

    if (res.code === 200) {
      const records = res.data || []
      const record = records.find(r => r.questionId === currentQuestion.value.id)
      const isCorrect = record ? (record.isCorrect === 1 || record.isCorrect === true) : false
      answerResult.value = isCorrect

      if (isCorrect) {
        ElMessage.success('回答正确！')
      } else {
        ElMessage.error('回答错误')
        try {
          await ElMessageBox.confirm('是否将这道题加入错题本？', '提示', {
            confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
          })
          await request2.post('/wrong/add', null, {
            params: { userId: parseInt(userId || '0'), questionId: currentQuestion.value.id }
          })
          ElMessage.success('已加入错题本！')
        } catch {}
      }

      if (currentIndex.value < questionList.value.length - 1) {
        setTimeout(() => nextQuestion(), 1000)
      } else {
        setTimeout(() => ElMessage.info('已完成所有题目！'), 1000)
      }
    } else {
      ElMessage.error(res.msg || '提交答案失败')
    }
  } catch (error) {
    ElMessage.error('提交答案失败，请重试！')
    console.error('提交答案错误：', error)
  } finally {
    submitting.value = false
  }
}

const fetchQuestions = async () => {
  try {
    isLoading.value = true
    let res
    if (knowledgePoint.value && knowledgePoint.value !== 'all') {
      res = await request2.get(`/question/knowledge/${encodeURIComponent(knowledgePoint.value)}`)
    } else {
      res = await request2.get('/question/list')
    }
    if (res.code === 200) {
      const data = res.data || []
      if (knowledgePoint.value && knowledgePoint.value !== 'all' && Array.isArray(data)) {
        questionList.value = data.filter(q => q.knowledgePoint === knowledgePoint.value)
      } else {
        questionList.value = Array.isArray(data) ? data : []
      }
    }
  } catch (err) {
    ElMessage.error('获取题目失败')
    console.error(err)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchQuestions()
  calculateProgress()
})
</script>

<style scoped>
.do-exercise-page {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
}

.custom-page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.back-btn { font-size: 18px; margin-right: 15px; color: #409eff; }
.page-title { font-size: 24px; font-weight: 600; color: #333; }

.exercise-container { max-width: 800px; margin: 0 auto; }

.question-card { margin-bottom: 30px; padding: 24px; border-radius: 16px; }

.question-header { display: flex; align-items: flex-start; margin-bottom: 20px; gap: 10px; font-size: 18px; line-height: 1.6; flex-wrap: wrap; }
.question-index { font-weight: bold; color: #409eff; flex-shrink: 0; }
.question-type-tag { display: inline-block; padding: 1px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; color: #fff; flex-shrink: 0; }
.type-single { background: #409eff; }
.type-multi { background: #67c23a; }
.type-judge { background: #e6a23c; }
.type-fill { background: #909399; }
.type-short { background: #f56c6c; }
.question-content-text { flex: 1; }

.options-area { margin-bottom: 20px; }
.radio-group { display: flex; flex-direction: column; gap: 4px; }
.option-item { padding: 6px 10px; border-radius: 8px; transition: background-color 0.2s; }
.option-item:hover { background-color: #f0f7ff; }

:deep(.el-radio), :deep(.el-checkbox) { height: auto; padding: 6px 0; }
:deep(.el-radio__label), :deep(.el-checkbox__label) { font-size: 16px; display: inline-flex; align-items: center; gap: 4px; }
.opt-label { font-weight: 600; color: #409eff; }
.opt-text { color: #333; }

.fill-area { margin-bottom: 20px; }
.fill-input { width: 100%; }

.btn-group { display: flex; justify-content: center; gap: 20px; flex-wrap: wrap; }

.answer-result { text-align: center; margin-top: 15px; padding: 10px; border-radius: 8px; font-size: 16px; font-weight: 600; }
.result-correct { color: #67c23a; }
.result-wrong { color: #f56c6c; }

.progress-container { max-width: 600px; margin: 0 auto; }
.progress-bar { margin-bottom: 10px; }
.progress-text { text-align: center; font-size: 16px; color: #666; }

.empty-tip { text-align: center; padding: 80px 20px; color: #909399; font-size: 16px; background: rgba(255, 255, 255, 0.9); border-radius: 16px; max-width: 800px; margin: 0 auto; }
</style>

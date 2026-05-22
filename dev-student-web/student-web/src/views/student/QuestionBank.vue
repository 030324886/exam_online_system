<template>
  <div class="question-bank-page">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="watermark">福州大学至诚学院</div>

    <div class="question-bank-container">
      <div class="custom-page-header">
        <el-button icon="el-icon-arrow-left" type="text" @click="goBack" class="back-btn">返回</el-button>
        <h2 class="page-title">题库列表</h2>
      </div>

      <el-card class="filter-card" shadow="hover">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="知识点" class="form-item">
            <el-select v-model="filterForm.knowledgePoint" placeholder="请选择知识点" size="large" class="filter-select" clearable>
              <el-option v-for="item in subjectList" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="题型" class="form-item">
            <el-select v-model="filterForm.type" placeholder="请选择题型" size="large" class="filter-select" clearable>
              <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-card>

      <div v-if="isLoading" class="loading-tip">加载题目中...</div>

      <template v-else>
        <div class="question-card" v-for="q in filteredQuestions" :key="q.id">
          <div class="question-header">
            <span class="question-tag" :class="typeClass(q)">{{ typeLabel(q) }}</span>
            <span class="question-knowledge">{{ q.knowledgePoint || '未分类' }}</span>
            <span class="question-difficulty" v-if="q.difficulty">难度：{{ '★'.repeat(q.difficulty) }}</span>
          </div>
          <div class="question-content">{{ q.content }}</div>

          <!-- 单选 / 判断题：使用 el-radio-group -->
          <div class="options-area" v-if="isSingleOrJudge(q)">
            <el-radio-group v-model="singleAnswers[q.id]" class="radio-group">
              <div class="option-item" v-for="(opt, idx) in getParsedOptions(q)" :key="idx">
                <el-radio :value="opt.value" size="large">
                  <span class="opt-label">{{ opt.label }}.</span>
                  <span class="opt-text">{{ opt.content }}</span>
                </el-radio>
              </div>
            </el-radio-group>
          </div>

          <!-- 多选题：使用 el-checkbox-group -->
          <div class="options-area" v-else-if="isMulti(q)">
            <el-checkbox-group v-model="multiAnswers[q.id]" class="radio-group">
              <div class="option-item" v-for="(opt, idx) in getParsedOptions(q)" :key="idx">
                <el-checkbox :value="opt.value" size="large">
                  <span class="opt-label">{{ opt.label }}.</span>
                  <span class="opt-text">{{ opt.content }}</span>
                </el-checkbox>
              </div>
            </el-checkbox-group>
          </div>

          <!-- 填空 / 简答题：使用 el-input -->
          <div class="fill-area" v-else>
            <el-input v-model="fillAnswers[q.id]" type="textarea" :rows="3" placeholder="请输入你的答案" class="fill-input"></el-input>
          </div>

          <div class="question-footer">
            <span class="answer-status" v-if="answerStatus[q.id] !== undefined">
              <span v-if="answerStatus[q.id]" class="status-correct">&#10003; 回答正确</span>
              <span v-else class="status-wrong">&#10007; 回答错误，正确答案：{{ correctAnswerText(q) }}</span>
            </span>
            <span class="answer-status" v-else>
              <span class="status-hint" v-if="isMulti(q) && multiAnswers[q.id]?.length > 0">已选 {{ multiAnswers[q.id].length }} 项</span>
              <span class="status-hint" v-else-if="singleAnswers[q.id]">已选答案</span>
              <span class="status-hint" v-else-if="fillAnswers[q.id]">已填写</span>
            </span>
            <el-button type="primary" size="small" @click="submitSingleAnswer(q)" class="submit-btn"
              :disabled="submittingMap[q.id]">
              {{ submittingMap[q.id] ? '提交中...' : '提交答案' }}
            </el-button>
          </div>
        </div>

        <div v-if="filteredQuestions.length === 0" class="empty-tip">暂无匹配的题目</div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request2 from '../../api/request2'

const router = useRouter()

const filterForm = ref({ knowledgePoint: '', type: '' })
const allQuestions = ref([])
const isLoading = ref(true)
const singleAnswers = ref({})
const multiAnswers = ref({})
const fillAnswers = ref({})
const answerStatus = ref({})
const submittingMap = ref({})

const typeOptions = [
  { label: '全部', value: '' },
  { label: '单选题', value: '1' },
  { label: '多选题', value: '2' },
  { label: '判断题', value: '3' },
  { label: '填空题', value: '4' },
  { label: '简答题', value: 'SHORT' }
]

const subjectList = computed(() => {
  const points = new Set(allQuestions.value.filter(q => q.knowledgePoint).map(q => q.knowledgePoint))
  return [...points].sort()
})

const filteredQuestions = computed(() => {
  let list = allQuestions.value
  if (filterForm.value.knowledgePoint) {
    list = list.filter(q => q.knowledgePoint === filterForm.value.knowledgePoint)
  }
  if (filterForm.value.type) {
    const t = filterForm.value.type
    list = list.filter(q => String(q.type) === t)
  }
  return list
})

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

const getParsedOptions = (q) => {
  if (!q.options) return []
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
    return typeof q.options === 'string' ? [{ label: 'A', value: 'A', content: q.options }] : []
  }
}

const correctAnswerText = (q) => {
  return q.answer || ''
}

const submitSingleAnswer = async (q) => {
  const userId = localStorage.getItem('userId')
  let finalAnswer = ''

  if (isSingleOrJudge(q)) {
    finalAnswer = singleAnswers.value[q.id] || ''
  } else if (isMulti(q)) {
    const selected = multiAnswers.value[q.id] || []
    finalAnswer = [...selected].sort().join(',')
  } else {
    finalAnswer = fillAnswers.value[q.id] || ''
  }

  if (!finalAnswer) {
    ElMessage.warning('请先选择或填写答案')
    return
  }

  try {
    submittingMap.value = { ...submittingMap.value, [q.id]: true }
    const res = await request2.post('/answer/submit', {
      userId: userId ? parseInt(userId) : 0,
      paperId: 0,
      answers: [{ questionId: q.id, userAnswer: finalAnswer }]
    })

    if (res.code === 200) {
      const records = res.data || []
      const record = records.find(r => r.questionId === q.id)
      const isCorrect = record ? (record.isCorrect === 1 || record.isCorrect === true) : false
      answerStatus.value = { ...answerStatus.value, [q.id]: isCorrect }

      if (isCorrect) {
        ElMessage.success('回答正确！')
      } else {
        ElMessage.error('回答错误')
        try {
          await ElMessageBox.confirm('是否将这道题加入错题本？', '提示', {
            confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
          })
          await request2.post('/wrong/add', null, {
            params: { userId: parseInt(userId || '0'), questionId: q.id }
          })
          ElMessage.success('已加入错题本！')
        } catch {}
      }
    } else {
      ElMessage.error(res.msg || '提交答案失败')
    }
  } catch (err) {
    ElMessage.error('提交答案失败，请重试')
    console.error('提交答案错误:', err)
  } finally {
    submittingMap.value = { ...submittingMap.value, [q.id]: false }
  }
}

const goBack = () => router.push('/student/home')

const fetchQuestions = async () => {
  try {
    isLoading.value = true
    const res = await request2.get('/question/list')
    if (res.code === 200) {
      allQuestions.value = res.data || []
    }
  } catch (err) {
    ElMessage.error('获取题目列表失败')
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchQuestions()
})
</script>

<style scoped>
.question-bank-page {
  width: 100vw;
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow-x: hidden;
  padding: 20px 0;
}
.bg-decoration { position: absolute; width: 100%; height: 100%; top: 0; left: 0; z-index: 0; pointer-events: none; }
.circle { position: absolute; border-radius: 50%; background: rgba(255, 255, 255, 0.4); animation: float 8s infinite ease-in-out; z-index: 0; }
.circle-1 { width: 300px; height: 300px; top: -100px; left: -100px; animation-delay: 0s; }
.circle-2 { width: 200px; height: 200px; bottom: -50px; right: -50px; animation-delay: 2s; }
.circle-3 { width: 150px; height: 150px; bottom: 50%; right: 30%; animation-delay: 4s; }
@keyframes float { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(30px) rotate(180deg); } }
.watermark { position: absolute; top: 40px; left: 50%; transform: translateX(-50%); font-size: 48px; font-weight: bold; color: rgba(255, 255, 255, 0.4); z-index: 1; white-space: nowrap; user-select: none; pointer-events: none; letter-spacing: 4px; }

.question-bank-container { padding: 20px; max-width: 1000px; margin: 0 auto; position: relative; z-index: 10; }
.custom-page-header { margin-bottom: 20px; padding: 15px 20px; background: rgba(255, 255, 255, 0.9); border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); display: flex; align-items: center; gap: 15px; }
.back-btn { font-size: 18px; font-weight: 500; color: #409eff; padding: 8px 16px; border-radius: 8px; transition: all 0.3s ease; }
.back-btn:hover { background-color: #f0f9ff; color: #66b1ff; }
.page-title { font-size: 24px; font-weight: 600; color: #303133; margin: 0; }

.filter-card { margin-bottom: 20px; padding: 20px; background: rgba(255, 255, 255, 0.95) !important; backdrop-filter: blur(10px); border-radius: 16px !important; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important; }
.filter-form { padding: 5px 0; width: 100%; }
.form-item { margin-right: 20px !important; }
:deep(.el-form-item__label) { font-size: 16px; font-weight: 500; color: #606266; }
.filter-select { width: 200px; }

.loading-tip { text-align: center; padding: 80px 20px; font-size: 16px; color: #909399; background: rgba(255, 255, 255, 0.9); border-radius: 16px; }

.question-card { background: rgba(255, 255, 255, 0.95); border-radius: 16px; padding: 24px; margin-bottom: 20px; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08); border: 1px solid rgba(255, 255, 255, 0.2); transition: all 0.3s ease; }
.question-card:hover { box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); }

.question-header { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; flex-wrap: wrap; }
.question-tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 12px; font-weight: 600; color: #fff; }
.type-single { background: #409eff; }
.type-multi { background: #67c23a; }
.type-judge { background: #e6a23c; }
.type-fill { background: #909399; }
.type-short { background: #f56c6c; }
.type-other { background: #909399; }
.question-knowledge { font-size: 13px; color: #909399; }
.question-difficulty { font-size: 13px; color: #e6a23c; margin-left: auto; }

.question-content { font-size: 16px; font-weight: 500; color: #303133; line-height: 1.6; margin-bottom: 16px; }

.options-area { display: flex; flex-direction: column; gap: 4px; margin-bottom: 16px; }
.radio-group { display: flex; flex-direction: column; gap: 4px; }
.option-item { padding: 6px 10px; border-radius: 8px; transition: background-color 0.2s; }
.option-item:hover { background-color: #f0f7ff; }

:deep(.el-radio), :deep(.el-checkbox) { height: auto; padding: 6px 0; }
:deep(.el-radio__label), :deep(.el-checkbox__label) { font-size: 15px; display: inline-flex; align-items: center; gap: 4px; }
.opt-label { font-weight: 600; color: #409eff; }
.opt-text { color: #333; }

.fill-area { margin-bottom: 16px; }
.fill-input { width: 100%; }

.question-footer { display: flex; align-items: center; justify-content: space-between; padding-top: 12px; border-top: 1px solid #f0f0f0; flex-wrap: wrap; gap: 8px; }
.answer-status { font-size: 14px; font-weight: 500; }
.status-correct { color: #67c23a; }
.status-wrong { color: #f56c6c; }
.status-hint { color: #909399; font-size: 13px; }
.submit-btn { border-radius: 8px; flex-shrink: 0; }

.empty-tip { text-align: center; padding: 60px; color: #909399; font-size: 16px; background: rgba(255, 255, 255, 0.9); border-radius: 16px; }

@media (max-width: 768px) {
  .question-bank-container { padding: 10px; }
  .question-card { padding: 16px; }
  .filter-select { width: 100%; }
}
</style>

<template>
  <div class="exam-page">
    <div class="header">
      <div class="header-content">
        <h1 class="title">在线刷题系统 - 在线模考</h1>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">{{ username }}</span>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="countdown-card stats-card" v-if="paperName">
        <div class="countdown">{{ paperName }} | 剩余时间：{{ formattedTime }}</div>
      </div>

      <div v-if="isLoading" class="empty-tip stats-card">
        <p>加载题目中...</p>
      </div>

      <template v-else-if="questionList.length > 0">
        <div class="question-item" v-for="(q, idx) in questionList" :key="q.id">
          <div class="question-title">{{ idx+1 }}. {{ q.content }}</div>

          <div v-if="q.type === '1' || q.type === '3'" class="options-area">
            <el-radio-group v-model="answers[q.id]" class="full-width">
              <div class="option-row" v-for="opt in q.parsedOptions" :key="opt.value">
                <el-radio :value="opt.value" size="large">
                  <span class="opt-label">{{ opt.label }}.</span>
                  <span class="opt-text">{{ opt.content }}</span>
                </el-radio>
              </div>
            </el-radio-group>
          </div>

          <div v-else-if="q.type === '2'" class="options-area">
            <el-checkbox-group v-model="answers[q.id]" class="full-width">
              <div class="option-row" v-for="opt in q.parsedOptions" :key="opt.value">
                <el-checkbox :value="opt.value" size="large">
                  <span class="opt-label">{{ opt.label }}.</span>
                  <span class="opt-text">{{ opt.content }}</span>
                </el-checkbox>
              </div>
            </el-checkbox-group>
          </div>

          <div v-else class="fill-area">
            <el-input v-model="answers[q.id]" type="textarea" :rows="3" placeholder="请输入答案"></el-input>
          </div>
        </div>

        <div class="submit-btn">
          <el-button type="primary" @click="handleSubmit" :disabled="isSubmitting"
            style="width:200px;height:42px;font-size:16px;border-radius:16px">
            {{ isSubmitting ? '交卷中...' : '提交试卷' }}
          </el-button>
        </div>
      </template>

      <div v-else class="empty-tip stats-card">
        <p>暂无考试题目</p>
        <el-button type="primary" @click="loadExamQuestions" style="margin-top:10px">重新加载</el-button>
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import request from '../../api/request'

const router = useRouter()
const route = useRoute()
const examId = String(route.params.id || '1')

const username = ref(localStorage.getItem('username') || '学生用户')
const questionList = ref([])
const answers = reactive({})
const isLoading = ref(true)
const isSubmitting = ref(false)
const totalSeconds = ref(30 * 60)
const formattedTime = ref('')
const paperName = ref('')
let timer = null

function fmt(s) {
  const h = String(Math.floor(s / 3600)).padStart(2, '0')
  const m = String(Math.floor((s % 3600) / 60)).padStart(2, '0')
  const sec = String(s % 60).padStart(2, '0')
  return h + ':' + m + ':' + sec
}

function startTimer() {
  formattedTime.value = fmt(totalSeconds.value)
  timer = setInterval(() => {
    if (totalSeconds.value <= 0) {
      clearInterval(timer)
      ElMessage.info('时间到，自动交卷')
      handleSubmit()
      return
    }
    totalSeconds.value--
    formattedTime.value = fmt(totalSeconds.value)
  }, 1000)
}

function parseOpts(q) {
  let opts = q.options
  if (!opts) return []
  if (typeof opts === 'string') { try { opts = JSON.parse(opts) } catch { return [] } }
  if (Array.isArray(opts)) {
    return opts.map((o, i) => ({
      label: String.fromCharCode(65 + i),
      value: String.fromCharCode(65 + i),
      content: typeof o === 'string' ? o : (o.content || '')
    }))
  }
  return []
}

function getFinalAnswer(qId) {
  const val = answers[qId]
  if (val === undefined || val === null) return ''
  if (Array.isArray(val)) return [...val].sort().join(',')
  return String(val)
}

async function loadExamQuestions() {
  isLoading.value = true
  try {
    const res = await request.get('/papers/detail/' + examId)
    if (res.success === false) throw new Error(res.message || '获取失败')
    const data = res.data || res
    const paper = data.paper || {}
    paperName.value = paper.name || paper.title || '试卷'
    const raw = data.questions || []
    questionList.value = raw.map(q => ({
      id: q.id,
      content: q.content || q.stem || '',
      type: String(q.type || '1'),
      parsedOptions: parseOpts(q)
    }))
    const d = paper.durationMinutes || paper.duration || 30
    totalSeconds.value = d * 60
  } catch (err) {
    console.error('加载试卷失败:', err)
    ElMessage.warning('试卷加载失败')
    try {
      const m = await import('../../api/request2')
      const r = await m.default.get('/question/list')
      if (r.code === 200) {
        const list = (r.data || []).slice(0, 10)
        questionList.value = list.map(q => ({
          id: q.id,
          content: q.content || '',
          type: String(q.type || '1'),
          parsedOptions: parseOpts(q)
        }))
        paperName.value = '随机练习'
      }
    } catch { ElMessage.error('加载失败') }
  } finally { isLoading.value = false }
}

async function handleSubmit() {
  try {
    await ElMessageBox.confirm('确定交卷？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
  } catch { return }

  isSubmitting.value = true
  try {
    const userId = localStorage.getItem('userId')
    const answerList = questionList.value.map(q => ({
      questionId: q.id,
      answer: getFinalAnswer(q.id)
    }))
    const body = {
      paperId: parseInt(examId),
      studentId: userId ? parseInt(userId) : 0,
      studentName: username.value,
      className: '',
      answers: answerList
    }
    const res = await request.post('/submissions/submit', body)
    if (res.success !== false) {
      const result = res.data || {}
      const used = 30 * 60 - totalSeconds.value
      const m = Math.floor(used / 60)
      const s = used % 60
      ElMessage.success('交卷成功！')
      localStorage.setItem('lastExamResult', JSON.stringify({
        examName: paperName.value,
        score: result.totalScore || result.objectiveScore || 0,
        totalScore: 100,
        submissionId: result.submissionId,
        useTime: m + '分' + s + '秒',
        paperId: parseInt(examId)
      }))
      router.push({ name: 'ScorePage', params: { id: result.submissionId } })
    } else {
      ElMessage.error(res.message || '交卷失败')
    }
  } catch (err) {
    ElMessage.error('交卷失败: ' + (err.message || '网络错误'))
    console.error(err)
  } finally { isSubmitting.value = false }
}

onMounted(() => { loadExamQuestions(); startTimer() })
onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.exam-page { min-height: 100vh; background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%); position: relative; overflow: hidden; }
.bg-decoration { position: absolute; width: 100%; height: 100%; top: 0; left: 0; z-index: 0; pointer-events: none; }
.circle { position: absolute; border-radius: 50%; background: rgba(255,255,255,0.4); animation: float 8s infinite ease-in-out; z-index: 0; }
.circle-1 { width:300px;height:300px;top:-100px;left:-100px; }
.circle-2 { width:200px;height:200px;bottom:-50px;right:-50px;animation-delay:2s; }
.circle-3 { width:150px;height:150px;bottom:50%;right:30%;animation-delay:4s; }
@keyframes float { 0%,100%{transform:translateY(0)rotate(0deg)} 50%{transform:translateY(30px)rotate(180deg)} }
.header { background:rgba(255,255,255,0.95);backdrop-filter:blur(10px);padding:0 20px;box-shadow:0 2px 12px rgba(0,0,0,0.05);position:relative;z-index:10; }
.header-content { max-width:1200px;margin:0 auto;display:flex;justify-content:space-between;align-items:center;height:60px; }
.title { font-size:20px;font-weight:600;margin:0;color:#409eff; }
.user-info { display:flex;align-items:center;gap:10px; }
.avatar { width:36px;height:36px;--el-avatar-bg-color:#e0f7ff;--el-avatar-text-color:#409eff; }
.username { font-size:14px;color:#606266;font-weight:500; }
.main-content { max-width:900px;margin:30px auto;padding:0 20px;position:relative;z-index:10; }
.countdown-card { margin-bottom:30px;padding:20px;text-align:center;border-radius:16px;background:rgba(255,255,255,0.95);box-shadow:0 8px 32px rgba(0,0,0,0.1); }
.countdown { color:#e64340;font-size:16px;font-weight:600; }
.question-item { padding:20px;margin-bottom:20px;border-radius:16px;background:rgba(255,255,255,0.95);box-shadow:0 8px 32px rgba(0,0,0,0.1);border:1px solid rgba(255,255,255,0.2); }
.question-title { font-size:16px;font-weight:600;color:#303133;margin:0 0 15px;padding-bottom:10px;border-bottom:1px solid #eee; }
.options-area { display:flex;flex-direction:column;gap:2px; }
.full-width { width:100%; }
.option-row { padding:6px 10px;border-radius:8px; }
.option-row:hover { background-color:#f0f7ff; }
:deep(.el-radio), :deep(.el-checkbox) { height:auto;padding:4px 0; }
:deep(.el-radio__label), :deep(.el-checkbox__label) { font-size:15px;white-space:normal; }
.opt-label { font-weight:600;color:#409eff;margin-right:4px; }
.opt-text { color:#333; }
.fill-area { margin-top:8px; }
:deep(.el-input__wrapper) { width:100%; }
.empty-tip { text-align:center;padding:60px;color:#909399;font-size:16px;border-radius:16px;background:rgba(255,255,255,0.95);box-shadow:0 8px 32px rgba(0,0,0,0.1); }
.submit-btn { text-align:center;margin-top:20px; }
</style>

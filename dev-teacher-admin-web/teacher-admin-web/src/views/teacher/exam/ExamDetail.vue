<template>
  <div class="box" v-loading="loading">
    <div class="detail-header">
      <el-button @click="$router.back()" text>← 返回</el-button>
      <h3 style="margin:10px 0">阅卷</h3>
    </div>

    <el-card v-if="record.id" class="info-card">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="学生">{{ record.studentName }}</el-descriptions-item>
        <el-descriptions-item label="试卷">{{ record.paperName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="record.status === 'GRADED' ? 'success' : 'warning'">{{ record.status === 'GRADED' ? '已批改' : '待批改' }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <div v-for="(item, idx) in answers" :key="item.id || idx" class="q-card">
      <div class="q-header">
        <span class="q-num">{{ idx + 1 }}.</span>
        <span class="q-type">{{ typeMap[item.type] || item.type }}</span>
        <span class="q-score">分值：{{ item.score }}</span>
      </div>
      <div class="q-content">{{ item.content || item.title }}</div>
      <div class="q-answer-row" v-if="item.correctAnswer">正确答案：<span class="correct">{{ item.correctAnswer }}</span></div>

      <div class="q-answer-row">
        学生答案：
        <span :class="getAnswerClass(item)">
          {{ getAnswerText(item) }}
        </span>
      </div>

      <div v-if="isSubjective(item)" class="grade-row">
        <span>教师给分：</span>
        <el-input-number v-model="item.teacherScore" :min="0" :max="item.score" size="small" style="width:120px" />
      </div>
      <div v-else class="grade-row" style="color:#909399;font-size:13px">
        {{ item.isCorrect === 1 ? '✓ 客观题自动判对' : '✗ 客观题自动判错' }}
      </div>
    </div>

    <div style="margin-top:20px;text-align:center">
      <el-button type="primary" @click="submitGrade" :loading="submitting" size="large">提交批改</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const record = ref({})
const answers = ref([])
const loading = ref(true)
const submitting = ref(false)

const typeMap = { 1: '单选', 2: '多选', 3: '判断', 4: '填空', 'SINGLE': '单选', 'MULTIPLE': '多选', 'JUDGE': '判断', 'FILL': '填空', 'SHORT': '简答' }

const isSubjective = (item) => {
  const t = String(item.type)
  return t === '4' || t === 'SHORT'
}

const getAnswerText = (item) => {
  if (!item.answerText?.trim()) {
    return '未作答'
  }
  if (item.isCorrect === 1) {
    return '答对'
  }
  return '答错'
}

const getAnswerClass = (item) => {
  if (!item.answerText?.trim()) {
    return 'wrong'
  }
  if (item.isCorrect === 1) {
    return 'correct'
  }
  return 'wrong'
}

const getDetail = async () => {
  try {
    const res = await request.get(`/exam/record/detail/${route.params.id}`)
    if (res.success !== false) {
      const data = res.data || res
      record.value = data.record || {}
      const rawAnswers = data.answers || []
      answers.value = rawAnswers.map(a => ({ ...a, teacherScore: a.score || 0 }))
    }
  } catch {}
  finally { loading.value = false }
}

const submitGrade = async () => {
  const items = answers.value.filter(a => isSubjective(a) && a.teacherScore !== undefined)
  if (items.length === 0) { ElMessage.warning('该提交没有主观题需要批改'); return }

  submitting.value = true
  try {
    const res = await request.post('/submissions/manual-grade', {
      submissionId: parseInt(route.params.id),
      teacherId: parseInt(localStorage.getItem('userId') || '0'),
      teacherName: localStorage.getItem('username') || '教师',
      items: items.map(a => ({ questionId: a.questionId, score: a.teacherScore || 0 }))
    })
    if (res.success !== false) { ElMessage.success('批改完成') }
    else ElMessage.error(res.message || '批改失败')
  } catch { ElMessage.error('批改失败') }
  finally { submitting.value = false }
}

onMounted(() => getDetail())
</script>

<style scoped>
.box { padding: 20px; }
.detail-header { margin-bottom: 20px; }
.info-card { margin-bottom: 20px; }
.q-card {
  background: #fff; border: 1px solid #e8e8e8; border-radius: 8px;
  padding: 16px; margin-bottom: 12px;
}
.q-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.q-num { font-weight: 600; color: #409eff; }
.q-type { font-size: 12px; padding: 1px 8px; border-radius: 4px; background: #909399; color: #fff; }
.q-score { margin-left: auto; font-size: 13px; color: #666; }
.q-content { font-size: 15px; margin-bottom: 6px; }
.q-answer-row { font-size: 14px; margin: 4px 0; }
.correct { color: #67c23a; font-weight: 500; }
.wrong { color: #f56c6c; font-weight: 500; }
.answered { color: #67c23a; font-weight: 500; }
.grade-row { margin-top: 10px; display: flex; align-items: center; gap: 8px; }
</style>
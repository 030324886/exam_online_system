<template>
  <div class="wrong-question-page">
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <span class="back-btn" @click="goBack">返回</span>
          <h1 class="title">在线刷题系统 - 我的错题本</h1>
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
          <el-col :span="8">
            <el-select v-model="filterKnowledge" placeholder="请选择知识点" clearable>
              <el-option v-for="k in knowledgeList" :key="k" :label="k" :value="k"></el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-select v-model="filterType" placeholder="请选择题型" clearable>
              <el-option label="单选题" value="SINGLE"></el-option>
              <el-option label="多选题" value="MULTIPLE"></el-option>
              <el-option label="判断题" value="JUDGE"></el-option>
              <el-option label="填空题" value="FILL"></el-option>
              <el-option label="简答题" value="SHORT"></el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="filterWrongQuestions" style="width: 100%; border-radius: 8px;">
              筛选
            </el-button>
          </el-col>
        </el-row>
      </div>

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
              <div class="overview-value">{{ knowledgeCount }}</div>
              <div class="overview-label">涉及知识点</div>
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

      <div class="wrong-list" v-if="displayList.length > 0">
        <div class="wrong-item function-card" v-for="(item, index) in displayList" :key="item.id">
          <div class="question-header">
            <span class="question-index">{{ index + 1 }}.</span>
            <span class="question-title">{{ item.content || item.title }}</span>
            <el-tag :type="item.reviewed ? 'success' : 'warning'" size="small">
              {{ item.reviewed ? '已复习' : '未复习' }}
            </el-tag>
          </div>

          <div class="question-options">
            <div class="option-item" v-for="(opt, optIdx) in getQuestionOptions(item)" :key="optIdx"
              :class="optionClass(optIdx, item)">
              {{ opt.label }}. {{ opt.content }}
            </div>
          </div>

          <div class="question-analysis" v-if="item.analysis">
            <div class="analysis-label">解析：</div>
            <div class="analysis-content">{{ item.analysis }}</div>
          </div>
          <div class="question-analysis" v-else-if="item.knowledgePoint">
            <div class="analysis-label">知识点：</div>
            <div class="analysis-content">{{ item.knowledgePoint }}</div>
          </div>

          <div class="question-actions">
            <el-button type="primary" size="small" @click="markReviewed(item.id)" v-if="!item.reviewed"
              style="border-radius: 8px;">标记已复习</el-button>
            <el-button type="success" size="small" disabled v-else style="border-radius: 8px;">已复习</el-button>
            <el-button type="danger" size="small" @click="deleteWrongQuestion(item.id)"
              style="border-radius: 8px; margin-left: 10px;">删除错题</el-button>
          </div>
        </div>
      </div>

      <div class="empty-tip stats-card" v-else>
        {{ isLoading ? '加载错题数据中...' : '暂无错题记录' }}
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request2 from '../../api/request2'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '学生用户')
const isLoading = ref(true)
const filterKnowledge = ref('')
const filterType = ref('')

const allWrongQuestions = ref([])
const knowledgeStats = ref([])
const knowledgeList = ref([])
const reviewedSet = ref(new Set())

const totalWrong = computed(() => allWrongQuestions.value.length)
const reviewedNum = computed(() => reviewedSet.value.size)
const knowledgeCount = computed(() => knowledgeStats.value.length)

const displayList = computed(() => {
  let list = allWrongQuestions.value
  if (filterKnowledge.value) {
    list = list.filter(item => item.knowledgePoint === filterKnowledge.value)
  }
  if (filterType.value) {
    list = list.filter(item => String(item.type) === String(filterType.value) || item.type === filterType.value)
  }
  return list
})

const getQuestionOptions = (q) => {
  if (!q.options) return []
  if (Array.isArray(q.options)) {
    return q.options.map((opt, idx) => {
      if (typeof opt === 'string') {
        return { label: String.fromCharCode(65 + idx), value: String.fromCharCode(65 + idx), content: opt }
      }
      return opt
    })
  }
  try {
    const parsed = typeof q.options === 'string' ? JSON.parse(q.options) : q.options
    if (Array.isArray(parsed)) {
      return parsed.map((opt, idx) => {
        if (typeof opt === 'string') {
          return { label: String.fromCharCode(65 + idx), value: String.fromCharCode(65 + idx), content: opt }
        }
        return opt
      })
    }
    return []
  } catch {
    return []
  }
}

const optionClass = (optIdx, item) => {
  const classes = []
  const ua = item.userAnswer
  const ca = item.correctAnswer
  if (ua !== undefined && (String(ua) === String(optIdx) || (Array.isArray(ua) && ua.includes(optIdx)))) {
    classes.push('user-answer')
  }
  if (ca !== undefined && (String(ca) === String(optIdx) || (Array.isArray(ca) && ca.includes(optIdx)))) {
    classes.push('correct-answer')
  }
  return classes
}

const goBack = () => {
  router.push({ name: 'StudentHome' })
}

const filterWrongQuestions = () => {
  ElMessage.success('筛选完成')
}

const markReviewed = (id) => {
  reviewedSet.value = new Set([...reviewedSet.value, id])
  ElMessage.success('标记为已复习成功')
}

const deleteWrongQuestion = (id) => {
  ElMessageBox.confirm('确定要删除这道错题吗？删除后无法恢复', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    allWrongQuestions.value = allWrongQuestions.value.filter(item => item.id !== id)
    ElMessage.success('删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const fetchData = async () => {
  try {
    isLoading.value = true
    const userId = localStorage.getItem('userId')
    if (!userId) return

    const [wrongRes, questionRes] = await Promise.allSettled([
      request2.get(`/wrong/stats/knowledge/${userId}`),
      request2.get('/question/list')
    ])

    if (wrongRes.status === 'fulfilled' && wrongRes.value?.code === 200) {
      const stats = wrongRes.value.data || []
      knowledgeStats.value = stats
      knowledgeList.value = stats.map(s => s.knowledgePoint).filter(Boolean)
    }

    if (questionRes.status === 'fulfilled' && questionRes.value?.code === 200) {
      const questions = questionRes.value.data || []
      const wrongKnowledgePoints = new Set(knowledgeStats.value.map(s => s.knowledgePoint).filter(Boolean))
      allWrongQuestions.value = questions.filter(q => q.knowledgePoint && wrongKnowledgePoints.has(q.knowledgePoint))
    }
  } catch (err) {
    console.error('获取错题数据失败：', err)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.wrong-question-page {
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
.stats-overview { margin-bottom: 30px; padding: 20px; }
.overview-title { font-size: 18px; font-weight: 600; margin: 0 0 20px 0; color: #303133; }
.overview-item { text-align: center; padding: 10px; }
.overview-value { font-size: 36px; font-weight: 700; color: #409eff; margin-bottom: 5px; }
.overview-label { font-size: 14px; color: #909399; }

.wrong-list { margin-bottom: 20px; }
.wrong-item { height: auto; padding: 20px; margin-bottom: 20px; display: flex; flex-direction: column; align-items: flex-start; justify-content: flex-start; cursor: default; }

.question-header { display: flex; align-items: center; gap: 10px; width: 100%; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px solid #eee; }
.question-index { font-weight: 600; color: #409eff; flex-shrink: 0; }
.question-title { flex: 1; font-size: 16px; color: #303133; }

.question-options { width: 100%; margin-bottom: 15px; display: flex; flex-direction: column; gap: 8px; }
.option-item { padding: 8px 10px; border-radius: 4px; font-size: 14px; color: #606266; }

.user-answer { background-color: #fef0f0; border: 1px solid #fbc4c4; color: #f56c6c; }
.correct-answer { background-color: #f0f9ff; border: 1px solid #91d5ff; color: #409eff; }

.question-analysis { width: 100%; margin-bottom: 15px; padding: 10px; background-color: #f8f9fa; border-radius: 4px; }
.analysis-label { font-weight: 600; color: #409eff; margin-bottom: 5px; display: block; }
.analysis-content { font-size: 14px; color: #606266; line-height: 1.5; }

.question-actions { width: 100%; text-align: right; }
.empty-tip { text-align: center; padding: 50px; color: #909399; font-size: 16px; margin-bottom: 20px; }

:deep(.stats-card) { border-radius: 16px; backdrop-filter: blur(10px); background: rgba(255, 255, 255, 0.95); box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); border: 1px solid rgba(255, 255, 255, 0.2); }
:deep(.function-card) { border-radius: 16px; backdrop-filter: blur(10px); background: rgba(255, 255, 255, 0.95); box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); border: 1px solid rgba(255, 255, 255, 0.2); }
:deep(.el-select), :deep(.el-button) { width: 100%; }

@media (max-width: 768px) {
  .main-content { padding: 0 10px; }
  .overview-value { font-size: 24px; }
}
</style>

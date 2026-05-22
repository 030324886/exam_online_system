<template>
  <div class="box">
    <h3>手动组卷</h3>
    <el-form :model="paperForm" label-width="100px" style="max-width:500px;margin:20px 0">
      <el-form-item label="试卷名称"><el-input v-model="paperForm.name" /></el-form-item>
      <el-form-item label="及格分数"><el-input-number v-model="paperForm.passScore" :min="0" :max="100" /></el-form-item>
    </el-form>

    <h4>可选试题</h4>
    <el-table :data="questionList" border @selection-change="handleSelect" v-loading="qLoading" max-height="400">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="content" label="题目内容" min-width="250" show-overflow-tooltip />
      <el-table-column label="题型" width="70">
        <template #default="s">{{ typeMap[s.row.type] }}</template>
      </el-table-column>
      <el-table-column prop="knowledgePoint" label="知识点" width="100" />
      <el-table-column label="分值" width="100">
        <template #default="scope">
          <el-input-number v-model="scoreMap[scope.row.id]" :min="1" :max="100" size="small" style="width:80px" />
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:20px">
      <span>已选 <b>{{ selectedList.length }}</b> 题，总分 <b>{{ totalScore }}</b></span>
      <el-button type="primary" style="margin-left:20px" @click="submitPaper" :loading="submitting">生成试卷</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import request2 from '@/utils/request2'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const questionList = ref([])
const selectedList = ref([])
const scoreMap = ref({})
const qLoading = ref(false)
const submitting = ref(false)

const paperForm = ref({ name: '', passScore: 60 })
const typeMap = { 1: '单选', 2: '多选', 3: '判断', 4: '填空' }

const totalScore = computed(() => selectedList.value.reduce((s, item) => s + (scoreMap.value[item.id] || 5), 0))

const getQuestionList = async () => {
  qLoading.value = true
  try {
    const res = await request2.get('/question/list')
    if (res.code === 200) {
      questionList.value = res.data || []
      questionList.value.forEach(q => { if (!scoreMap.value[q.id]) scoreMap.value[q.id] = 5 })
    }
  } catch { ElMessage.error('获取试题列表失败') }
  finally { qLoading.value = false }
}

const handleSelect = (val) => { selectedList.value = val }

const submitPaper = async () => {
  if (!paperForm.value.name) { ElMessage.warning('请输入试卷名称'); return }
  if (selectedList.value.length === 0) { ElMessage.warning('请至少选择一道题'); return }

  submitting.value = true
  try {
    const params = {
      name: paperForm.value.name,
      passScore: paperForm.value.passScore,
      createdBy: parseInt(localStorage.getItem('userId') || '0'),
      questions: selectedList.value.map((item, idx) => ({
        questionId: item.id,
        score: scoreMap.value[item.id] || 5,
        sortNo: idx + 1
      }))
    }
    const res = await request.post('/papers/manual', params)
    if (res.success !== false) { ElMessage.success('组卷成功'); router.push('/teacher/paper') }
    else ElMessage.error(res.message || '组卷失败')
  } catch { ElMessage.error('组卷失败') }
  finally { submitting.value = false }
}

onMounted(() => getQuestionList())
</script>

<style scoped>
.box { padding: 20px; }
</style>

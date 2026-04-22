<template>
  <div class="box">
    <h3>手动组卷</h3>
    <!-- 试卷基础信息 -->
    <el-form :model="paperForm" label-width="100px" style="margin-bottom:20px">
      <el-form-item label="试卷名称"><el-input v-model="paperForm.name" /></el-form-item>
      <el-form-item label="考试时长"><el-input v-model="paperForm.duration" /></el-form-item>
      <el-form-item label="总分"><el-input v-model="paperForm.totalScore" /></el-form-item>
    </el-form>

    <!-- 试题筛选与选择 -->
    <h4>可选试题</h4>
    <el-table :data="questionList" border @selection-change="handleSelect">
      <el-table-column type="selection" />
      <el-table-column prop="title" label="题目" />
      <el-table-column prop="type" label="题型" />
      <el-table-column prop="score" label="分值" />
    </el-table>

    <!-- 已选试题 -->
    <h4 style="margin-top:20px">已选试题</h4>
    <el-table :data="selectedList" border>
      <el-table-column prop="title" label="题目" />
      <el-table-column prop="score" label="分值" />
      <el-table-column label="操作"><template #default="scope"><el-button type="danger" @click="remove(scope.row)">移除</el-button></template></el-table-column>
    </el-table>

    <el-button type="primary" style="margin-top:20px" @click="submitPaper">生成试卷</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
const router = useRouter()

// 试卷信息
const paperForm = ref({ name: '', duration: 90, totalScore: 100 })
// 试题列表
const questionList = ref([])
// 已选试题
const selectedList = ref([])

// 获取所有试题
const getQuestionList = async () => {
  const res = await request.get('/question/list', { params: { page:1, size:100 }})
  questionList.value = res.records
}

// 勾选试题
const handleSelect = (val) => { selectedList.value = val }

// 移除试题
const remove = (row) => {
  selectedList.value = selectedList.value.filter(item => item.id !== row.id)
}

// 提交组卷
const submitPaper = async () => {
  const params = {
    paper: paperForm.value,
    questionIds: selectedList.value.map(item => item.id)
  }
  await request.post('/paper/create/manual', params)
  ElMessage.success('组卷成功')
  router.push('/teacher/home')
}

onMounted(() => getQuestionList())
</script>
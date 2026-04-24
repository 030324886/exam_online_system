<template>
  <div class="box" style="padding:20px">
    <h3>阅卷</h3>
    <div>学生：{{ record.username }}</div>
    <div>试卷：{{ record.paperName }}</div>
    <div>总分：{{ record.totalScore }}</div>
    <hr>

    <div v-for="(item, idx) in answers" :key="idx" style="margin-bottom:20px">
      <div>题目：{{ item.title }}</div>
      <div>学生答案：{{ item.userAnswer }}</div>
      <div>标准答案：{{ item.answer }}</div>
      <div>分值：{{ item.score }}</div>

      <div v-if="item.type === 'SHORT'">
        教师给分：
        <el-input v-model="item.teacherScore" style="width:100px" />
      </div>
    </div>

    <el-button type="primary" @click="submit">提交批改</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
const route = useRoute()
const id = route.params.id

const record = ref({})
const answers = ref([])

const getDetail = async () => {
  const res = await request.get(`/exam/record/detail/${id}`)
  record.value = res.record || {}
  answers.value = res.answers || []
}

const submit = async () => {
  await request.post('/exam/record/submit', {
    recordId: id,
    answers: answers.value
  })
  ElMessage.success('批改完成')
}

onMounted(() => getDetail())
</script>
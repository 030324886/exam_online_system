<template>
  <div class="box" style="padding:20px">
    <h3>试卷预览</h3>
    <div>名称：{{ paper.name }}</div>
    <div>总分：{{ paper.totalScore }}</div>
    <div>时长：{{ paper.duration }} 分钟</div>
    <hr>

    <div v-for="q in questions" :key="q.id" style="margin-bottom:20px">
      <div>题目：{{ q.title }}</div>
      <div>题型：{{ q.type === 'SINGLE' ? '单选' : '多选' }}</div>
      <div>选项A：{{ q.optionA }}</div>
      <div>选项B：{{ q.optionB }}</div>
      <div>答案：{{ q.answer }}</div>
      <div>分值：{{ q.score }}</div>
    </div>

    <el-button @click="$router.back()">返回</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useRoute } from 'vue-router'
const route = useRoute()
const id = route.params.id

const paper = ref({})
const questions = ref([])

const getDetail = async () => {
  const res = await request.get(`/paper/detail/${id}`)
  paper.value = res.paper || {}
  questions.value = res.questions || []
}

onMounted(() => getDetail())
</script>
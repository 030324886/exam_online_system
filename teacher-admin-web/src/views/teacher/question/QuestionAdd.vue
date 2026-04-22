<template>
  <div class="box">
    <el-form :model="form" label-width="100px">
      <el-form-item label="题目内容"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="题型"><el-select v-model="form.type"><el-option label="单选" value="SINGLE" /><el-option label="多选" value="MULTIPLE" /><el-option label="简答" value="SHORT" /></el-select></el-form-item>
      <el-form-item label="选项A"><el-input v-model="form.optionA" /></el-form-item>
      <el-form-item label="选项B"><el-input v-model="form.optionB" /></el-form-item>
      <el-form-item label="正确答案"><el-input v-model="form.answer" /></el-form-item>
      <el-form-item label="分值"><el-input v-model="form.score" /></el-form-item>
      <el-form-item><el-button type="primary" @click="submit">提交</el-button></el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
const router = useRouter()
const form = ref({ title: '', type: 'SINGLE', optionA: '', optionB: '', answer: '', score: 1 })

const submit = async () => {
  await request.post('/question/add', form.value)
  ElMessage.success('新增成功')
  router.push('/teacher/question')
}
</script>
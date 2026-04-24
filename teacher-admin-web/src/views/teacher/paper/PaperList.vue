<template>
  <div class="box">
    <h3>试卷管理</h3>
    <el-table :data="list" border>
      <el-table-column label="ID" prop="id" />
      <el-table-column label="试卷名称" prop="name" />
      <el-table-column label="总分" prop="totalScore" />
      <el-table-column label="时长(分钟)" prop="duration" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="toDetail(scope.row.id)">预览</el-button>
          <el-button type="warning" @click="publish(scope.row.id)">发布考试</el-button>
          <el-button type="danger" @click="del(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
const router = useRouter()

const list = ref([])

// 获取试卷列表
const getList = async () => {
  const res = await request.get('/paper/list')
  list.value = res
}

// 去预览
const toDetail = (id) => {
  router.push(`/teacher/paper/detail/${id}`)
}

// 发布考试
const publish = async (id) => {
  await ElMessageBox.confirm('确定发布此试卷？学生将可参与考试')
  await request.post(`/exam/publish/${id}`)
  ElMessage.success('发布成功')
}

// 删除试卷
const del = async (id) => {
  await ElMessageBox.confirm('确定删除？')
  await request.delete(`/paper/delete/${id}`)
  ElMessage.success('删除成功')
  getList()
}

onMounted(() => getList())
</script>
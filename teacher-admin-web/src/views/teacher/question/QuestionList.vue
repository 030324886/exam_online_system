<template>
  <div class="box">
    <el-button type="primary" @click="$router.push('/teacher/question/add')">新增试题</el-button>
    <el-table :data="list" border style="width:100%;margin-top:10px">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="title" label="题目内容" />
      <el-table-column prop="type" label="题型" />
      <el-table-column prop="score" label="分值" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="$router.push(`/teacher/question/edit/${scope.row.id}`)">编辑</el-button>
          <el-button type="danger" @click="del(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" @size-change="getList" @current-change="getList" layout="total,prev,pager,next,jumper" style="margin-top:10px;text-align:right" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
const page = ref(1), size = ref(10), list = ref([]), total = ref(0)

// 获取试题列表
const getList = async () => {
  const res = await request.get('/question/list', { params: { page: page.value, size: size.value }})
  list.value = res.records
  total.value = res.total
}

// 删除试题
const del = async (id) => {
  await ElMessageBox.confirm('确定删除?')
  await request.delete(`/question/delete/${id}`)
  ElMessage.success('删除成功')
  getList()
}

onMounted(() => getList())
</script>
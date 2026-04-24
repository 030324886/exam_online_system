<template>
  <div class="box">
    <h3>考试记录</h3>
    <el-table :data="list" border>
      <el-table-column label="学生" prop="username" />
      <el-table-column label="试卷" prop="paperName" />
      <el-table-column label="得分" prop="score" />
      <el-table-column label="状态" prop="status">
        <template #default="scope">
          <span>{{ scope.row.status === 0 ? '待批改' : '已完成' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="toDetail(scope.row.id)">
            查看/阅卷
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'
const router = useRouter()
const list = ref([])

const getList = async () => {
  const res = await request.get('/exam/record/list')
  list.value = res
}

const toDetail = (id) => {
  router.push(`/teacher/exam/detail/${id}`)
}

onMounted(() => getList())
</script>
<template>
  <div class="box">
    <h3>考试记录</h3>
    <el-table :data="list" border style="margin-top:15px" v-loading="loading">
      <el-table-column label="学生" prop="studentName" min-width="100" />
      <el-table-column label="试卷" prop="paperName" min-width="200" />
      <el-table-column label="客观题得分" prop="objectiveScore" width="100" />
      <el-table-column label="总分" prop="totalScore" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="s">
          <el-tag :type="s.row.status === 'GRADED' ? 'success' : 'warning'" size="small">
            {{ s.row.status === 'GRADED' ? '已批改' : '待批改' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="toDetail(scope.row.id)">查看/阅卷</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const list = ref([])
const loading = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/exam/record/list')
    list.value = Array.isArray(res) ? res : res.data || []
  } catch { ElMessage.error('获取考试记录失败') }
  finally { loading.value = false }
}

const toDetail = (id) => router.push(`/teacher/exam/detail/${id}`)

onMounted(() => getList())
</script>

<style scoped>
.box { padding: 20px; }
</style>

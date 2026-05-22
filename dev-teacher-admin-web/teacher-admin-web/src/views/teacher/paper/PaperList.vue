<template>
  <div class="box">
    <h3>试卷管理</h3>
    <el-table :data="list" border style="margin-top:15px" v-loading="loading">
      <el-table-column label="ID" prop="id" width="60" />
      <el-table-column label="试卷名称" prop="name" min-width="180" />
      <el-table-column label="总分" prop="totalScore" width="70" />
      <el-table-column label="时长(分钟)" prop="duration" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="s"><el-tag :type="s.row.status === 2 ? 'success' : 'info'" size="small">{{ s.row.status === 2 ? '已发布' : '草稿' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="toDetail(scope.row.id)">预览</el-button>
          <el-button size="small" type="warning" @click="publish(scope.row.id)">发布考试</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.id)">删除</el-button>
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
const loading = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/paper/list')
    list.value = Array.isArray(res) ? res : res.data || []
  } catch { ElMessage.error('获取试卷列表失败') }
  finally { loading.value = false }
}

const toDetail = (id) => router.push(`/teacher/paper/detail/${id}`)

const publish = async (id) => {
  try {
    await ElMessageBox.confirm('确定发布此试卷？学生将可参与考试')
    const res = await request.post(`/papers/publish/${id}`)
    if (res.success !== false) { ElMessage.success('发布成功'); getList() }
    else ElMessage.error(res.message || '发布失败')
  } catch {}
}

const del = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除？')
    const res = await request.delete(`/papers/${id}`)
    if (res.success !== false) { ElMessage.success('删除成功'); getList() }
    else ElMessage.error(res.message || '删除失败')
  } catch {}
}

onMounted(() => getList())
</script>

<style scoped>
.box { padding: 20px; }
</style>

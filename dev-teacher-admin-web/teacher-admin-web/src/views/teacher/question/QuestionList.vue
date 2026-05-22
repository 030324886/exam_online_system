<template>
  <div class="box">
    <div class="toolbar">
      <el-button type="primary" @click="$router.push('/teacher/question/add')">新增试题</el-button>
      <span style="margin-left:10px;color:#909399;font-size:13px">共 {{ total }} 题</span>
    </div>
    <el-table :data="list" border style="width:100%;margin-top:10px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="content" label="题目内容" min-width="250" show-overflow-tooltip />
      <el-table-column label="题型" width="80">
        <template #default="s">{{ typeMap[s.row.type] }}</template>
      </el-table-column>
      <el-table-column label="正确答案" width="100">
        <template #default="s">{{ s.row.answer }}</template>
      </el-table-column>
      <el-table-column prop="knowledgePoint" label="知识点" width="120" />
      <el-table-column label="难度" width="70">
        <template #default="s"><span v-if="s.row.difficulty">{{ '★'.repeat(s.row.difficulty) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="$router.push(`/teacher/question/edit/${scope.row.id}`)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request2 from '@/utils/request2'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const total = ref(0)
const loading = ref(false)

const typeMap = { 1: '单选', 2: '多选', 3: '判断', 4: '填空', 'SINGLE': '单选', 'MULTIPLE': '多选', 'JUDGE': '判断', 'FILL': '填空', 'SHORT': '简答' }

const getList = async () => {
  loading.value = true
  try {
    const res = await request2.get('/question/list')
    if (res.code === 200) {
      const data = res.data || []
      list.value = data
      total.value = data.length
    }
  } catch { ElMessage.error('获取试题列表失败') }
  finally { loading.value = false }
}

const del = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此题？')
    const res = await request2.delete(`/question/${id}`)
    if (res.code === 200) { ElMessage.success('删除成功'); getList() }
  } catch { }
}

onMounted(() => getList())
</script>

<style scoped>
.box { padding: 20px; }
.toolbar { display: flex; align-items: center; }
</style>

<template>
  <div class="box">
    <h3>成绩统计</h3>
    <el-table :data="list" border style="margin-top:15px" v-loading="loading">
      <el-table-column label="试卷" prop="paperName" min-width="200" />
      <el-table-column label="参考人数" prop="submissionCount" width="100" />
      <el-table-column label="平均分" width="100">
        <template #default="s">{{ (s.row.avgScore || s.row.averageScore)?.toFixed(1) }}</template>
      </el-table-column>
      <el-table-column label="最高分" prop="highestScore" width="80" />
      <el-table-column label="最低分" prop="lowestScore" width="80" />
      <el-table-column label="及格率" width="100">
        <template #default="s">{{ ((s.row.passRate || 0) * 100).toFixed(1) }}%</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)

const getStats = async () => {
  loading.value = true
  try {
    const res = await request.get('/statistics/class-score')
    const data = res.data || res
    list.value = Array.isArray(data) ? data : data ? [data] : []
  } catch { ElMessage.error('获取成绩统计失败') }
  finally { loading.value = false }
}

onMounted(() => getStats())
</script>

<style scoped>
.box { padding: 20px; }
</style>

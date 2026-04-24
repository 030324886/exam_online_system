<template>
  <div class="box">
    <h3>成绩统计</h3>
    <el-table :data="list" border>
      <el-table-column label="试卷" prop="paperName" />
      <el-table-column label="参考人数" prop="userCount" />
      <el-table-column label="平均分" prop="avgScore" />
      <el-table-column label="最高分" prop="maxScore" />
      <el-table-column label="最低分" prop="minScore" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const list = ref([])

const getStats = async () => {
  const res = await request.get('/exam/stats')
  list.value = res
}

onMounted(() => getStats())
</script>
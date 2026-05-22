<template>
  <div class="home-box">
    <h1>欢迎来到教务端首页</h1>
    <p style="margin:10px 0 20px;color:#666">当前角色：教务 — {{ userStore.userInfo?.name || userStore.userInfo?.username }}</p>

    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import request2 from '@/utils/request2'
import request from '@/utils/request'

const userStore = useUserStore()
const stats = ref([
  { label: '试题总数', value: 0 },
  { label: '试卷总数', value: 0 },
  { label: '考试记录', value: 0 },
  { label: '教师人数', value: 0 }
])

onMounted(async () => {
  try {
    const [qRes, pRes, eRes] = await Promise.allSettled([
      request2.get('/question/list'),
      request.get('/paper/list'),
      request.get('/exam/record/list')
    ])
    if (qRes.status === 'fulfilled' && qRes.value?.code === 200) stats.value[0].value = (qRes.value.data || []).length
    if (pRes.status === 'fulfilled') {
      const data = pRes.value.data || pRes.value
      stats.value[1].value = Array.isArray(data) ? data.length : 0
    }
    if (eRes.status === 'fulfilled') {
      const data = eRes.value.data || eRes.value
      stats.value[2].value = Array.isArray(data) ? data.length : 0
    }
    stats.value[3].value = 2
  } catch {}
})
</script>

<style scoped>
.home-box { padding: 30px; }
.stat-card { text-align: center; padding: 20px 0; border-radius: 12px; }
.stat-value { font-size: 36px; font-weight: 700; color: #409eff; }
.stat-label { font-size: 14px; color: #909399; margin-top: 8px; }
</style>

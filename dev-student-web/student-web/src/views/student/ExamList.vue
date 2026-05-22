<template>
  <div class="exam-list-page">
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <span class="back-btn" @click="goBack">返回</span>
          <h1 class="title">在线刷题系统 - 选择模考</h1>
        </div>
        <div class="user-info">
          <el-avatar icon="el-icon-user" class="avatar"></el-avatar>
          <span class="username">{{ username }}</span>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="loading-tip" v-if="isLoading">加载试卷列表中...</div>

      <template v-else>
        <div class="exam-card" v-for="p in paperList" :key="p.id" @click="startExam(p.id)">
          <div class="exam-left">
            <div class="exam-icon">
              <el-icon size="36"><Document /></el-icon>
            </div>
          </div>
          <div class="exam-center">
            <div class="exam-name">{{ p.name }}</div>
            <div class="exam-meta">
              <span>总分 <b>{{ p.totalScore || 0 }}</b> 分</span>
              <span class="meta-sep">|</span>
              <span>时长 <b>{{ p.durationMinutes || p.duration || 60 }}</b> 分钟</span>
            </div>
          </div>
          <div class="exam-right">
            <el-button type="primary" size="large" round @click.stop="startExam(p.id)">
              开始考试
            </el-button>
          </div>
        </div>

        <div v-if="paperList.length === 0" class="empty-tip">
          暂无可用试卷，请联系教师发布
        </div>
      </template>
    </div>

    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import request from '../../api/request'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '学生用户')
const paperList = ref([])
const isLoading = ref(true)

const fetchPapers = async () => {
  try {
    isLoading.value = true
    const res = await request.get('/papers/list')
    if (res.success !== false) {
      const data = res.data || res
      paperList.value = Array.isArray(data) ? data : []
    }
  } catch {
    ElMessage.error('获取试卷列表失败')
  } finally {
    isLoading.value = false
  }
}

const startExam = (id) => {
  router.push({ name: 'ExamPage', params: { id } })
}

const goBack = () => {
  router.push('/student/home')
}

onMounted(() => fetchPapers())
</script>

<style scoped>
.exam-list-page { min-height: 100vh; background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%); position: relative; overflow: hidden; }
.bg-decoration { position: absolute; width: 100%; height: 100%; top: 0; left: 0; z-index: 0; pointer-events: none; }
.circle { position: absolute; border-radius: 50%; background: rgba(255, 255, 255, 0.4); animation: float 8s infinite ease-in-out; z-index: 0; }
.circle-1 { width: 300px; height: 300px; top: -100px; left: -100px; animation-delay: 0s; }
.circle-2 { width: 200px; height: 200px; bottom: -50px; right: -50px; animation-delay: 2s; }
.circle-3 { width: 150px; height: 150px; bottom: 50%; right: 30%; animation-delay: 4s; }
@keyframes float { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(30px) rotate(180deg); } }
.header { background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); padding: 0 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); position: relative; z-index: 10; }
.header-content { max-width: 1000px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; height: 60px; }
.header-left { display: flex; align-items: center; gap: 15px; }
.back-btn { color: #409eff; font-size: 16px; cursor: pointer; transition: color 0.3s; }
.back-btn:hover { color: #66b1ff; text-decoration: underline; }
.title { font-size: 20px; font-weight: 600; margin: 0; color: #409eff; }
.user-info { display: flex; align-items: center; gap: 10px; }
.avatar { width: 36px; height: 36px; --el-avatar-bg-color: #e0f7ff; --el-avatar-text-color: #409eff; }
.username { font-size: 14px; color: #606266; font-weight: 500; }
.main-content { max-width: 800px; margin: 30px auto; padding: 0 20px; position: relative; z-index: 10; }
.loading-tip { text-align: center; padding: 80px; color: #909399; font-size: 16px; }

.exam-card {
  display: flex; align-items: center; gap: 20px;
  background: rgba(255, 255, 255, 0.95); border-radius: 16px;
  padding: 24px; margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer; transition: all 0.3s ease;
}
.exam-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15); }

.exam-left { flex-shrink: 0; }
.exam-icon { color: #409eff; }
.exam-center { flex: 1; min-width: 0; }
.exam-name { font-size: 18px; font-weight: 600; color: #303133; margin-bottom: 6px; }
.exam-meta { font-size: 14px; color: #909399; display: flex; align-items: center; gap: 8px; }
.meta-sep { color: #ddd; }
.exam-right { flex-shrink: 0; }

.empty-tip { text-align: center; padding: 80px; color: #909399; font-size: 16px; background: rgba(255, 255, 255, 0.9); border-radius: 16px; }

@media (max-width: 768px) {
  .exam-card { flex-direction: column; text-align: center; }
  .exam-right .el-button { width: 100%; }
}
</style>

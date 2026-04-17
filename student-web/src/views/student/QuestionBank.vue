<template>
  <div class="question-bank-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 水印 -->
    <div class="watermark">福州大学至诚学院</div>

    <!-- 内容容器 -->
    <div class="question-bank-container">
      <!-- 自定义返回头（核心修复：替换el-page-header） -->
      <div class="custom-page-header">
        <el-button
          icon="el-icon-arrow-left"
          type="text"
          @click="goBack"
          class="back-btn"
        >返回</el-button>
        <h2 class="page-title">题库列表</h2>
      </div>

      <!-- 筛选卡片 -->
      <el-card class="filter-card" shadow="hover">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="科目" class="form-item">
            <el-select
              v-model="filterForm.subjectId"
              placeholder="请选择科目"
              @change="getChapters"
              size="large"
              class="filter-select"
            >
              <el-option
                v-for="item in subjectList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="章节" class="form-item">
            <el-select
              v-model="filterForm.chapterId"
              placeholder="请选择章节"
              size="large"
              class="filter-select"
            >
              <el-option
                v-for="item in chapterList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 章节列表 -->
      <el-card class="chapter-list-card" shadow="hover">
        <div
          class="chapter-item"
          v-for="chapter in chapterList"
          :key="chapter.id"
          @click="goToDoExercise(chapter.id)"
        >
          <div class="chapter-info">
            <div class="chapter-title">{{ chapter.name }}</div>
            <div class="chapter-desc">{{ chapter.questionCount }}道题目</div>
          </div>
          <el-button
            type="primary"
            size="large"
            class="exercise-btn"
          >开始刷题</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSubjectList, getChapterList } from '../../api/questionBank'

const router = useRouter()

// 筛选表单
const filterForm = ref({
  subjectId: '',
  chapterId: ''
})

// 模拟数据
const subjectList = ref([
  { id: 1, name: 'JavaScript' },
  { id: 2, name: 'Vue3' },
  { id: 3, name: 'HTML/CSS' }
])

const chapterList = ref([
  { id: 101, name: 'Vue3基础语法', questionCount: 20 },
  { id: 102, name: 'Vue3组件通信', questionCount: 15 },
  { id: 103, name: 'Vue3路由', questionCount: 18 }
])

// 核心修复：返回按钮直接跳主页面（替换成你的主页面路由！）
const goBack = () => {
  // ！！！关键：修改为你的真实主页面路由（比如 /home /student/home）！！！
  router.push('/student/home')
}

// 跳转刷题页：用replace避免历史记录堆积
const goToDoExercise = (chapterId) => {
  router.replace({
    name: 'DoExercise',
    params: { chapterId }
  })
}

// 获取科目列表
const getSubjects = async () => {
  try {
    ElMessage.success('科目列表加载成功（模拟数据）')
  } catch (err) {
    ElMessage.error('获取科目列表失败')
    console.error(err)
  }
}

// 获取章节列表
const getChapters = async () => {
  if (!filterForm.value.subjectId) return
  try {
    ElMessage.success('章节列表加载成功（模拟数据）')
  } catch (err) {
    ElMessage.error('获取章节列表失败')
    console.error(err)
  }
}

onMounted(() => {
  getSubjects()
})
</script>

<style scoped>
.question-bank-page {
  width: 100vw;
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow: hidden;
  padding: 20px 0;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  animation: float 8s infinite ease-in-out;
  z-index: 0;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  bottom: 50%;
  right: 30%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(30px) rotate(180deg);
  }
}

.watermark {
  position: absolute;
  top: 40px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 48px;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.4);
  z-index: 1;
  white-space: nowrap;
  user-select: none;
  pointer-events: none;
  letter-spacing: 4px;
}

.question-bank-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 10;
  backdrop-filter: blur(5px);
}

/* 自定义返回头样式 */
.custom-page-header {
  margin-bottom: 30px;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 15px;
}

.back-btn {
  font-size: 18px;
  font-weight: 500;
  color: #409eff;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background-color: #f0f9ff;
  color: #66b1ff;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.filter-card {
  margin-bottom: 20px;
  padding: 25px;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(10px);
  border-radius: 16px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
}

.filter-form {
  padding: 5px 0;
  width: 100%;
}

.form-item {
  margin-right: 20px !important;
}

:deep(.el-form-item__label) {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
}

.filter-select {
  width: 200px;
  border-radius: 8px;
}

:deep(.el-select .el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: none;
}

.chapter-list-card {
  padding: 30px;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(10px);
  border-radius: 16px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
}

.chapter-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  margin-bottom: 10px;
}

.chapter-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.chapter-item:hover {
  background-color: #f0f9ff;
  transform: translateX(5px);
}

.chapter-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.chapter-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.chapter-desc {
  color: #909399;
  font-size: 14px;
}

.exercise-btn {
  border-radius: 8px;
  padding: 8px 24px;
  font-weight: 500;
  font-size: 16px;
  background: #409eff;
  border: none;
}

:deep(.el-button--primary:hover) {
  background: #66b1ff;
}
</style>
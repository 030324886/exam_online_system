<template>
  <div class="wrong-question-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 水印 -->
    <div class="watermark">福州大学至诚学院</div>

    <!-- 内容容器 -->
    <div class="wrong-question-container">
      <!-- 自定义返回头 -->
      <div class="custom-page-header">
        <el-button
          icon="el-icon-arrow-left"
          type="text"
          @click="goBack"
          class="back-btn"
        >返回</el-button>
        <h2 class="page-title">我的错题本</h2>
      </div>

      <!-- 错题列表卡片 -->
      <el-card class="wrong-question-card" shadow="hover" v-loading="loading">
        <el-table
          :data="wrongQuestionList"
          border
          stripe
          class="wrong-question-table"
          :empty-text="loading ? '加载中...' : '暂无错题'"
        >
          <el-table-column
            prop="id"
            label="序号"
            width="80"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="content"
            label="题目内容"
            min-width="400"
          ></el-table-column>
          <el-table-column
            prop="chapterName"
            label="所属章节"
            width="150"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="createTime"
            label="添加时间"
            width="180"
            align="center"
          ></el-table-column>
          <el-table-column
            label="操作"
            width="120"
            align="center"
          >
            <template #default="scope">
              <el-button
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row.id)"
                class="delete-btn"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          class="pagination"
        ></el-pagination>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
// 错题列表（模拟数据）
const wrongQuestionList = ref([
  {
    id: 1,
    content: 'Vue3中用于创建响应式数据的API是？',
    chapterName: 'Vue3基础语法',
    createTime: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    content: 'Vue3的组合式API中，setup函数的执行时机是？',
    chapterName: 'Vue3组件通信',
    createTime: '2024-01-16 14:20:00'
  },
  {
    id: 3,
    content: 'Vue Router中编程式导航使用的方法是？',
    chapterName: 'Vue3路由',
    createTime: '2024-01-17 09:15:00'
  }
])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(wrongQuestionList.value.length)

// 返回主页面
const goBack = () => {
  router.push('/student/home')
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  // 模拟重新加载数据
  loadWrongQuestions()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  // 模拟重新加载数据
  loadWrongQuestions()
}

// 加载错题列表
const loadWrongQuestions = async () => {
  try {
    loading.value = true
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    // 实际项目中这里会调用后端接口获取错题列表
    console.log('加载错题列表成功')
  } catch (error) {
    ElMessage.error('加载错题列表失败，请重试！')
    console.error('加载错题错误：', error)
  } finally {
    loading.value = false
  }
}

// 删除错题
const handleDelete = async (questionId) => {
  try {
    await ElMessageBox.confirm('确定要删除这道错题吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      // 模拟删除API请求
      await new Promise(resolve => setTimeout(resolve, 500))
      // 从列表中移除
      wrongQuestionList.value = wrongQuestionList.value.filter(item => item.id !== questionId)
      total.value = wrongQuestionList.value.length
      ElMessage.success('删除成功！')
    }).catch(() => {
      ElMessage.info('已取消删除')
    })
  } catch (error) {
    ElMessage.error('删除失败，请重试！')
    console.error('删除错题错误：', error)
  }
}

// 初始化加载错题
onMounted(() => {
  loadWrongQuestions()
})
</script>

<style scoped>
/* 基础页面样式 - 与登录页保持一致的渐变背景 */
.wrong-question-page {
  width: 100vw;
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  padding: 0;
  margin: 0;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 - 复用登录页的浮动动画效果 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  animation: float 8s infinite ease-in-out;
  z-index: 0;
  opacity: 0.8;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 30%;
  animation-delay: 4s;
}

/* 浮动动画 - 与登录页一致 */
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(30px) rotate(180deg);
  }
}

/* 水印样式 - 与登录页保持一致 */
.watermark {
  position: absolute;
  top: 80px;
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

/* 内容容器 */
.wrong-question-container {
  position: relative;
  z-index: 10;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 自定义页面头部 */
.custom-page-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.back-btn {
  font-size: 18px;
  margin-right: 15px;
  color: #409eff;
  font-weight: 500;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 错题卡片样式 - 与登录卡片风格统一 */
.wrong-question-card {
  padding: 40px 30px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 表格样式 */
.wrong-question-table {
  --el-table-header-text-color: #606266;
  --el-table-row-hover-bg-color: #f0f7ff;
  --el-table-border-color: #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  background-color: #f8f9fa;
}

:deep(.el-table-cell) {
  padding: 12px 0;
  font-size: 14px;
  color: #303133;
}

/* 删除按钮样式 */
.delete-btn {
  color: #f56c6c;
  font-size: 14px;
  font-weight: 500;
}

.delete-btn:hover {
  color: #f78989;
}

/* 分页样式 */
.pagination {
  margin-top: 20px;
  text-align: right;
}

:deep(.el-pagination) {
  --el-pagination-text-color: #606266;
  --el-pagination-button-hover-color: #409eff;
}

:deep(.el-pagination__total) {
  font-size: 14px;
  color: #606266;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .wrong-question-container {
    padding: 20px 15px;
  }

  .wrong-question-card {
    padding: 20px 15px;
  }

  .page-title {
    font-size: 24px;
  }

  .watermark {
    font-size: 36px;
  }
}
</style>
<template>
  <!-- 根容器：统一全局样式，避免组件样式溢出 -->
  <div id="app">
    <!-- 新增：全局加载遮罩（请求题目/提交试卷时展示） -->
    <div v-if="isLoading" class="global-loading">
      <el-loading-spinner />
      <p>加载中...</p>
    </div>
    <router-view />
  </div>
</template>

<script setup>
import { getCurrentInstance, onMounted, onErrorCaptured, ref, provide } from 'vue'
import { ElMessage } from 'element-plus' // 引入Element Plus提示组件

// 关键：获取Vue应用实例（替代window挂载）
const app = getCurrentInstance().appContext.app

// 新增：全局加载状态（通过provide供子组件调用）
const isLoading = ref(false)
provide('setLoading', (status) => {
  isLoading.value = status
})

// 新增：全局组件错误捕获（防止刷题/考试页面崩溃）
onErrorCaptured((err, instance, info) => {
  console.error('全局组件渲染错误:', err, instance, info)
  ElMessage.error('页面加载异常，请刷新重试！')
  return true // 阻止错误向上传播
})

// 新增：全局接口错误拦截（需确保api/request.js是axios封装实例）
import request from '@/api/request.js'
request.interceptors.response.use(
  (response) => response,
  (error) => {
    // 考试场景专属错误处理
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录！')
      window.location.href = '/#/login' // 跳转到登录页（匹配你的路由）
    } else if (error.response?.status === 403) {
      ElMessage.error('无考试权限，请联系管理员！')
    } else if (error.response?.status === 500) {
      ElMessage.error('服务器异常，交卷失败请稍后重试！')
    } else {
      ElMessage.error('网络异常，请检查网络后重试！')
    }
    return Promise.reject(error)
  }
)

// 原有：挂载全局通用工具方法（支撑考试页面）
onMounted(() => {
  // 1. 全局倒计时格式化方法（秒 → 时:分:秒）
  app.config.globalProperties.$formatCountdown = (seconds) => {
    const h = Math.floor(seconds / 3600).toString().padStart(2, '0')
    const m = Math.floor((seconds % 3600) / 60).toString().padStart(2, '0')
    const s = (seconds % 60).toString().padStart(2, '0')
    return `${h}:${m}:${s}`
  }

  // 2. 全局防抖函数（交卷/提交防重复请求）
  app.config.globalProperties.$debounce = (fn, delay = 500) => {
    let timer = null
    return (...args) => {
      if (timer) clearTimeout(timer)
      timer = setTimeout(() => fn.apply(this, args), delay)
    }
  }

  // 新增：监听页面刷新/关闭（考试中提示防答案丢失）
  window.addEventListener('beforeunload', (e) => {
    const currentRoute = window.location.hash
    if (currentRoute.includes('/exam/')) { // 匹配你的考试页面路由
      e.preventDefault()
      e.returnValue = '考试中刷新/关闭页面会导致答案丢失，确定离开？'
      return '考试中刷新/关闭页面会导致答案丢失，确定离开？'
    }
  })
})
</script>

<style>
/* 原有所有样式完整保留 */
/* 全局样式重置：解决默认边距/盒模型问题 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 全局body样式：避免页面滚动异常 */
body {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", Arial, sans-serif;
  background-color: #f5f7fa;
  overflow-x: hidden; /* 防止横向滚动 */
}

/* 根容器高度：确保页面占满视口 */
#app {
  width: 100vw;
  min-height: 100vh;
}

/* 全局滚动条优化（可选） */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-thumb {
  border-radius: 4px;
  background-color: rgba(144, 147, 153, 0.5);
}
::-webkit-scrollbar-track {
  background-color: transparent;
}

/* 新增：全局加载遮罩样式 */
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* 确保覆盖所有元素 */
}
.global-loading p {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

/* 新增：响应式适配（手机/平板刷题排版） */
/* 平板端 */
@media (max-width: 768px) {
  #app {
    padding: 0 10px;
  }
  .question-card { /* 匹配你题目卡片的类名 */
    width: 100% !important;
    margin: 10px 0 !important;
  }
  .countdown { /* 匹配你倒计时组件的类名 */
    font-size: 14px !important;
    padding: 5px 0 !important;
  }
}
/* 手机端 */
@media (max-width: 480px) {
  .el-button { /* Element Plus按钮适配 */
    width: 100% !important;
    margin-bottom: 10px !important;
  }
  .el-table { /* 考试记录表格适配 */
    overflow-x: auto;
  }
}
</style>
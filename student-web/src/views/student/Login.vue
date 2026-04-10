<template>
  <div class="login-page">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="watermark">福州大学至诚学院</div>

    <el-card class="login-card" shadow="hover">
      <div class="login-header">
        <h2 class="login-title">学生登录</h2>
        <p class="login-subtitle">欢迎使用在线考试系统</p>
      </div>

      <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          label-width="80px"
          class="login-form"
      >
        <el-form-item label="学号" prop="username">
          <el-input
              v-model="form.username"
              placeholder="请输入学号"
              prefix-icon="User"
              size="large"
              clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
          />
        </el-form-item>

        <el-form-item>
          <div class="btn-group">
            <el-button
                type="primary"
                size="large"
                @click="handleLogin"
                :loading="loading"
                class="login-btn"
            >
              登录
            </el-button>
            <el-button size="large" @click="handleReset" class="reset-btn">
              重置
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const rules = ref({
  username: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { min: 6, max: 12, message: '学号长度为6-12位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
})

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    localStorage.setItem('token', 'student-token-123456')
    ElMessage.success('登录成功')
    router.push('/student/home')
  } catch (error) {
    ElMessage.error('登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 🔥 白色文字水印（在上方） */
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

.login-card {
  width: 420px;
  padding: 40px 30px;
  border-radius: 16px;
  z-index: 10;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  padding: 0 10px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 10px;
}

.login-btn {
  flex: 1;
  border-radius: 8px;
  font-weight: 500;
  font-size: 16px;
  background: #409eff;
  border: none;
}

.reset-btn {
  flex: 1;
  border-radius: 8px;
  font-weight: 500;
  font-size: 16px;
  color: #606266;
}
</style>
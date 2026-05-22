<template>
  <div class="login-page">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="watermark">福州大学至诚学院</div>

    <div class="left-section">
      <div class="logo-container">
        <h1 class="system-name">在线刷题系统</h1>
        <p class="system-desc">福州大学至诚学院 - 高效学习平台</p>
      </div>
      <div class="illustration">
        <el-icon size="120"><Reading /></el-icon>
      </div>
    </div>

    <div class="right-section">
      <div class="login-card">
        <h2 class="login-title">学生登录</h2>
        <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item label="学号" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入学号"
              size="large"
              prefix-icon="el-icon-user"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="el-icon-lock"
              show-password
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="验证码" prop="code">
            <el-row :gutter="10">
              <el-col :span="16">
                <el-input
                  v-model="form.code"
                  placeholder="请输入验证码"
                  size="large"
                  prefix-icon="el-icon-check"
                  clearable
                ></el-input>
              </el-col>
              <el-col :span="8">
                <div class="code-img" @click="generateCode">
                  {{ randomCode }}
                </div>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item class="login-btn-item">
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              @click="handleLogin"
              :loading="loading"
            >登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'
import request2 from '../../api/request2'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const form = ref({
  username: '',
  password: '',
  code: ''
})

const rules = ref({
  username: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为4个字符', trigger: 'blur' }
  ]
})

const randomCode = ref('8888')

const generateCode = () => {
  const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'
  let code = ''
  for (let i = 0; i < 4; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  randomCode.value = code
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()

    if (form.value.code.toUpperCase() !== randomCode.value.toUpperCase()) {
      ElMessage.error('验证码错误，请重新输入')
      generateCode()
      return
    }

    loading.value = true

    const res = await request2.post('/user/login', {
      username: form.value.username,
      password: form.value.password
    })

    if (res.code === 200 && res.data) {
      const user = res.data
      const token = `student-${user.id}-${Date.now()}`
      localStorage.setItem('student_token', token)
      localStorage.setItem('userId', String(user.id))
      localStorage.setItem('username', user.name || user.username)
      localStorage.setItem('userInfo', JSON.stringify(user))

      ElMessage.success('登录成功！即将跳转到首页...')
      await router.push({ name: 'StudentHome' }).catch(() => {
        router.push('/student/home')
      })
    } else {
      ElMessage.error(res.msg || '登录失败，请检查账号密码')
    }
  } catch (error) {
    if (error?.name !== 'ValidationError') {
      ElMessage.error('登录失败，请检查账号密码')
      console.error('登录错误：', error)
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  generateCode()
})
</script>

<style scoped>
.login-page {
  display: flex;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 100%);
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0;
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

.left-section {
  flex: 1;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  z-index: 10;
}

.logo-container {
  text-align: center;
  margin-bottom: 40px;
}

.system-name {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  color: #303133;
}

.system-desc {
  font-size: 16px;
  opacity: 0.8;
  color: #606266;
}

.illustration {
  color: #409eff;
  opacity: 0.9;
}

.right-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.login-form {
  width: 100%;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
  label-width: 80px;
}

:deep(.el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.code-img {
  background-color: #f5f7fa;
  height: 48px;
  line-height: 48px;
  text-align: center;
  font-size: 18px;
  letter-spacing: 5px;
  color: #409eff;
  font-weight: bold;
  cursor: pointer;
  border-radius: 8px;
  user-select: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.login-btn-item {
  margin-bottom: 0;
}

.login-btn {
  width: 100%;
  border-radius: 8px;
  font-weight: 500;
  font-size: 16px;
  background: #409eff;
  border: none;
  padding: 12px 0;
}
</style>

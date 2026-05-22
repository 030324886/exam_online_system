<template>
  <div class="login-box">
    <el-card class="login-card">
      <h2 class="login-title">在线考试系统登录</h2>
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="80px" class="login-form">
        <el-form-item label="账号" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="loginForm.role" placeholder="请选择角色">
            <el-option label="教师" value="teacher" />
            <el-option label="教务" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-btn">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import request2 from '@/utils/request2'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()

const loginForm = ref({
  username: '',
  password: '',
  role: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
  } catch {
    ElMessage.warning('请完善登录信息')
    return
  }

  try {
    const res = await request2.post('/user/login', {
      username: loginForm.value.username,
      password: loginForm.value.password
    })

    if (res.code === 200 && res.data) {
      const user = res.data
      const roleMap = { 2: 'teacher', 3: 'admin' }
      const expectedRole = roleMap[user.role]

      if (!expectedRole) {
        ElMessage.error('该账号无教师/教务权限')
        return
      }

      if (expectedRole !== loginForm.value.role) {
        ElMessage.error(`此账号身份为${expectedRole === 'teacher' ? '教师' : '教务'}，请选择正确的角色`)
        return
      }

      const token = 'token-' + user.id + '-' + Date.now()
      userStore.login(token, user, loginForm.value.role)
      localStorage.setItem('userId', String(user.id))
      localStorage.setItem('username', user.name || user.username)
      ElMessage.success('登录成功')
      router.push(loginForm.value.role === 'teacher' ? '/teacher/home' : '/admin/home')
    } else {
      ElMessage.error(res.msg || '登录失败，请检查账号密码')
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查账号密码')
    console.error('登录错误：', e)
  }
}
</script>

<style scoped>
.login-box {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
}
.login-card { width: 420px; padding: 30px; border-radius: 12px; }
.login-title { text-align: center; margin-bottom: 30px; color: #303133; font-size: 24px; }
.login-form { margin-top: 10px; }
.login-btn { width: 100%; height: 40px; font-size: 16px; }
</style>

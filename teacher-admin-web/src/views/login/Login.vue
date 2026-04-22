<template>
  <div class="login-box">
    <el-card class="login-card">
      <h2 class="login-title">在线考试系统登录</h2>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="80px"
        class="login-form"
      >
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

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()

// 登录表单数据
const loginForm = ref({
  username: '',
  password: '',
  role: ''
})

// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 登录逻辑（当前为模拟，后续对接后端接口）
const handleLogin = async () => {
  await loginFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟后端返回的Token和用户信息
      const mockToken = 'mock-token-' + Date.now()
      const mockUserInfo = { username: loginForm.value.username }

      // 调用Pinia保存登录状态
      userStore.login(mockToken, mockUserInfo, loginForm.value.role)

      ElMessage.success('登录成功')
      // 按角色跳对应首页
      router.push(loginForm.value.role === 'teacher' ? '/teacher/home' : '/admin/home')
    } else {
      ElMessage.warning('请完善登录信息')
    }
  })
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
.login-card {
  width: 400px;
  padding: 20px;
}
.login-title {
  text-align: center;
  margin-bottom: 20px;
}
.login-form {
  margin-top: 20px;
}
.login-btn {
  width: 100%;
}
</style>
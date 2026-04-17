// src/api/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // 后端接口基础路径（根据后端A提供的地址修改，比如http://localhost:8080/api）
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器：添加token鉴权（和你登录页的token逻辑匹配）
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}` // 后端要求的token格式，可根据实际调整
    }
    return config
  },
  (error) => {
    console.error('请求拦截器错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理返回结果和异常
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 假设后端返回格式：{ code: 200, data: {}, msg: '' }
    if (res.code !== 200) {
      ElMessage.error(res.msg || '接口请求失败')
      return Promise.reject(res)
    }
    return res
  },
  (error) => {
    console.error('响应拦截器错误：', error)
    ElMessage.error(error.message || '服务器异常，请稍后重试')
    return Promise.reject(error)
  }
)

export default service
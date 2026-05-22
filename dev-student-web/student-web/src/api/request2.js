import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api2',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('student_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求拦截器错误：', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
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

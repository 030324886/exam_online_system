// src/utils/request.js
import axios from 'axios'
const request = axios.create({ baseURL: '/api', timeout: 10000 })
// 请求/响应拦截（简单版）
request.interceptors.response.use(res => res.data, err => Promise.reject(err))
export default request
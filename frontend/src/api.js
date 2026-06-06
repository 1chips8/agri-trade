import axios from 'axios'
import { ElMessage } from 'element-plus/es/components/message/index'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && body.code !== 0) {
      const message = body.message || '请求失败'
      const error = new Error(message)
      error.code = body.code
      error.responseBody = body
      ElMessage.error(message)
      return Promise.reject(error)
    }
    return body.data
  },
  (error) => {
    const status = error.response?.status
    const message = status === 401
      ? '请先登录'
      : status === 403
        ? '没有权限访问'
        : error.message || '网络异常'
    const normalized = new Error(message)
    normalized.status = status
    normalized.original = error
    ElMessage.error(message)
    return Promise.reject(normalized)
  }
)

export default api

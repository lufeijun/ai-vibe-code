import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

// 清除登录信息并跳转到登录页
const handleAuthError = () => {
  const authStore = useAuthStore()
  authStore.logout()
  ElMessage.warning('登录已过期，请重新登录')
  router.push('/login')
}

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 如果返回的 code 不是 200，说明有错误
    if (res.code !== 200) {
      // token 过期或无效：根据后端约定的 code 判断，常见的有 401、403、1001 等
      if (res.code === 401 || res.code === 403 || res.code === 1001) {
        handleAuthError()
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    // HTTP 状态码 401 或 403 表示认证失败
    if (error.response?.status === 401 || error.response?.status === 403) {
      handleAuthError()
    } else {
      const message = error.response?.data?.message || error.message || '网络错误'
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

export default request

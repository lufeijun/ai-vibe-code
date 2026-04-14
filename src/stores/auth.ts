import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

interface LoginData {
  token: string
  tokenType: string
  expiresIn: number
  user: {
    id: number
    username: string
    email: string
    phone: string
  }
  roles: string[]
  permissions: string[]
}

interface LoginResponse {
  code: number
  message: string
  data: LoginData
}

export interface PermissionTreeDTO {
  id: number
  parentId: number
  name: string
  code: string
  type: string
  level: number
  path: string
  icon: string
  sortOrder: number
  enabled: boolean
  children?: PermissionTreeDTO[]
}

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true')
  const userInfo = ref({
    username: localStorage.getItem('username') || '',
    remember: localStorage.getItem('remember') === 'true'
  })
  const token = ref(localStorage.getItem('token') || '')
  const permissionTree = ref<PermissionTreeDTO[]>([])
  const permissionTreeLoaded = ref(false)

  const login = async (username: string, password: string, remember: boolean) => {
    const res = await request.post<LoginResponse>('/user/login', {
      account: username,
      password: password
    })

    const response = res as unknown as LoginResponse
    if (response.code === 200) {
      const { token: newToken, user } = response.data

      isLoggedIn.value = true
      userInfo.value.username = user.username
      userInfo.value.remember = remember
      token.value = newToken

      // 保存到localStorage
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('username', user.username)
      localStorage.setItem('remember', remember.toString())
      localStorage.setItem('token', newToken)

      // 清除之前的权限树
      permissionTree.value = []
      permissionTreeLoaded.value = false
    }
  }

  const fetchPermissionTree = async () => {
    if (permissionTreeLoaded.value && permissionTree.value.length > 0) {
      return
    }

    try {
      console.log('开始获取权限树...')
      const res = await request.get('/user/permission')
      console.log('权限树响应:', res)
      // request 返回的是 { code, message, data } 结构
      const response = res as unknown as { code: number; data: PermissionTreeDTO[] }
      if (response.code === 200) {
        console.log('权限树数据:', response.data)
        permissionTree.value = response.data
        permissionTreeLoaded.value = true
      }
    } catch (error) {
      console.error('获取权限树失败:', error)
    }
  }

  const logout = () => {
    isLoggedIn.value = false
    userInfo.value.username = ''
    token.value = ''
    permissionTree.value = []
    permissionTreeLoaded.value = false

    // 清除localStorage
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    localStorage.removeItem('remember')
    localStorage.removeItem('token')
  }

  const isAuthenticated = computed(() => isLoggedIn.value)

  return {
    isLoggedIn,
    userInfo,
    token,
    permissionTree,
    permissionTreeLoaded,
    login,
    logout,
    fetchPermissionTree,
    isAuthenticated
  }
})
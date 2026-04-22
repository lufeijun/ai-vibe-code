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

export interface RoleDTO {
  id: number
  name: string
  code: string
  description: string
  enabled: boolean
  createdAt: string
  updatedAt: string
}

export interface UserInfoDTO {
  id: number
  username: string
  email: string
  phone: string
  city: string
  isEmployed: boolean
  hireDate: string | null
  resignationDate: string | null
  createdAt: string
  updatedAt: string
  roles: RoleDTO[]
}

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true')
  const userInfo = ref({
    id: parseInt(localStorage.getItem('userId') || '0'),
    username: localStorage.getItem('username') || '',
    email: localStorage.getItem('email') || '',
    phone: localStorage.getItem('phone') || '',
    remember: localStorage.getItem('remember') === 'true'
  })
  const fullUserInfo = ref<UserInfoDTO | null>(null)
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
      userInfo.value.id = user.id
      userInfo.value.username = user.username
      userInfo.value.email = user.email
      userInfo.value.phone = user.phone
      userInfo.value.remember = remember
      token.value = newToken

      // 保存到localStorage
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('userId', user.id.toString())
      localStorage.setItem('username', user.username)
      localStorage.setItem('email', user.email)
      localStorage.setItem('phone', user.phone)
      localStorage.setItem('remember', remember.toString())
      localStorage.setItem('token', newToken)

      // 清除之前的权限树
      permissionTree.value = []
      permissionTreeLoaded.value = false
      fullUserInfo.value = null

      // 登录成功后获取完整用户信息
      await fetchFullUserInfo()
    }
  }

  const fetchFullUserInfo = async () => {
    if (!userInfo.value.id) return
    try {
      const res = await request.get(`/user/${userInfo.value.id}`)
      const response = res as unknown as { code: number; data: UserInfoDTO }
      if (response.code === 200) {
        fullUserInfo.value = response.data
        // 更新基本信息
        userInfo.value.username = response.data.username
        userInfo.value.email = response.data.email
        userInfo.value.phone = response.data.phone
        localStorage.setItem('username', response.data.username)
        localStorage.setItem('email', response.data.email)
        localStorage.setItem('phone', response.data.phone)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  const updateUserInfo = async (data: {
    username?: string
    phone?: string
    city?: string
  }) => {
    if (!userInfo.value.id) return
    try {
      const res = await request.post('/user/update', {
        id: userInfo.value.id,
        ...data
      })
      const response = res as unknown as { code: number; data: any }
      if (response.code === 200) {
        // 更新本地信息
        if (data.username) {
          userInfo.value.username = data.username
          localStorage.setItem('username', data.username)
        }
        if (data.phone) {
          userInfo.value.phone = data.phone
          localStorage.setItem('phone', data.phone)
        }
        // 重新获取完整信息
        await fetchFullUserInfo()
        return true
      }
      return false
    } catch (error) {
      console.error('更新用户信息失败:', error)
      return false
    }
  }

  const changePassword = async (userId: number, newPassword: string) => {
    try {
      const res = await request.post('/user/change-password', {
        userId,
        newPassword
      })
      const response = res as unknown as { code: number; message: string }
      if (response.code === 200) {
        return { success: true, message: response.message }
      }
      return { success: false, message: response.message || '密码修改失败' }
    } catch (error: any) {
      console.error('修改密码失败:', error)
      return { success: false, message: error.message || '密码修改失败' }
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
    userInfo.value = {
      id: 0,
      username: '',
      email: '',
      phone: '',
      remember: false
    }
    fullUserInfo.value = null
    token.value = ''
    permissionTree.value = []
    permissionTreeLoaded.value = false

    // 清除localStorage
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('email')
    localStorage.removeItem('phone')
    localStorage.removeItem('remember')
    localStorage.removeItem('token')
  }

  const isAuthenticated = computed(() => isLoggedIn.value)

  return {
    isLoggedIn,
    userInfo,
    fullUserInfo,
    token,
    permissionTree,
    permissionTreeLoaded,
    login,
    logout,
    fetchPermissionTree,
    fetchFullUserInfo,
    updateUserInfo,
    changePassword,
    isAuthenticated
  }
})
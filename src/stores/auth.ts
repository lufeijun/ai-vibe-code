import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

interface LoginResponse {
  code: number
  message: string
  data: {
    token: string
    tokenType: string
    expiresIn: number
    user: {
      id: number
      username: string
      email: string
      phone: string
    }
  }
}

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true')
  const userInfo = ref({
    username: localStorage.getItem('username') || '',
    remember: localStorage.getItem('remember') === 'true'
  })
  const token = ref(localStorage.getItem('token') || '')

  const login = async (username: string, password: string, remember: boolean) => {
    const res = await request.post<LoginResponse>('/user/login', {
      account: username,
      password: password
    })

    if (res.code === 200) {
      const { token: newToken, user } = res.data

      isLoggedIn.value = true
      userInfo.value.username = user.username
      userInfo.value.remember = remember
      token.value = newToken

      // 保存到localStorage
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('username', user.username)
      localStorage.setItem('remember', remember.toString())
      localStorage.setItem('token', newToken)
    }
  }

  const logout = () => {
    isLoggedIn.value = false
    userInfo.value.username = ''
    token.value = ''

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
    login,
    logout,
    isAuthenticated
  }
})
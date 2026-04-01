import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true')
  const userInfo = ref({
    username: localStorage.getItem('username') || '',
    remember: localStorage.getItem('remember') === 'true'
  })

  const login = (username: string, remember: boolean) => {
    return new Promise<void>((resolve) => {
      setTimeout(() => {
        isLoggedIn.value = true
        userInfo.value.username = username
        userInfo.value.remember = remember

        // 保存到localStorage
        localStorage.setItem('isLoggedIn', 'true')
        localStorage.setItem('username', username)
        localStorage.setItem('remember', remember.toString())

        resolve()
      }, 1000)
    })
  }

  const logout = () => {
    isLoggedIn.value = false
    userInfo.value.username = ''

    // 清除localStorage
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    localStorage.removeItem('remember')
  }

  const isAuthenticated = computed(() => isLoggedIn.value)

  return {
    isLoggedIn,
    userInfo,
    login,
    logout,
    isAuthenticated
  }
})
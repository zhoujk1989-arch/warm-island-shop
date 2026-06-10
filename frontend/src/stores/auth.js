import { defineStore } from 'pinia'
import { login as apiLogin, register as apiRegister } from '../api/auth.js'
import router from '../router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    role: localStorage.getItem('role') || '',
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN',
  },
  actions: {
    async login(username, password) {
      const result = await apiLogin(username, password)
      this.token = result.token
      this.username = result.username
      this.role = result.role
      localStorage.setItem('token', result.token)
      localStorage.setItem('username', result.username)
      localStorage.setItem('role', result.role)
      return result
    },
    async register(username, password) {
      const result = await apiRegister(username, password)
      this.token = result.token
      this.username = result.username
      this.role = result.role
      localStorage.setItem('token', result.token)
      localStorage.setItem('username', result.username)
      localStorage.setItem('role', result.role)
      return result
    },
    logout() {
      this.token = ''
      this.username = ''
      this.role = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      router.push('/')
    },
  },
})

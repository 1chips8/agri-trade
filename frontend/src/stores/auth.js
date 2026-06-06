import { defineStore } from 'pinia'
import api from '../api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),
  getters: {
    isLogin: (state) => Boolean(state.token),
    role: (state) => state.user?.role || 'GUEST',
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isMerchant: (state) => state.user?.role === 'MERCHANT'
  },
  actions: {
    async login(payload) {
      const data = await api.post('/auth/login', payload)
      this.token = data.token
      this.user = data.user
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
    },
    async register(payload) {
      await api.post('/auth/register', payload)
    },
    async fetchMe() {
      if (!this.token) return null
      try {
        this.user = await api.get('/auth/me')
        localStorage.setItem('user', JSON.stringify(this.user))
        return this.user
      } catch (error) {
        this.token = ''
        this.user = null
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        return null
      }
    },
    async logout() {
      if (this.token) await api.post('/auth/logout').catch(() => {})
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})

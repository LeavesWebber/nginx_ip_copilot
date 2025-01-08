import { login, logout } from '../../api'
import { ElMessage } from 'element-plus'

interface AuthState {
  token: string | null
  user: any | null
}

export default {
  namespaced: true,
  
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    user: null
  }),

  mutations: {
    SET_TOKEN(state: AuthState, token: string) {
      state.token = token
      localStorage.setItem('token', token)
    },
    CLEAR_TOKEN(state: AuthState) {
      state.token = null
      localStorage.removeItem('token')
    }
  },

  actions: {
    async login({ commit }, credentials) {
      try {
        const { data } = await login(credentials)
        if (data.code === 200 && data.data?.token) {
          commit('SET_TOKEN', data.data.token)
          return data.data
        } else {
          ElMessage.error(data.message || '登录失败')
          return Promise.reject(data.message)
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || '登录失败')
        return Promise.reject(error)
      }
    },

    async logout({ commit }) {
      try {
        await logout()
        commit('CLEAR_TOKEN')
      } catch (error) {
        // 即使登出失败，也清除本地token
        commit('CLEAR_TOKEN')
        console.error('Logout failed:', error)
      }
    }
  }
}
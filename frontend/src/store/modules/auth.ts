import { login, logout, getUserInfo } from '../../api'
import { updateSettings } from '../../api'
import { ElMessage } from 'element-plus'
import { ActionContext } from 'vuex'

interface AuthState {
  token: string | null
  user: any | null
}

interface LoginCredentials {
  userId: string
  userPassword: string
}

export default {
  namespaced: true,
  
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    user: null
  }),

  mutations: {
    SET_TOKEN(state: AuthState, token: string) {
      console.log('设置 token:', token)
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER(state: AuthState, user: any) {
      console.log('设置用户信息:', user)
      state.user = user
    },
    CLEAR_USER(state: AuthState) {
      console.log('清除用户信息')
      state.user = null
    },
    CLEAR_TOKEN(state: AuthState) {
      console.log('清除 token 和用户信息')
      state.token = null
      localStorage.removeItem('token')
    }
  },

  actions: {
    async login({ commit, dispatch }: ActionContext<AuthState, any>, credentials: LoginCredentials) {
      try {
        const { data } = await login(credentials)
        if (data.code === 200 && data.data?.token) {
          commit('SET_TOKEN', data.data.token)
          await dispatch('fetchUserInfo')
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

    async fetchUserInfo({ commit }: ActionContext<AuthState, any>) {
      try {
        console.log('开始调用获取用户信息 API')
        const { data } = await getUserInfo()
        console.log('获取用户信息 API 响应:', data)
        if (data) {
          commit('SET_USER', data)
          return data
        }
      } catch (error) {
        console.error('获取用户信息 API 错误:', error)
        console.error('Failed to fetch user info:', error)
      }
    },

    async logout({ commit, dispatch, state }: ActionContext<AuthState, any>) {
      console.log('开始登出 - 当前状态:', {
        token: localStorage.getItem('token'),
        nginxConfigPath: localStorage.getItem('nginxConfigPath'),
        store: { auth: state, token: state.token, user: state.user }
      })

      // 1. 先重置所有相关的 store 状态
      try {
        // 重置设置
        await dispatch('settings/resetSettings', null, { root: true })
        // 清空 IP 规则
        await dispatch('ipRules/SET_RULES', [], { root: true })
        // 清空地理位置规则
        await dispatch('geoRules/SET_RULES', [], { root: true })
        // 清空 Nginx 状态
        await dispatch('nginx/SET_STATUS', null, { root: true })
        await dispatch('nginx/UPDATE_CONFIG', '', { root: true })
      } catch (error) {
        console.error('Failed to reset store states:', error)
      }

      // 2. 调用后端登出 API
      try {
        await logout()
      } catch (error) {
        console.error('Logout API failed:', error)
      }

      // 3. 清除认证状态
      commit('CLEAR_USER')
      commit('CLEAR_TOKEN')

      console.log('登出完成 - 最终状态:', {
        token: localStorage.getItem('token'),
        nginxConfigPath: localStorage.getItem('nginxConfigPath'),
        store: { auth: state, token: state.token, user: state.user }
      })
    }
  }
}
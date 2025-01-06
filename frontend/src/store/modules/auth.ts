import { login, logout } from '../../api'

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
      const { data } = await login(credentials)
      commit('SET_TOKEN', data.token)
      return data
    },
    async logout({ commit }) {
      await logout()
      commit('CLEAR_TOKEN')
    }
  }
}
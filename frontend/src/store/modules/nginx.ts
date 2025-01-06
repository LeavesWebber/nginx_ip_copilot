import { getNginxStatus, reloadNginx } from '../../api'

interface NginxState {
  status: any | null
}

export default {
  namespaced: true,
  
  state: (): NginxState => ({
    status: null
  }),

  mutations: {
    SET_STATUS(state: NginxState, status: any) {
      state.status = status
    }
  },

  actions: {
    async getStatus({ commit }) {
      const { data } = await getNginxStatus()
      commit('SET_STATUS', data)
      return data
    },
    async reload() {
      await reloadNginx()
    }
  }
}
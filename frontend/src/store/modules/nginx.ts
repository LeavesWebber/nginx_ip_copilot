import { getNginxStatus, reloadNginx, updateNginxConfig } from '../../api'
import { Commit } from 'vuex'

interface NginxState {
  status: any | null
  config: string
}

export default {
  namespaced: true,
  
  state: (): NginxState => ({
    status: null,
    config: ''
  }),

  mutations: {
    SET_STATUS(state: NginxState, status: any) {
      state.status = status
    },
    UPDATE_CONFIG(state: NginxState, config: string) {
      state.config = config
    }
  },

  actions: {
    async getStatus({ commit }: { commit: Commit }) {
      const { data } = await getNginxStatus()
      commit('SET_STATUS', data)
      commit('UPDATE_CONFIG', data.config)
      return data
    },
    async reload() {
      await reloadNginx()
    },
    async updateStatus({ commit }: { commit: Commit }) {
      const response = await getNginxStatus()
      commit('SET_STATUS', response.data)
      commit('UPDATE_CONFIG', response.data.config)
    },
    async updateConfig({ commit }: { commit: Commit }, config: string) {
      const { data } = await updateNginxConfig(config)
      commit('UPDATE_CONFIG', data.config)
      return data
    }
  }
}
import { getSettings, updateSettings, validateNginxConfigPath } from '../../api'
import { Commit } from 'vuex'

interface SettingsState {
  nginxConfigPath: string
  enableAbuseIPDB: boolean
  abuseIPDBApiKey: string
}

export default {
  namespaced: true,
  
  state: (): SettingsState => ({
    nginxConfigPath: '',
    enableAbuseIPDB: false,
    abuseIPDBApiKey: ''
  }),

  mutations: {
    SET_SETTINGS(state: SettingsState, settings: Partial<SettingsState>) {
      Object.assign(state, settings)
    }
  },

  actions: {
    async fetchSettings({ commit }: { commit: Commit }) {
      const { data } = await getSettings()
      commit('SET_SETTINGS', data)
      return data
    },
    async validatePath(_: { commit: Commit }, path: string) {
      const { data } = await validateNginxConfigPath(path)
      return data
    },
    async saveSettings({ commit }: { commit: Commit }, settings: SettingsState) {
      // 更新设置
      const { data } = await updateSettings(settings)
      commit('SET_SETTINGS', data)
      return data
    }
  }
}
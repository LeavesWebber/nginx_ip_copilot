import { getSettings, updateSettings } from '../../api'

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
    async fetchSettings({ commit }) {
      const { data } = await getSettings()
      commit('SET_SETTINGS', data)
      return data
    },
    async saveSettings({ commit }, settings) {
      await updateSettings(settings)
      commit('SET_SETTINGS', settings)
    }
  }
}
import { getSettings, updateSettings, validateNginxConfigPath } from '../../api'
import { Commit, ActionContext } from 'vuex'

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
      if (settings.nginxConfigPath !== undefined) {
        localStorage.setItem('nginxConfigPath', settings.nginxConfigPath)
      }
      if (settings.enableAbuseIPDB !== undefined) {
        localStorage.setItem('enableAbuseIPDB', String(settings.enableAbuseIPDB))
      }
      if (settings.abuseIPDBApiKey !== undefined) {
        localStorage.setItem('abuseIPDBApiKey', settings.abuseIPDBApiKey)
      }
      Object.assign(state, settings)
    },
    SET_NGINX_CONFIG_PATH(state: SettingsState, path: string) {
      state.nginxConfigPath = path
      localStorage.setItem('nginxConfigPath', path)
    },
    RESET_SETTINGS(state: SettingsState) {
      // 清除 localStorage
      localStorage.removeItem('nginxConfigPath')
      localStorage.removeItem('enableAbuseIPDB')
      localStorage.removeItem('abuseIPDBApiKey')
      
      // 重置 Vuex store 状态
      state.nginxConfigPath = ''
      state.enableAbuseIPDB = false
      state.abuseIPDBApiKey = ''

      console.log('Settings reset - state:', {
        state,
        localStorage: {
          nginxConfigPath: localStorage.getItem('nginxConfigPath'),
          enableAbuseIPDB: localStorage.getItem('enableAbuseIPDB'),
          abuseIPDBApiKey: localStorage.getItem('abuseIPDBApiKey')
        }
      })
    }
  },

  actions: {
    async fetchSettings({ commit }: { commit: Commit }) {
      // 只有当本地有配置时才从后端获取
      const localPath = localStorage.getItem('nginxConfigPath')
      if (!localPath) {
        return {
          nginxConfigPath: '',
          enableAbuseIPDB: false,
          abuseIPDBApiKey: ''
        }
      }

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
    },
    async updateNginxConfigPath({ commit }: ActionContext<SettingsState, any>, path: string) {
      try {
        const response = await validateNginxConfigPath(path)
        if (response.data.valid) {
          commit('SET_NGINX_CONFIG_PATH', path)
          return true
        }
        return false
      } catch (error) {
        console.error('Failed to validate nginx config path:', error)
        return false
      }
    },
    async resetSettings({ commit }: ActionContext<SettingsState, any>) {
      // 重置前端状态
      commit('RESET_SETTINGS')
      
      // 打印重置后的状态
      console.log('Settings reset - localStorage:', {
        token: localStorage.getItem('token'),
        nginxConfigPath: localStorage.getItem('nginxConfigPath'),
        enableAbuseIPDB: localStorage.getItem('enableAbuseIPDB'),
        abuseIPDBApiKey: localStorage.getItem('abuseIPDBApiKey')
      })
    }
  }
}
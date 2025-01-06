import { createStore } from 'vuex'
import { ipRulesApi, geoRulesApi, nginxApi } from '../api'

export default createStore({
  state: {
    user: null,
    ipRules: [],
    geoRules: [],
    nginxConfig: null
  },
  
  mutations: {
    SET_USER(state, user) {
      state.user = user
    },
    SET_IP_RULES(state, rules) {
      state.ipRules = rules
    },
    SET_GEO_RULES(state, rules) {
      state.geoRules = rules
    },
    SET_NGINX_CONFIG(state, config) {
      state.nginxConfig = config
    }
  },
  
  actions: {
    async fetchIPRules({ commit }) {
      const rules = await ipRulesApi.getList()
      commit('SET_IP_RULES', rules.data)
    },
    async fetchGeoRules({ commit }) {
      const rules = await geoRulesApi.getList()
      commit('SET_GEO_RULES', rules.data)
    },
    async updateNginxConfig({ commit }, path) {
      await nginxApi.setConfigPath(path)
      const config = await nginxApi.getConfig()
      commit('SET_NGINX_CONFIG', config.data)
    }
  }
}) 
import { getGeoRules, addGeoRule, deleteGeoRule } from '../../api'

interface GeoRulesState {
  rules: any[]
}

export default {
  namespaced: true,
  
  state: (): GeoRulesState => ({
    rules: []
  }),

  mutations: {
    SET_RULES(state: GeoRulesState, rules: any[]) {
      state.rules = rules
    }
  },

  actions: {
    async fetchRules({ commit }) {
      const { data } = await getGeoRules()
      commit('SET_RULES', data)
      return data
    },
    async addRule({ dispatch }, rule) {
      await addGeoRule(rule)
      return dispatch('fetchRules')
    },
    async deleteRule({ dispatch }, ruleId) {
      await deleteGeoRule(ruleId)
      return dispatch('fetchRules')
    }
  }
}
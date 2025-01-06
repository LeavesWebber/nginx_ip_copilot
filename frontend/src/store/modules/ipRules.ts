import { getIPRules, addIPRule, deleteIPRule } from '../../api'

interface IPRulesState {
  rules: any[]
}

export default {
  namespaced: true,
  
  state: (): IPRulesState => ({
    rules: []
  }),

  mutations: {
    SET_RULES(state: IPRulesState, rules: any[]) {
      state.rules = rules
    }
  },

  actions: {
    async fetchRules({ commit }) {
      const { data } = await getIPRules()
      commit('SET_RULES', data)
      return data
    },
    async addRule({ dispatch }, rule) {
      await addIPRule(rule)
      return dispatch('fetchRules')
    },
    async deleteRule({ dispatch }, ruleId) {
      await deleteIPRule(ruleId)
      return dispatch('fetchRules')
    }
  }
}
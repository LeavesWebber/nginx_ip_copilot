import { createStore } from 'vuex'
import auth from './modules/auth'
import ipRules from './modules/ipRules'
import geoRules from './modules/geoRules'
import settings from './modules/settings'
import nginx from './modules/nginx'

export default createStore({
  modules: {
    auth,
    ipRules,
    geoRules,
    settings,
    nginx
  }
})
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Dashboard from '../components/Dashboard.vue'
import Login from '../components/Login.vue'
import IPRules from '../components/IPRules.vue'
import GeoRules from '../components/GeoRules.vue'
import Settings from '../components/Settings.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    children: [
      {
        path: 'ip-rules',
        name: 'IPRules',
        component: IPRules
      },
      {
        path: 'geo-rules',
        name: 'GeoRules',
        component: GeoRules
      },
      {
        path: 'settings',
        name: 'Settings',
        component: Settings
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 
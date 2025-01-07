import axios from 'axios'

export const api = axios.create({
  baseURL: '/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Auth APIs
export const login = (data: { userId: string; userPassword: string }) => 
  api.post('/auth/login', data)

export const logout = () => 
  api.post('/auth/logout')

export const getUserInfo = () => 
  api.get('/auth/info')

// IP Rules APIs
export const getIPRules = () => 
  api.get('/ip-rules')

export const addIPRule = (rule: any) => 
  api.post('/ip-rules', rule)

export const deleteIPRule = (ruleId: string) => 
  api.delete(`/ip-rules/${ruleId}`)

// Geo Rules APIs
export const getGeoRules = () => 
  api.get('/geo-rules')

export const addGeoRule = (rule: any) => 
  api.post('/geo-rules', rule)

export const deleteGeoRule = (ruleId: string) => 
  api.delete(`/geo-rules/${ruleId}`)

// Settings APIs
export const getSettings = () => 
  api.get('/system/settings')

export const updateSettings = (settings: any) => 
  api.post('/system/settings', settings)

// Nginx APIs
export const getNginxStatus = () => 
  api.get('/nginx/status')

export const reloadNginx = () => 
  api.post('/nginx/reload')

api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
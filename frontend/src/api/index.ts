import axios from 'axios'

export const api = axios.create({
  baseURL: '/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true
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
  api.get('/nginx/settings')

export const updateSettings = (settings: any) => 
  api.post('/nginx/settings', settings)

// Nginx APIs
export const getNginxStatus = () => 
  api.get('/nginx/status')

export const reloadNginx = () => 
  api.post('/nginx/reload')

export const updateNginxConfig = (config: string) => 
  api.post('/nginx/config', { content: config })

export const validateNginxConfigPath = (path: string) =>
  api.post('/nginx/config/path/validate', { path })

// 请求拦截器：添加token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：处理401错误
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
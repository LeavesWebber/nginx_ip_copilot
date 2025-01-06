import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Auth APIs
export const login = (data: { username: string; password: string }) => 
  api.post('/auth/login', data)

export const logout = () => 
  api.post('/auth/logout')

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
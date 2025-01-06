import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// IP规则相关接口
export const ipRulesApi = {
  getList: () => api.get('/ip-rules'),
  add: (data: any) => api.post('/ip-rules', data),
  delete: (id: string) => api.delete(`/ip-rules/${id}`),
  update: (id: string, data: any) => api.put(`/ip-rules/${id}`, data)
}

// 地理位置规则相关接口
export const geoRulesApi = {
  getList: () => api.get('/geo-rules'),
  add: (data: any) => api.post('/geo-rules', data),
  delete: (id: string) => api.delete(`/geo-rules/${id}`)
}

// Nginx配置相关接口
export const nginxApi = {
  getConfig: () => api.get('/nginx/config'),
  setConfigPath: (path: string) => api.post('/nginx/config/path', { path }),
  reload: () => api.post('/nginx/reload')
} 
import MockAdapter from 'axios-mock-adapter'
import { api } from './index'
import { generateNginxConfig } from './mock/nginx-config'

const mock = new MockAdapter(api, { delayResponse: 1000 })

// Mock Data
const ipRules = [
  {
    id: '1',
    ip: '192.168.1.100',
    type: 'single_ip',
    comment: 'Blocked for suspicious activity',
    created_at: '2024-03-20T10:00:00Z',
    status: 'active'
  },
  {
    id: '2',
    ip_range: '10.0.0.0/24',
    type: 'ip_range',
    comment: 'Blocked subnet',
    created_at: '2024-03-20T11:00:00Z',
    status: 'active'
  }
]

const geoRules = [
  {
    id: '1',
    country_code: 'XX',
    comment: 'Blocked country',
    created_at: '2024-03-20T10:00:00Z',
    status: 'active'
  }
]

const settings = {
  nginxConfigPath: '/etc/nginx/conf.d/myweb.conf',
  enableAbuseIPDB: false,
  abuseIPDBApiKey: ''
}

// Auth APIs
mock.onPost('/auth/login').reply((config) => {
  const { username, password } = JSON.parse(config.data)
  if (username === 'admin' && password === 'admin') {
    return [200, { token: 'mock-token-123' }]
  }
  return [401, { message: 'Invalid credentials' }]
})

mock.onPost('/auth/logout').reply(200)

// IP Rules APIs
mock.onGet('/ip-rules').reply(200, ipRules)

mock.onPost('/ip-rules').reply((config) => {
  const newRule = JSON.parse(config.data)
  ipRules.push({
    id: String(ipRules.length + 1),
    ...newRule,
    created_at: new Date().toISOString(),
    status: 'active'
  })
  const newStatus = getLatestNginxStatus()
  return [200, { rule: newRule, nginxStatus: newStatus }]
})

mock.onDelete(/\/ip-rules\/\d+/).reply((config) => {
  const id = config.url?.split('/').pop()
  const index = ipRules.findIndex(rule => rule.id === id)
  if (index > -1) {
    ipRules.splice(index, 1)
    return [200]
  }
  return [404]
})

// Geo Rules APIs
mock.onGet('/geo-rules').reply(200, geoRules)

mock.onPost('/geo-rules').reply((config) => {
  const newRule = JSON.parse(config.data)
  geoRules.push({
    id: String(geoRules.length + 1),
    ...newRule,
    created_at: new Date().toISOString(),
    status: 'active'
  })
  const newStatus = getLatestNginxStatus()
  return [200, { rule: newRule, nginxStatus: newStatus }]
})

mock.onDelete(/\/geo-rules\/\d+/).reply((config) => {
  const id = config.url?.split('/').pop()
  const index = geoRules.findIndex(rule => rule.id === id)
  if (index > -1) {
    geoRules.splice(index, 1)
    return [200]
  }
  return [404]
})

// Settings APIs
mock.onGet('/system/settings').reply(200, settings)

mock.onPost('/system/settings').reply((config) => {
  Object.assign(settings, JSON.parse(config.data))
  return [200, settings]
})

// Nginx APIs
mock.onGet('/nginx/status').reply(() => {
  return [200, getLatestNginxStatus()]
})

mock.onPost('/nginx/reload').reply(() => {
  return [200, {
    success: true,
    message: 'Nginx configuration reloaded successfully',
    ...getLatestNginxStatus()
  }]
})

// 添加一个用于获取最新配置的函数
const getLatestNginxStatus = () => ({
  status: 'running',
  worker_processes: 4,
  connections: 100,
  config: generateNginxConfig(ipRules, geoRules)
})

export default mock
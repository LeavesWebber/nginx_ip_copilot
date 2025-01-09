<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRoute } from 'vue-router'
import { CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'

interface NginxStatus {
  status: string
  worker_processes: number
  connections: number
  config: string
}

const store = useStore()
const nginxStatus = ref<NginxStatus | null>(null)
const nginxConfig = ref('')
const isEditing = ref(false)
const editedConfig = ref('')
const isLoading = ref(false)
const geoModuleStatus = ref({
  isInstalled: false,
  isEnabled: false,
  message: ''
})
const isInstallingGeoModule = ref(false)

const fetchDashboardData = async () => {
  isLoading.value = true
  try {
    const status = await store.dispatch('nginx/getStatus')
    nginxStatus.value = status
    nginxConfig.value = status.config
  } catch (error: any) {
    console.error('Failed to fetch dashboard data:', error)
    let errorMessage = '获取仪表盘数据失败'
    if (error.response) {
      // 服务器响应错误
      if (error.response.status === 403) {
        errorMessage = '无权限访问，请先登录'
      } else if (error.response.data?.message) {
        errorMessage = error.response.data.message
      }
    } else if (error.request) {
      // 请求发送失败
      errorMessage = '无法连接到服务器'
    } else {
      // 其他错误
      errorMessage = error.message || '未知错误'
    }
    ElMessage.error(errorMessage)
  } finally {
    isLoading.value = false
  }
}

const startEditing = () => {
  editedConfig.value = nginxConfig.value
  isEditing.value = true
}

const cancelEditing = () => {
  isEditing.value = false
  editedConfig.value = ''
}

const saveConfig = async () => {
  if (!editedConfig.value.trim()) {
    ElMessage.warning('配置内容不能为空')
    return
  }

  isLoading.value = true
  try {
    const response = await store.dispatch('nginx/updateConfig', editedConfig.value)
    // 配置更新成功
    if (response.reloadSuccess) {
      ElMessage.success('配置文件更新成功并已重载Nginx')
    } else {
      // 配置更新成功但重载失败
      ElMessage({
        message: response.message,
        type: 'warning'
      })
    }
    isEditing.value = false
    await fetchDashboardData()
  } catch (error: any) {
    console.error('Failed to update config:', error)
    // 配置更新失败
    ElMessage.error(error.response?.data?.message || error.message || '更新配置文件失败')
  } finally {
    isLoading.value = false
  }
}

const checkGeoModuleStatus = async () => {
  try {
    const response = await axios.get('/api/nginx/geo-module/status')
    geoModuleStatus.value = response.data
  } catch (error) {
    ElMessage.error('检查GeoIP2模块状态失败')
  }
}

const handleInstallGeoModule = async () => {
  try {
    isInstallingGeoModule.value = true
    const response = await axios.post('/api/nginx/geo-module/install')
    if (response.data.success) {
      ElMessage.success('GeoIP2模块安装成功')
      await checkGeoModuleStatus()
    } else {
      ElMessage.error(response.data.message || 'GeoIP2模块安装失败')
    }
  } catch (error) {
    ElMessage.error('安装GeoIP2模块失败')
  } finally {
    isInstallingGeoModule.value = false
  }
}

const reloadNginx = async () => {
  try {
    await store.dispatch('nginx/reload')
    ElMessage.success('Nginx 配置已重载')
    await fetchDashboardData()
  } catch (error: any) {
    console.error('Failed to reload nginx:', error)
    ElMessage.error(error.response?.data?.message || '重载 Nginx 失败')
  }
}

const route = useRoute()
watch(() => route.path, async (newPath) => {
  if (newPath === '/dashboard') {
    await Promise.all([
      fetchDashboardData(),
      checkGeoModuleStatus()
    ])
  }
})

onMounted(async () => {
  await Promise.all([
    fetchDashboardData(),
    checkGeoModuleStatus()
  ])
})
</script>

<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card v-loading="isLoading">
          <template #header>
            <div class="card-header">
              <span>Nginx 配置</span>
              <div class="header-buttons">
                <el-button 
                  v-if="!isEditing" 
                  type="primary" 
                  size="small" 
                  @click="startEditing"
                  :disabled="isLoading"
                >
                  编辑配置
                </el-button>
                <template v-else>
                  <el-button 
                    type="success" 
                    size="small" 
                    @click="saveConfig"
                    :loading="isLoading"
                  >
                    保存
                  </el-button>
                  <el-button 
                    type="default" 
                    size="small" 
                    @click="cancelEditing"
                    :disabled="isLoading"
                  >
                    取消
                  </el-button>
                </template>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="fetchDashboardData"
                  :loading="isLoading"
                >
                  刷新
                </el-button>
              </div>
            </div>
          </template>
          <div v-if="!isEditing" class="nginx-config">
            <pre v-if="nginxConfig">{{ nginxConfig }}</pre>
            <div v-else class="no-config">
              <p>未找到配置文件内容</p>
              <p class="tip">请先在设置页面设置正确的Nginx配置文件路径</p>
            </div>
          </div>
          <div v-else class="nginx-config-editor">
            <el-input
              v-model="editedConfig"
              type="textarea"
              :rows="20"
              resize="none"
              :disabled="isLoading"
            />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="status-card" v-loading="isLoading">
          <template #header>
            <div class="card-header">
              <span>系统状态</span>
            </div>
          </template>
          <div class="status-content">
            <div v-if="nginxStatus">
              <div class="status-item">
                <span>运行状态:</span>
                <el-tag :type="nginxStatus.status === 'running' ? 'success' : 'danger'">
                  {{ nginxStatus.status === 'running' ? '运行中' : '已停止' }}
                </el-tag>
              </div>
              <div class="status-item">
                <span>工作进程:</span>
                <span>{{ nginxStatus.worker_processes }}</span>
              </div>
              <div class="status-item">
                <span>活动连接:</span>
                <span>{{ nginxStatus.connections }}</span>
              </div>
            </div>
            <div v-else class="no-status">
              <p>未能获取系统状态</p>
            </div>
          </div>
        </el-card>
        <el-card class="status-card">
          <template #header>
            <div class="card-header">
              <span>GeoIP2 模块状态</span>
            </div>
          </template>
          <div class="status-content">
            <div class="status-item">
              <span>安装状态:</span>
              <el-tag :type="geoModuleStatus.isInstalled ? 'success' : 'warning'">
                {{ geoModuleStatus.isInstalled ? '已安装' : '未安装' }}
              </el-tag>
            </div>
            <div class="status-item">
              <span>启用状态:</span>
              <el-tag :type="geoModuleStatus.isEnabled ? 'success' : 'warning'">
                {{ geoModuleStatus.isEnabled ? '已启用' : '未启用' }}
              </el-tag>
            </div>
            <div class="status-message" v-if="geoModuleStatus.message">
              {{ geoModuleStatus.message }}
            </div>
            <div class="button-container" v-if="!geoModuleStatus.isInstalled">
              <el-button 
                type="primary" 
                @click="handleInstallGeoModule"
                :loading="isInstallingGeoModule">
                安装 GeoIP2 模块
              </el-button>
            </div>
          </div>
        </el-card>
        <el-card class="nginx-status-card" v-loading="isLoading">
          <template #header>
            <div class="card-header">
              <span>Nginx 运行状态</span>
              <el-button 
                type="primary" 
                size="small" 
                @click="fetchDashboardData"
                :loading="isLoading"
              >
                刷新
              </el-button>
            </div>
          </template>
          <div class="status-content">
            <div v-if="nginxStatus" class="nginx-status">
              <div class="status-item">
                <el-tag 
                  class="status-tag"
                  size="large"
                  :type="nginxStatus.status === 'running' ? 'success' : 'danger'"
                >
                  <el-icon class="status-icon">
                    <component :is="nginxStatus.status === 'running' ? 'CircleCheckFilled' : 'CircleCloseFilled'" />
                  </el-icon>
                  {{ nginxStatus.status === 'running' ? 'Nginx 正在运行' : 'Nginx 已停止' }}
                </el-tag>
              </div>
              <div class="status-details">
                <div class="detail-item">
                  <span class="label">工作进程数：</span>
                  <span class="value">{{ nginxStatus.worker_processes }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">当前连接数：</span>
                  <span class="value">{{ nginxStatus.connections }}</span>
                </div>
              </div>
              <div class="status-actions">
                <el-button 
                  type="primary" 
                  :loading="isLoading"
                  @click="reloadNginx"
                >
                  重载配置
                </el-button>
              </div>
            </div>
            <div v-else class="no-status">
              <el-empty description="无法获取 Nginx 状态" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
/* 仪表盘容器 */
.dashboard {
  padding: 24px;
  animation: fadeIn 0.3s ease-in-out;
}

/* 卡片基础样式 */
.el-card {
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1),
              0 4px 12px rgba(0, 0, 0, 0.05) !important;  /* 双层阴影效果 */
  transition: all 0.3s ease;
  overflow: hidden;
  backdrop-filter: blur(10px);
  transform: translateZ(0);  /* 启用硬件加速 */
  position: relative;
  background: #ffffff;
}

/* 卡片悬浮效果 */
.el-card:hover {
  transform: translateY(-2px) translateZ(0);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15),
              0 8px 20px rgba(0, 0, 0, 0.08) !important;  /* 悬浮时加深阴影 */
}

/* 添加卡片边缘微光效果 */
.el-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 16px;
  pointer-events: none;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.1);
}

/* 左侧大卡片特殊样式（Nginx Configuration） */
.el-col:first-child .el-card {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1),
              0 4px 12px rgba(0, 0, 0, 0.05) !important;
}

.el-col:first-child .el-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15),
              0 8px 20px rgba(0, 0, 0, 0.08) !important;
}

/* 右侧小卡片特殊样式（System Status） */
.el-col:last-child .el-card {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1),
              0 4px 12px rgba(0, 0, 0, 0.05) !important;
}

.el-col:last-child .el-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15),
              0 8px 20px rgba(0, 0, 0, 0.08) !important;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  height: 80px;
  border-bottom: 1px solid #e5e7eb;
  width: 100%;
}

/* Nginx Configuration 标题样式 */
.card-header span {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(120deg, #2563eb, #3b82f6, #60a5fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 0.5px;
  position: relative;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

/* System Status 标题样式 */
:deep(.el-card__header) {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  padding: 20px 24px;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #e5e7eb;
  height: 80px;
  display: flex;
  align-items: center;
}

/* 按钮组样式 */
.header-buttons {
  display: flex;
  gap: 8px;
}

/* 配置显示区域 */
.nginx-config {
  background: #f8fafc;
  padding: 24px;
  border-radius: 12px;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #334155;
  border: 1px solid #e2e8f0;
  margin: 16px;
  min-height: 200px;
}

/* 编辑器样式 */
.nginx-config-editor {
  margin: 16px;
}

:deep(.el-textarea__inner) {
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 14px;
  line-height: 1.6;
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

/* 无配置状态提示 */
.no-config, .no-status {
  text-align: center;
  padding: 40px 20px;
  color: #64748b;
}

.no-config p, .no-status p {
  margin: 8px 0;
}

.tip {
  font-size: 14px;
  color: #94a3b8;
}

/* 系统状态信息 */
.el-card p {
  padding: 16px 24px;
  margin: 0;
  border-bottom: 1px solid #f1f5f9;
  font-size: 15px;
  color: #475569;
  display: flex;
  align-items: center;
  transition: background-color 0.2s ease;
}

.el-card p:hover {
  background-color: #f8fafc;
}

.el-card p:last-child {
  border-bottom: none;
}

.el-card p strong {
  color: #1e293b;
  font-weight: 600;
  margin-right: 12px;
  min-width: 160px;
}

/* 添加淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dashboard {
  padding: 20px;
}

.status-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-content {
  padding: 16px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.status-item:last-child {
  margin-bottom: 0;
}

.status-message {
  margin-top: 16px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #666;
  font-size: 14px;
}

.no-status {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}

.button-container {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.nginx-status-card {
  margin-bottom: 20px;
}

.nginx-status {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.status-tag {
  width: 100%;
  justify-content: center;
  font-size: 16px;
  padding: 12px;
}

.status-icon {
  margin-right: 8px;
}

.status-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-item .label {
  color: #606266;
}

.detail-item .value {
  font-weight: bold;
}

.status-actions {
  display: flex;
  justify-content: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
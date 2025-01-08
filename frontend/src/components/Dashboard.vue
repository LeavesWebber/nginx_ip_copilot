<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const store = useStore()
const nginxStatus = ref(null)
const nginxConfig = ref('')

const fetchDashboardData = async () => {
  try {
    const status = await store.dispatch('nginx/getStatus')
    nginxStatus.value = status
    nginxConfig.value = status.config
  } catch (error) {
    ElMessage.error('Failed to fetch dashboard data')
  }
}

onMounted(fetchDashboardData)
</script>

<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Nginx Configuration</span>
              <el-button type="primary" size="small" @click="fetchDashboardData">
                Refresh
              </el-button>
            </div>
          </template>
          <pre class="nginx-config">{{ nginxConfig }}</pre>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>System Status</template>
          <div v-if="nginxStatus">
            <p><strong>Status:</strong> {{ nginxStatus.status }}</p>
            <p><strong>Worker Processes:</strong> {{ nginxStatus.worker_processes }}</p>
            <p><strong>Active Connections:</strong> {{ nginxStatus.connections }}</p>
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
  padding: 20px 24px;  /* 调整为与 IPRules.vue 一致的内边距 */
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
  padding-left: 0;  /* 移除额外的左内边距 */
  margin-left: 0;   /* 移除左边距 */
}


/* System Status 标题样式 */
:deep(.el-card__header) {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  padding: 20px 24px;  /* 调整为与 IPRules.vue 一致的内边距 */
  letter-spacing: 0.5px;
  border-bottom: 1px solid #e5e7eb;
  height: 80px;
  display: flex;
  align-items: center;
}

/* 刷新按钮样式 */
:deep(.el-button) {
  border-radius: 12px;
  padding: 10px 24px;
  font-weight: 500;
  font-size: 14px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border: none;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(37, 99, 235, 0.2);
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 40px;
  margin-right: 0;  /* 移除右边距 */
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

/* 刷新按钮样式 */
:deep(.el-button) {
  border-radius: 10px;
  padding: 8px 20px;
  font-weight: 500;
  background: linear-gradient(to right, #2563eb, #3b82f6);
  border: none;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
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

</style>
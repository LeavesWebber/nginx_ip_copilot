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
.dashboard {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nginx-config {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
  max-height: 500px;
  overflow-y: auto;
}
</style>
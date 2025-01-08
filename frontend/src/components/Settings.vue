<script setup lang="ts">
import { ref } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const settings = ref({
  nginxConfigPath: '',
  enableAbuseIPDB: false,
  abuseIPDBApiKey: ''
})

const saveSettings = async () => {
  try {
    await store.dispatch('settings/saveSettings', settings.value)
  } catch (error) {
    console.error('Failed to save settings:', error)
  }
}
</script>

<template>
  <div class="settings">
    <el-card>
      <template #header>
        <span>System Settings</span>
      </template>
      
      <el-form :model="settings" label-width="200px">
        <el-form-item label="Nginx Config Path">
          <el-input v-model="settings.nginxConfigPath" />
        </el-form-item>
        
        <el-form-item label="Enable AbuseIPDB">
          <el-switch v-model="settings.enableAbuseIPDB" />
        </el-form-item>
        
        <el-form-item label="AbuseIPDB API Key" v-if="settings.enableAbuseIPDB">
          <el-input v-model="settings.abuseIPDBApiKey" type="password" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveSettings">Save Settings</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.settings {
  padding: 24px;
  min-width: 1000px;
  width: 100%;
  position: relative;
}

.el-card {
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1),
              0 4px 12px rgba(0, 0, 0, 0.05) !important;
  transition: all 0.3s ease;
  overflow: hidden;
  min-width: 1000px;
  width: 100%;
}

:deep(.el-card__header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  height: 80px;
  border-bottom: 1px solid #e5e7eb;
}

:deep(.el-card__header span) {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(120deg, #2563eb, #3b82f6, #60a5fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 0.5px;
  position: relative;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

:deep(.el-form) {
  padding: 20px 24px;
}

:deep(.el-form-item__label) {
  font-weight: 600 !important;
  color: #1e293b !important;
  font-size: 15px !important;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
  min-width: 100%;
}
</style>
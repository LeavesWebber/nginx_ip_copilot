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
      <template #header>System Settings</template>
      
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
  padding: 20px;
}
</style>
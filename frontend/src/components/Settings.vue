<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const store = useStore()
const settings = ref({
  nginxConfigPath: '',
  enableAbuseIPDB: false,
  abuseIPDBApiKey: ''
})

const isLoading = ref(false)
const isPathModified = ref(false)

// 使用computed属性来控制按钮状态
const isSaveDisabled = computed(() => {
  return !settings.value.nginxConfigPath || settings.value.nginxConfigPath.trim() === ''
})

// 监听配置路径的变化
watch(() => settings.value.nginxConfigPath, (newValue, oldValue) => {
  if (oldValue !== '') {  // 只在初始加载后才标记修改
    isPathModified.value = true
  }
})

const saveSettings = async () => {
  try {
    if (!settings.value.nginxConfigPath) {
      ElMessage.warning('请输入Nginx配置文件路径')
      return
    }

    // 清理路径中的引号
    settings.value.nginxConfigPath = settings.value.nginxConfigPath
      .replace(/["']/g, '')
      .trim()

    // 先验证路径
    const validateResponse = await store.dispatch('settings/validatePath', settings.value.nginxConfigPath)
    const { exists, details } = validateResponse

    if (!exists) {
      // 构建详细的错误消息
      const errors = []
      if (!details.exists) {
        errors.push('配置文件不存在')
      } else {
        if (!details.isFile) {
          errors.push('指定的路径不是一个文件')
        }
        if (!details.canRead) {
          errors.push('无法读取配置文件')
        }
        if (!details.canWrite) {
          errors.push('无法写入配置文件')
        }
      }
      ElMessage.error(errors.join('，'))
      return
    }

    isLoading.value = true
    // 保存设置
    await store.dispatch('settings/saveSettings', settings.value)
    ElMessage.success('配置文件路径设置成功！')
    isPathModified.value = false
  } catch (error: any) {
    console.error('Save settings error:', error)
    ElMessage.error(error.message || '保存设置失败，请检查文件路径是否正确')
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  try {
    const data = await store.dispatch('settings/fetchSettings')
    settings.value = data
    isPathModified.value = false
  } catch (error: any) {
    console.error('Load settings error:', error)
    ElMessage.error('加载设置失败')
  }
})
</script>

<template>
  <div class="settings">
    <el-card>
      <template #header>系统设置</template>
      
      <el-form :model="settings" label-width="200px">
        <el-form-item label="Nginx 配置文件路径">
          <el-input
            v-model="settings.nginxConfigPath" 
            placeholder="Windows示例：D:\nginx\conf\nginx.conf，Linux示例：/etc/nginx/nginx.conf"
          />
          <div class="form-item-tip">
            提示：
            <ul>
              <li>请确保输入的配置文件已存在且有读写权限</li>
              <li>Windows系统请使用完整路径，如：D:\nginx\conf\nginx.conf</li>
              <li>Linux系统请使用完整路径，如：/etc/nginx/nginx.conf</li>
              <li>支持正斜杠(/)和反斜杠(\)作为路径分隔符</li>
              <li>路径中不要包含引号</li>
            </ul>
          </div>
        </el-form-item>
        
        <el-form-item label="启用 AbuseIPDB">
          <el-switch v-model="settings.enableAbuseIPDB" />
        </el-form-item>
        
        <el-form-item label="AbuseIPDB API Key" v-if="settings.enableAbuseIPDB">
          <el-input v-model="settings.abuseIPDBApiKey" type="password" />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            @click="saveSettings"
            :loading="isLoading"
            :disabled="isSaveDisabled"
          >保存设置</el-button>
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

.form-item-tip {
  margin-top: 8px;
  color: #666;
  font-size: 14px;
}

.form-item-tip ul {
  margin: 8px 0;
  padding-left: 20px;
}

.form-item-tip li {
  margin: 4px 0;
}

.is-disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
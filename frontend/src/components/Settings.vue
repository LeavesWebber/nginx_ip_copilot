<template>
  <div class="settings">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
        </div>
      </template>
      
      <el-form :model="form" label-width="120px">
        <el-form-item label="Nginx配置路径">
          <el-input v-model="form.nginxConfigPath">
            <template #append>
              <el-button @click="setNginxPath">保存</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="AbuseIPDB API">
          <el-switch
            v-model="form.enableAbuseIPDB"
            @change="toggleAbuseIPDB"
          ></el-switch>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default defineComponent({
  name: 'Settings',
  setup() {
    const store = useStore()
    
    const form = reactive({
      nginxConfigPath: '',
      enableAbuseIPDB: false
    })
    
    const setNginxPath = async () => {
      try {
        await store.dispatch('updateNginxConfig', form.nginxConfigPath)
        ElMessage.success('配置路径更新成功')
      } catch (error) {
        ElMessage.error('配置路径更新失败')
      }
    }
    
    const toggleAbuseIPDB = async (value: boolean) => {
      // TODO: 实现 AbuseIPDB 开关逻辑
    }
    
    return {
      form,
      setNginxPath,
      toggleAbuseIPDB
    }
  }
})
</script> 
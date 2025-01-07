<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { Monitor, List, Location, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore()
const activeMenu = ref(router.currentRoute.value.name)

const handleLogout = async () => {
  try {
    await store.dispatch('auth/logout')
    router.push('/login')
  } catch (error) {
    console.error('Logout error:', error)
    // 即使出错也跳转到登录页
    router.push('/login')
  }
}
</script>

<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu 
        :default-active="activeMenu"
        router
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#409EFF">
        <el-menu-item index="dashboard" :route="{ name: 'dashboard' }">
          <el-icon><Monitor /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="ip-rules" :route="{ name: 'ip-rules' }">
          <el-icon><List /></el-icon>
          <span>IP Rules</span>
        </el-menu-item>
        <el-menu-item index="geo-rules" :route="{ name: 'geo-rules' }">
          <el-icon><Location /></el-icon>
          <span>Geo Rules</span>
        </el-menu-item>
        <el-menu-item index="settings" :route="{ name: 'settings' }">
          <el-icon><Setting /></el-icon>
          <span>Settings</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>Nginx IP Copilot</h2>
          <el-button type="danger" @click="handleLogout">Logout</el-button>
        </div>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-menu-vertical {
  height: 100%;
  border-right: none;
}

.el-aside {
  background-color: #304156;
}

.el-header {
  background-color: white;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 20px;
}

.header-content {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { Monitor, List, Location, Setting } from '@element-plus/icons-vue'
import AppLogo from './LoginPages/AppLogo.vue'

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
  <div class="app-container">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-content">
        <!-- Logo Section -->
        <div class="logo-section">
          <AppLogo />
        </div>

        <!-- Navigation Menu -->
        <el-menu
            :default-active="activeMenu"
            router
            class="nav-menu"
            :collapse="false">
          <el-menu-item index="dashboard" :route="{ name: 'dashboard' }" class="nav-item">
            <el-icon><Monitor /></el-icon>
            <span>Dashboard</span>
          </el-menu-item>
          <el-menu-item index="ip-rules" :route="{ name: 'ip-rules' }" class="nav-item">
            <el-icon><List /></el-icon>
            <span>IP Rules</span>
          </el-menu-item>
          <el-menu-item index="geo-rules" :route="{ name: 'geo-rules' }" class="nav-item">
            <el-icon><Location /></el-icon>
            <span>Geo Rules</span>
          </el-menu-item>
          <el-menu-item index="settings" :route="{ name: 'settings' }" class="nav-item">
            <el-icon><Setting /></el-icon>
            <span>Settings</span>
          </el-menu-item>
        </el-menu>
      </div>
    </aside>

    <!-- Main Content Area -->
    <div class="main-area">
      <!-- Header -->
      <header class="header">
        <div class="header-content">
          <h2 class="header-title">Nginx IP Copilot</h2>
          <el-button
              type="danger"
              class="logout-button"
              @click="handleLogout">
            Logout
          </el-button>
        </div>
      </header>

      <!-- Page Content -->
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped>
/* 重置样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 容器样式 */
.app-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  background: linear-gradient(-45deg, var(--gradient-background-start), var(--gradient-background-end));
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
}

/* 侧边栏样式 */
.sidebar {
  width: 260px;
  height: 100%;
  background: var(--card-background);
  backdrop-filter: blur(10px);
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 25px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  flex-shrink: 0;
}

.sidebar-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 2rem 0;
}

.logo-section {
  padding: 0 1rem;
  margin-bottom: 2rem;
}

/* 导航菜单样式 */
.nav-menu {
  background: transparent;
  border: none;
  flex-grow: 1;
}

.nav-item {
  margin: 0.5rem 1rem;
  border-radius: 12px;
  height: 50px;
  line-height: 50px;
}

:deep(.el-menu-item) {
  background: transparent !important;
  border-radius: 12px;
  margin: 0.5rem 1rem;
  transition: all 0.3s ease;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(to right, var(--gradient-start), var(--gradient-end)) !important;
  color: white !important;
  transform: translateX(5px);
}

:deep(.el-menu-item:hover) {
  background: linear-gradient(to right, var(--gradient-start), var(--gradient-end)) !important;
  color: white !important;
  transform: translateX(5px);
}

/* 主要内容区域样式 */
.main-area {
  flex-grow: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部样式 */
.header {
  height: 70px;
  background: var(--card-background);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 0 2rem;
  flex-shrink: 0;
}

.header-content {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 1.5rem;
  font-weight: 600;
  background: linear-gradient(to right, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 登出按钮样式 */
.logout-button {
  background: linear-gradient(to right, #ef4444, #dc2626);
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.logout-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(239, 68, 68, 0.3);
}

.logout-button:active {
  transform: translateY(0);
}

/* 主要内容区域样式 */
.main-content {
  flex-grow: 1;
  overflow-y: auto;
  padding: 2rem;
  position: relative;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 背景动画 */
@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-container {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: auto;
  }

  .main-area {
    height: calc(100% - 260px);
  }

  .header {
    padding: 0 1rem;
  }

  .main-content {
    padding: 1rem;
  }
}
</style>
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

/* 主容器样式 */
.app-container {
  display: flex;
  width: 100%;
  height: 100vh;
  min-width: 1460px;  /* 设置最小宽度 = 侧边栏(260px) + 主内容区(1200px) */
  max-width: 1460px;  /* 设置最大宽度 */
  min-height: 800px;  /* 设置最小高度 */
  position: relative;
  background: #ffffff;
  margin: 0 auto;  /* 居中显示 */
}

/* 侧边栏样式 */
.sidebar {
  width: 260px;
  min-width: 260px;
  max-width: 260px;
  height: 100%;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.03);
  z-index: 1000;
  flex-shrink: 0;
  position: fixed;  /* 固定位置 */
  left: auto;  /* 自动计算左侧位置 */
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

/* 主要内容区域 */
.main-area {
  flex: 1;
  width: 1200px;  /* 设置固定宽度 */
  min-width: 1200px;
  max-width: 1200px;
  height: 100%;
  margin-left: 260px;  /* 留出侧边栏的空间 */
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.header {
  height: 70px;
  min-height: 70px;
  width: 1200px;
  min-width: 1200px;
  max-width: 1200px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  padding: 0 24px;
  position: fixed;
  top: 0;
  z-index: 999;
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

/* 内容区域样式 */
.main-content {
  flex: 1;
  width: 1200px;
  min-width: 1200px;
  max-width: 1200px;
  padding-top: 70px;  /* 为固定的header留出空间 */
  overflow-x: hidden;  /* 隐藏横向滚动条 */
  overflow-y: auto;  /* 允许纵向滚动 */
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

/* 确保所有卡片和表格的最小宽度 */
:deep(.el-card), :deep(.el-table) {
  min-width: fit-content;
}

/* 小屏幕处理 */
@media screen and (max-width: 1460px) {
  body {
    overflow-x: auto !important;  /* 允许横向滚动 */
  }

  .app-container {
    width: 1460px;  /* 保持固定宽度 */
    min-width: 1460px;
    position: relative;
  }
}

/* 大屏幕处理 */
@media screen and (min-width: 1920px) {
  .app-container {
    margin: 0 auto;  /* 居中显示 */
    width: 1460px;  /* 保持固定宽度 */
    min-width: 1460px;
    max-width: 1460px;
  }
}
</style>
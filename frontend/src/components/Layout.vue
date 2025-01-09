<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { Monitor, List, Location, Setting, User } from '@element-plus/icons-vue'
import AppLogo from './LoginPages/AppLogo.vue'

const router = useRouter()
const store = useStore()
const activeMenu = ref(router.currentRoute.value.name)
console.log('当前 token:', store.state.auth.token)
console.log('当前 user:', store.state.auth.user)

const username = computed(() => {
  console.log('computed username - user:', store.state.auth.user)
  return store.state.auth.user?.userId || '未登录'
})

const fetchUserInfo = async () => {
  if (store.state.auth.token) {
    console.log('开始获取用户信息')
    try {
      await store.dispatch('auth/fetchUserInfo')
      console.log('获取用户信息成功:', store.state.auth.user)
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  } else {
    console.log('没有 token，跳过获取用户信息')
  }
}

const handleLogout = async () => {
  console.log('开始登出 - 当前状态:', {
    token: localStorage.getItem('token'),
    nginxConfigPath: localStorage.getItem('nginxConfigPath'),
    store: {
      auth: store.state.auth,
      settings: store.state.settings
    }
  })

  try {
    await store.dispatch('auth/logout')
  } catch (error) {
    console.error('Logout error:', error)
  } finally {
    // 无论登出是否成功，都重定向到登录页面
    router.push('/login')
  }

  console.log('登出完成 - 最终状态:', {
    token: localStorage.getItem('token'),
    nginxConfigPath: localStorage.getItem('nginxConfigPath'),
    store: {
      auth: store.state.auth,
      settings: store.state.settings
    }
  })
}

onMounted(async () => {
  console.log('组件挂载，准备获取用户信息')
  console.log('localStorage token:', localStorage.getItem('token'))
  console.log('localStorage nginxConfigPath:', localStorage.getItem('nginxConfigPath'))
  console.log('Vuex store state:', store.state)
  await fetchUserInfo()
})
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

        <!-- 页脚 -->
        <div class="sidebar-footer">
          <a href="https://www.mozilla.org/en-US/MPL/2.0/"
             target="_blank"
             class="footer-link">
            <el-icon class="footer-icon"><Document /></el-icon>
            <span>MPL v2</span>
          </a>
          <span class="footer-separator">|</span>
          <a href="https://github.com/LeavesWebber/nginx_ip_copilot"
             target="_blank"
             class="footer-link">
            <svg class="github-icon" viewBox="0 0 16 16" width="16" height="16">
              <path fill="currentColor" d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"></path>
            </svg>
            <span>GitHub</span>
          </a>
        </div>
      </div>
    </aside>

    <!-- Main Content Area -->
    <div class="main-area">
      <!-- Header -->
      <header class="header">
        <div class="header-content">
          <h2 class="header-title">Nginx IP Copilot</h2>
          <div class="header-right">
            <div class="user-info">
              <el-icon class="user-icon"><User /></el-icon>
              <span class="username">{{ username }}</span>
            </div>
            <el-button type="danger" class="logout-button" @click="handleLogout">
              Logout
            </el-button>
          </div>
        </div>
      </header>

      <!-- 路由视图 -->
      <main class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style>
/* 全局渐变色变量 */
:root {
  --gradient-start: #3b82f6; /* 蓝色起始 */
  --gradient-middle: #6366f1; /* 靛蓝色中间 */
  --gradient-end: #8b5cf6; /* 紫色结束 */
}
</style>

<style scoped>
/* 重置样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 网格布局容器 */
.app-container {
  display: grid;
  grid-template-columns: 260px 1fr;
  grid-template-rows: 100vh;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
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
}

.logo-section {
  padding: 2rem 1rem;
}

/* 导航菜单样式 */
.nav-menu {
  flex: 1;
  border: none;
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

/* 主内容区域 */
.main-area {
  grid-column: 2;
  display: grid;
  grid-template-rows: 70px 1fr;
  height: 100vh;
  overflow: hidden;
}

/* 头部 */
.header {
  grid-row: 1;
  height: 70px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  padding: 0 24px;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
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

/* 头部右侧区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #f3f4f6;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: #e5e7eb;
}

.user-icon {
  color: #6b7280;
  font-size: 16px;
}

.username {
  color: #374151;
  font-size: 14px;
  font-weight: 500;
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

/* 页面内容 */
.page-content {
  grid-row: 2;
  overflow: auto;
  padding: 24px;
  background: #f5f7fa;
}

/* 侧边栏页脚样式，调整后确保在页面底部正中间 */
.sidebar-footer {
  padding: 20px;

  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  width: 100%;
  position: sticky;
  bottom: 0;
}

.footer-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: var(--gradient-start);
}

.footer-separator {
  color: #666;
}

.footer-icon,.github-icon {
  width: 16px;
  height: 16px;
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
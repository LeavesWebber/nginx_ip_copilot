<template>
  <div class="dashboard">
    <el-container>
      <el-aside width="200px">
        <el-menu>
          <el-menu-item index="1">IP 规则管理</el-menu-item>
          <el-menu-item index="2">地理位置规则</el-menu-item>
          <el-menu-item index="3">系统设置</el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'Dashboard',
  setup() {
    // 组件逻辑
  }
})
</script> 
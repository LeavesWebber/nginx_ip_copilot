<script setup lang="ts">
import { ref } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const nginxStatus = ref(null)

// TODO: Implement dashboard data fetching
const fetchDashboardData = async () => {
  try {
    await store.dispatch('nginx/getStatus')
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error)
  }
}
</script>

<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>System Status</template>
          <!-- Add system status information -->
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>Active Rules</template>
          <!-- Add active rules summary -->
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>Recent Activities</template>
          <!-- Add recent activities list -->
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px;
}
</style>
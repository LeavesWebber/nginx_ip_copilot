<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const rules = ref([])

const fetchRules = async () => {
  try {
    await store.dispatch('ipRules/fetchRules')
  } catch (error) {
    console.error('Failed to fetch IP rules:', error)
  }
}

onMounted(fetchRules)
</script>

<template>
  <div class="ip-rules">
    <el-card>
      <template #header>
        <div class="header">
          <span>IP Rules Management</span>
          <el-button type="primary">Add Rule</el-button>
        </div>
      </template>
      
      <el-table :data="rules">
        <el-table-column prop="ip" label="IP Address" />
        <el-table-column prop="type" label="Type" />
        <el-table-column prop="comment" label="Comment" />
        <el-table-column prop="created_at" label="Created At" />
        <el-table-column prop="status" label="Status" />
        <el-table-column label="Actions">
          <template #default="scope">
            <el-button type="danger" size="small">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.ip-rules {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const geoRules = ref([])

const fetchGeoRules = async () => {
  try {
    await store.dispatch('geoRules/fetchRules')
  } catch (error) {
    console.error('Failed to fetch geo rules:', error)
  }
}

onMounted(fetchGeoRules)
</script>

<template>
  <div class="geo-rules">
    <el-card>
      <template #header>
        <div class="header">
          <span>Geographic Rules Management</span>
          <el-button type="primary">Add Geo Rule</el-button>
        </div>
      </template>
      
      <el-table :data="geoRules">
        <el-table-column prop="country_code" label="Country Code" />
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
.geo-rules {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
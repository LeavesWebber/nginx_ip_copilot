<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const store = useStore()
const geoRules = ref([])
const dialogVisible = ref(false)
const newRule = ref({
  country_code: '',
  comment: ''
})

const fetchGeoRules = async () => {
  try {
    await store.dispatch('geoRules/fetchRules')
    geoRules.value = store.state.geoRules.rules
  } catch (error) {
    ElMessage.error('获取地理位置规则失败')
  }
}

const handleAddRule = async () => {
  try {
    await store.dispatch('geoRules/addRule', newRule.value)
    ElMessage.success('添加规则成功')
    dialogVisible.value = false
    newRule.value = { country_code: '', comment: '' }
    fetchGeoRules()
  } catch (error) {
    ElMessage.error('添加规则失败')
  }
}

onMounted(fetchGeoRules)
</script>

<template>
  <div class="geo-rules">
    <el-card>
      <template #header>
        <div class="header">
          <span>地理位置规则管理</span>
          <el-button type="primary" @click="dialogVisible = true">添加规则</el-button>
        </div>
      </template>
      
      <el-table :data="geoRules">
        <el-table-column prop="country_code" label="国家代码" />
        <el-table-column prop="comment" label="备注" />
        <el-table-column prop="created_at" label="创建时间" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="danger" size="small" 
              @click="store.dispatch('geoRules/deleteRule', scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加地理位置规则">
      <el-form :model="newRule" label-width="120px">
        <el-form-item label="国家代码">
          <el-input v-model="newRule.country_code" placeholder="例如：CN, US, JP" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="newRule.comment" placeholder="请输入规则备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddRule">确定</el-button>
      </template>
    </el-dialog>
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
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const store = useStore()
const rules = ref([])
const dialogVisible = ref(false)
const newRule = ref({
  ip: '',
  ip_range: '',
  type: 'single_ip',
  comment: ''
})

const fetchRules = async () => {
  try {
    await store.dispatch('ipRules/fetchRules')
    rules.value = store.state.ipRules.rules
  } catch (error) {
    ElMessage.error('获取规则失败')
  }
}

const handleAddRule = async () => {
  try {
    const ruleData = {
      ...newRule.value,
      // 根据类型选择使用 ip 或 ip_range
      ...(newRule.value.type === 'single_ip' 
        ? { ip: newRule.value.ip } 
        : { ip_range: newRule.value.ip_range })
    }
    // 删除不需要的字段
    if (ruleData.type === 'single_ip') {
      delete ruleData.ip_range
    } else {
      delete ruleData.ip
    }
    
    await store.dispatch('ipRules/addRule', ruleData)
    ElMessage.success('添加规则成功')
    dialogVisible.value = false
    // 重置表单
    newRule.value = {
      ip: '',
      ip_range: '',
      type: 'single_ip',
      comment: ''
    }
    fetchRules()
  } catch (error) {
    ElMessage.error('添加规则失败')
  }
}

onMounted(fetchRules)
</script>

<template>
  <div class="ip-rules">
    <el-card>
      <template #header>
        <div class="header">
          <span>IP 规则管理</span>
          <el-button type="primary" @click="dialogVisible = true">添加规则</el-button>
        </div>
      </template>
      
      <el-table :data="rules">
        <el-table-column label="IP地址">
          <template #default="scope">
            {{ scope.row.type === 'single_ip' ? scope.row.ip : scope.row.ip_range }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="comment" label="备注" />
        <el-table-column prop="created_at" label="创建时间" />
        <el-table-column prop="status" label="状态" />
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加IP规则">
      <el-form :model="newRule">
        <el-form-item label="类型">
          <el-select v-model="newRule.type">
            <el-option label="单个IP" value="single_ip" />
            <el-option label="IP范围" value="ip_range" />
          </el-select>
        </el-form-item>
        <el-form-item :label="newRule.type === 'single_ip' ? 'IP地址' : 'IP范围'">
          <el-input 
            v-if="newRule.type === 'single_ip'"
            v-model="newRule.ip"
            placeholder="例如：192.168.1.1"
          />
          <el-input 
            v-else
            v-model="newRule.ip_range"
            placeholder="例如：192.168.1.0/24"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="newRule.comment" />
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
.ip-rules {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
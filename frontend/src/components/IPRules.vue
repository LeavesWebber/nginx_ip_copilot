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

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  try {
    // 尝试处理不同格式的日期字符串
    const date = dateStr.includes('T') 
      ? new Date(dateStr)  // ISO格式
      : new Date(dateStr.replace(' ', 'T')); // MySQL格式
    
    if (isNaN(date.getTime())) return '-'
    
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    })
  } catch (e) {
    return '-'
  }
}

const nginxStatus = ref({
  status: '',
  worker_processes: 0,
  connections: 0,
  config: ''
})

const fetchRules = async () => {
  try {
    // 检查是否设置了 Nginx 配置文件路径
    const nginxConfigPath = localStorage.getItem('nginxConfigPath')
    if (!nginxConfigPath) {
      rules.value = []
      return
    }

    await store.dispatch('ipRules/fetchRules')
    rules.value = store.state.ipRules.rules
  } catch (error) {
    console.error('获取规则失败:', error)
    ElMessage.error('获取规则失败')
  }
}

const handleAddRule = async () => {
  try {
    // 检查是否设置了 Nginx 配置文件路径
    const nginxConfigPath = localStorage.getItem('nginxConfigPath')
    if (!nginxConfigPath) {
      ElMessage.warning('请先在设置页面配置 Nginx 配置文件路径')
      return
    }

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
    console.error('添加规则失败:', error)
    ElMessage.error('添加规则失败')
  }
}

const fetchNginxStatus = async () => {
  try {
    const { data } = await store.dispatch('nginx/getStatus')
    nginxStatus.value = data
  } catch (error) {
    console.error('获取Nginx状态失败:', error)
  }
}

const handleDeleteRule = async (ruleId: string) => {
  try {
    await store.dispatch('ipRules/deleteRule', ruleId)
    ElMessage.success('删除规则成功')
    await fetchRules()
  } catch (error) {
    ElMessage.error('删除规则失败')
  }
}

onMounted(async () => {
  await Promise.all([
    fetchRules(),
    fetchNginxStatus()
  ])
})
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
      
      <el-alert
        v-if="!nginxStatus?.status || nginxStatus.status !== 'running'"
        title="Nginx 未运行"
        type="warning"
        description="当前 Nginx 服务未运行，IP 规则相关配置可能无法生效。请检查 Nginx 服务状态。"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
      />
      
      <el-table :data="rules">
        <el-table-column label="IP地址">
          <template #default="scope">
            {{ scope.row.type === 'single_ip' ? scope.row.ip : scope.row.ip_range }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型">
          <template #default="scope">
            {{ scope.row.type === 'single_ip' ? '单个IP' : 'IP范围' }}
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="备注" />
        <el-table-column prop="createdAt" label="创建时间">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'info'">
              {{ scope.row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="danger" size="small" @click="handleDeleteRule(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
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
  padding: 24px;
  width: 1200px; /* 固定宽度 */
  margin: 0 auto; /* 居中显示 */
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: none;
  height: 80px;
}

.header span {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(120deg, #2563eb, #3b82f6, #60a5fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 0.5px;
  position: relative;
  padding-bottom: 4px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

:deep(.el-button) {
  border-radius: 12px;
  padding: 10px 24px;
  font-weight: 500;
  font-size: 14px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border: none;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(37, 99, 235, 0.2);
  display: flex;
  align-items: center;
  gap: 6px;
  height: 40px;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.3);
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
}

:deep(.el-button:active) {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(37, 99, 235, 0.2);
}

.el-card {
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1),
              0 4px 12px rgba(0, 0, 0, 0.05) !important;
  transition: all 0.3s ease;
  overflow: hidden;
}
/* 弹出框底部按钮容器 */
:deep(.el-dialog__footer) {
  padding: 20px 24px;
  display: flex;
  justify-content: flex-end;  /* 靠右对齐 */
  align-items: center;  /* 垂直居中 */
  gap: 12px;  /* 按钮之间的间距 */
}

/* 取消按钮样式 */
:deep(.el-dialog__footer .el-button:first-child) {
  background: #f1f5f9;
  color: #475569;
  box-shadow: none;
  border: none;
  margin-right: 0;  /* 移除默认的右边距 */
}

:deep(.el-dialog__footer .el-button:first-child:hover) {
  background: #e2e8f0;
  transform: translateY(-2px);
}

/* 确定按钮样式 */
:deep(.el-dialog__footer .el-button:last-child) {
  margin-left: 0;  /* 移除默认的左边距 */
}

/* 弹出框表单样式优化 */
:deep(.el-dialog) {
  width: 500px !important;
  min-width: 500px !important;
  max-width: 500px !important;
  position: fixed !important;
  top: 50% !important;
  left: 50% !important;
  transform: translate(-50%, -50%) !important;
  margin: 0 !important;
  border-radius: 16px;
  overflow: hidden;
}

/* 弹出框头部样式 */
:deep(.el-dialog__header) {
  padding: 20px 24px;
  margin: 0;
  border-bottom: 1px solid #e5e7eb;
  background: #ffffff;
}

/* 弹出框内容区域样式 */
:deep(.el-dialog__body) {
  padding: 24px;
  background: #ffffff;
  width: 500px !important;
  min-width: 500px !important;
  max-width: 500px !important;
}

/* 弹出框底部按钮区域样式 */
:deep(.el-dialog__footer) {
  padding: 20px 24px;
  background: #ffffff;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

/* 遮罩层样式 */
:deep(.el-overlay) {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 2000;
  background-color: rgba(0, 0, 0, 0.5);
  overflow: auto;
}

/* 小屏幕适配 */
@media screen and (max-width: 1460px) {
  :deep(.el-dialog) {
    position: fixed !important;
    width: 500px !important;
    min-width: 500px !important;
    max-width: 500px !important;
    top: 50% !important;
    left: 50% !important;
    transform: translate(-50%, -50%) !important;
    margin: 0 !important;
  }

  /* 确保遮罩层覆盖整个可滚动区域 */
  :deep(.el-overlay) {
    position: fixed;
    width: 100%;
    height: 100%;
    min-width: 1460px;  /* 与页面最小宽度一致 */
  }
}

/* 大屏幕适配 */
@media screen and (min-width: 1920px) {
  :deep(.el-dialog) {
    position: fixed !important;
    width: 500px !important;
    min-width: 500px !important;
    max-width: 500px !important;
    top: 50% !important;
    left: 50% !important;
    transform: translate(-50%, -50%) !important;
    margin: 0 !important;
  }
}

/* 确保按钮在同一水平线上 */
:deep(.el-dialog__footer .el-button) {
  margin: 0;  /* 移除所有默认边距 */
  height: 40px;  /* 统一按钮高度 */
  line-height: 40px;  /* 确保文字垂直居中 */
  padding: 0 24px;  /* 统一水平内边距 */
}
</style>
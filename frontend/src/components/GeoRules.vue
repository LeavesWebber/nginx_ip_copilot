<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const store = useStore()
const geoRules = ref([])
const dialogVisible = ref(false)
const newRule = ref({
  country_code: '',
  comment: ''
})
const geoModuleStatus = ref({
  isInstalled: false,
  isEnabled: false,
  message: ''
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

const checkGeoModuleStatus = async () => {
  try {
    const response = await axios.get('/api/nginx/geo-module/status')
    geoModuleStatus.value = response.data
  } catch (error) {
    ElMessage.error('检查GeoIP2模块状态失败')
  }
}

const fetchGeoRules = async () => {
  try {
    // 检查是否设置了 Nginx 配置文件路径
    const nginxConfigPath = localStorage.getItem('nginxConfigPath')
    if (!nginxConfigPath) {
      geoRules.value = []
      return
    }

    await store.dispatch('geoRules/fetchRules')
    geoRules.value = store.state.geoRules.rules
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

    await store.dispatch('geoRules/addRule', newRule.value)
    ElMessage.success('添加规则成功')
    dialogVisible.value = false
    // 重置表单
    newRule.value = {
      country_code: '',
      comment: ''
    }
    fetchGeoRules()
  } catch (error) {
    console.error('添加规则失败:', error)
    ElMessage.error('添加规则失败')
  }
}

const handleDeleteRule = async (ruleId: string) => {
  try {
    await store.dispatch('geoRules/deleteRule', ruleId)
    ElMessage.success('删除规则成功')
    await fetchGeoRules()
  } catch (error) {
    ElMessage.error('删除规则失败')
  }
}

onMounted(async () => {
  await checkGeoModuleStatus()
  await fetchGeoRules()
})
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
      
      <el-alert
        v-if="!geoModuleStatus.isInstalled || !geoModuleStatus.isEnabled"
        title="Geo 模块未安装"
        type="warning"
        description="当前 Geo 模块未正确安装或启用，地理位置配置可能无法生效。请在仪表盘中检查模块状态。"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
      />
      
      <el-table :data="geoRules">
        <el-table-column prop="country_code" label="国家代码" />
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
            <el-button type="danger" size="small" 
              @click="handleDeleteRule(scope.row.id)">
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
/* 容器样式 */
.geo-rules {
  padding: 24px;
  width: 1200px; /* 固定宽度 */
  margin: 0 auto; /* 居中显示 */
  position: relative;
}

/* 卡片样式 */
.el-card {
  width: 100%;
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1),
              0 4px 12px rgba(0, 0, 0, 0.05) !important;
  transition: all 0.3s ease;
  overflow: hidden;
  min-width: 1000px;
  width: 100%;
}

/* 卡片头部样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: none;
  height: 80px;
}

/* 标题样式 */
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

/* 添加按钮样式 */
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

/* 按钮悬浮效果 */
:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.3);
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
}

/* 按钮点击效果 */
:deep(.el-button:active) {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(37, 99, 235, 0.2);
}

/* 表格样式 */
:deep(.el-table) {
  min-width: 900px;
  width: 100%;
  margin: 16px;
}

/* 弹出框样式 */
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

/* 弹出框按钮样式 */
:deep(.el-dialog__footer .el-button) {
  margin: 0;
  height: 40px;
  line-height: 40px;
  padding: 0 24px;
}

/* 取消按钮样式 */
:deep(.el-dialog__footer .el-button:first-child) {
  background: #f1f5f9;
  color: #475569;
  box-shadow: none;
  border: none;
}

:deep(.el-dialog__footer .el-button:first-child:hover) {
  background: #e2e8f0;
  transform: translateY(-2px);
}

/* 确定按钮样式 */
:deep(.el-dialog__footer .el-button:last-child) {
  margin-left: 0;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border: none;
  color: white;
  box-shadow: 0 4px 15px rgba(37, 99, 235, 0.2);
}

:deep(.el-dialog__footer .el-button:last-child:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.3);
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
}
</style>
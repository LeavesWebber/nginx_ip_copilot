<template>
  <div class="geo-rules">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>地理位置规则管理</span>
          <el-button type="primary" @click="showAddDialog">添加规则</el-button>
        </div>
      </template>
      
      <el-table :data="rules">
        <el-table-column prop="country_code" label="国家代码"></el-table-column>
        <el-table-column prop="comment" label="备注"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="danger" @click="deleteRule(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加规则对话框 -->
    <el-dialog v-model="dialogVisible" title="添加地理位置规则">
      <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef">
        <el-form-item label="国家代码" prop="country_code">
          <el-input v-model="ruleForm.country_code"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input v-model="ruleForm.comment"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRule">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default defineComponent({
  name: 'GeoRules',
  setup() {
    const store = useStore()
    const rules = ref([])
    const dialogVisible = ref(false)
    
    const ruleForm = reactive({
      country_code: '',
      comment: ''
    })
    
    const loadRules = async () => {
      await store.dispatch('fetchGeoRules')
      rules.value = store.state.geoRules
    }
    
    // 初始加载
    loadRules()
    
    return {
      rules,
      dialogVisible,
      ruleForm
    }
  }
})
</script> 
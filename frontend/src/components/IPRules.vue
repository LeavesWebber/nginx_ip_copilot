<template>
  <div class="ip-rules">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>IP 规则管理</span>
          <el-button type="primary" @click="showAddDialog">添加规则</el-button>
        </div>
      </template>
      
      <el-table :data="rules">
        <el-table-column prop="ip" label="IP地址"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
        <el-table-column prop="comment" label="备注"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="danger" @click="deleteRule(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'IPRules',
  setup() {
    const rules = ref([])
    
    // TODO: 实现规则管理逻辑
    
    return {
      rules
    }
  }
})
</script> 
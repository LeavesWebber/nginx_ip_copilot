<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useStore()

const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  try {
    // TODO: Implement login logic using store action
    await store.dispatch('auth/login', loginForm.value)
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error('Login failed')
  }
}
</script>

<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>Nginx IP Copilot</h2>
      <el-form :model="loginForm">
        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="Username" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.password" type="password" placeholder="Password" />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" block>Login</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
  padding: 20px;
}
</style>
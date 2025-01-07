<template>
    <form class="login-form" @submit.prevent="handleSubmit">
      <FormInput
        v-model="username"
        id="username"
        label="Username"
        type="text"
        :error="errors.username"
        required
      />
      <FormInput
        v-model="password"
        id="password"
        label="Password"
        type="password"
        :error="errors.password"
        required
      />
      <AppButton 
        type="submit"
        :loading="isLoading"
        :disabled="isLoading"
      >
        {{ isLoading ? 'Logging in...' : 'Login' }}
      </AppButton>
    </form>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { useStore } from 'vuex'
  import FormInput from './FormInput.vue'
  import AppButton from './AppButton.vue'
  import { ElMessage } from 'element-plus'
  
  const router = useRouter()
  const store = useStore()
  const username = ref('')
  const password = ref('')
  const isLoading = ref(false)
  const errors = reactive({
    username: '',
    password: ''
  })
  
  const handleSubmit = async () => {
    errors.username = !username.value ? '请输入用户名' : ''
    errors.password = !password.value ? '请输入密码' : ''
    
    if (!errors.username && !errors.password) {
      isLoading.value = true
      try {
        await store.dispatch('auth/login', {
          username: username.value,
          password: password.value
        })
        router.push('/dashboard')
      } catch (error) {
        ElMessage.error('登录失败')
      } finally {
        isLoading.value = false
      }
    }
  }
  </script>
  
  <style scoped>
  .login-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  </style>
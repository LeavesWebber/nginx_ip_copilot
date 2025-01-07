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
  import FormInput from './FormInput.vue'
  import AppButton from './AppButton.vue'
  import { useLogin } from '../../../useLogin'
  
  const username = ref('')
  const password = ref('')
  const errors = reactive({})
  const { login, isLoading } = useLogin()
  
  const handleSubmit = async () => {
    errors.username = !username.value ? 'Username is required' : ''
    errors.password = !password.value ? 'Password is required' : ''
    
    if (!errors.username && !errors.password) {
      await login(username.value, password.value)
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
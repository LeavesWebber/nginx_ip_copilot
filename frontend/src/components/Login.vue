<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import AppLogo from './LoginPages/AppLogo.vue'
import FormInput from './LoginPages/FormInput.vue'
import AppButton from './LoginPages/AppButton.vue'

const router = useRouter()
const store = useStore()
const isLoading = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  errors.username = !loginForm.value.username ? 'Username is required' : ''
  errors.password = !loginForm.value.password ? 'Password is required' : ''

  if (!errors.username && !errors.password) {
    isLoading.value = true
    try {
      await store.dispatch('auth/login', loginForm.value)
      router.push('/dashboard')
    } catch (error) {
      ElMessage.error('Login failed')
    } finally {
      isLoading.value = false
    }
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <AppLogo />
      <h1>Nginx IP Copilot</h1>
      <form @submit.prevent="handleLogin" class="login-form">
        <FormInput
          v-model="loginForm.username"
          id="username"
          label="Username"
          type="text"
          :error="errors.username"
        />
        <FormInput
          v-model="loginForm.password"
          id="password"
          label="Password"
          type="password"
          :error="errors.password"
        />
        <AppButton 
          type="submit"
          :loading="isLoading"
          :disabled="isLoading"
        >
          {{ isLoading ? 'Logging in...' : 'Login' }}
        </AppButton>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  width: 200%;
  height: 200%;
  opacity: 0.1;
  animation: move 30s linear infinite;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: var(--card-background);
  padding: 2.5rem;
  border-radius: 24px;
  box-shadow: 
    0 10px 25px rgba(0, 0, 0, 0.1),
    0 0 1px rgba(255, 255, 255, 0.2) inset;
  backdrop-filter: blur(10px);
  animation: slideUp 0.5s ease-out;
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  z-index: 1;
}

.login-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 70%);
  transform: rotate(0deg);
  animation: rotate 20s linear infinite;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

h1 {
  text-align: center;
  font-size: 1.75rem;
  margin-bottom: 2rem;
  color: var(--text-color);
  font-weight: 700;
  background: linear-gradient(to right, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

@keyframes gradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes move {
  0% { transform: translate(0, 0); }
  100% { transform: translate(-50%, -50%); }
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>


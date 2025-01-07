import { ref } from 'vue'

export function useLogin() {
  const isLoading = ref(false)

  const login = async (username, password) => {
    isLoading.value = true
    
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000))
      console.log('Login attempt:', { username, password })
      
      // Add your actual login logic here
      
    } catch (error) {
      console.error('Login failed:', error)
    } finally {
      isLoading.value = false
    }
  }

  return {
    login,
    isLoading
  }
}
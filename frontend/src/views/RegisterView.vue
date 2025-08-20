<!-- src/views/RegisterView.vue -->
<template>
  <div
    class="min-h-screen bg-gradient-to-br from-blue-50 to-slate-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8"
  >
    <div class="max-w-md w-full">
      <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-8">
        <!-- Header -->
        <div class="text-center mb-8">
          <div
            class="w-12 h-12 bg-blue-700 rounded-xl flex items-center justify-center mx-auto mb-4"
          >
            <UserPlus class="w-6 h-6 text-white" />
          </div>
          <h2 class="text-3xl font-semibold text-slate-900 font-poppins">Create Account</h2>
          <p class="text-slate-600 mt-2 font-inter">Join our digital insurance platform</p>
        </div>

        <!-- Register Form -->
        <form @submit.prevent="handleRegister" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2 font-inter">
              Full Name
            </label>
            <input
              v-model="registerForm.name"
              type="text"
              required
              class="w-full px-4 py-3 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter"
              placeholder="Enter your full name"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2 font-inter">
              Email Address
            </label>
            <input
              v-model="registerForm.email"
              type="email"
              required
              class="w-full px-4 py-3 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter"
              placeholder="Enter your email"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2 font-inter">
              Phone Number
            </label>
            <input
              v-model="registerForm.phone"
              type="tel"
              required
              class="w-full px-4 py-3 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter"
              placeholder="+1-123-456-7890"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2 font-inter">
              Address
            </label>
            <input
              v-model="registerForm.address"
              type="text"
              required
              class="w-full px-4 py-3 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter"
              placeholder="Enter your address"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2 font-inter">
              Password
            </label>
            <input
              v-model="registerForm.password"
              type="password"
              required
              minlength="8"
              class="w-full px-4 py-3 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter"
              placeholder="Create a password (min 8 characters)"
            />
          </div>

          <AppButton type="submit" variant="primary" class="w-full" :disabled="isLoading">
            <span v-if="!isLoading">Create Account</span>
            <span v-else class="flex items-center justify-center space-x-2">
              <div
                class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
              ></div>
              <span>Creating Account...</span>
            </span>
          </AppButton>

          <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-lg">
            <p class="text-red-600 text-sm font-inter">{{ errorMessage }}</p>
          </div>
        </form>

        <!-- Footer -->
        <div class="mt-6 text-center">
          <p class="text-slate-600 font-inter">
            Already have an account?
            <router-link
              to="/login"
              class="text-blue-700 hover:text-blue-800 font-medium transition-colors duration-200"
            >
              Sign in
            </router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppButton from '@/components/common/AppButton.vue'
import { UserPlus } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const registerForm = reactive({
  name: '',
  email: '',
  phone: '',
  address: '',
  password: '',
  role: 'USER',
})

const isLoading = ref(false)
const errorMessage = ref('')

const handleRegister = async () => {
  try {
    isLoading.value = true
    errorMessage.value = ''

    await authStore.register(registerForm)
    router.push('/policies')
  } catch (error: any) {
    errorMessage.value = error.message || 'Registration failed. Please try again.'
  } finally {
    isLoading.value = false
  }
}
</script>

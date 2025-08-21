<!-- src/views/admin/AdminLoginView.vue -->
<template>
  <div
    class="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-800 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8"
  >
    <div class="max-w-md w-full space-y-8">
      <!-- Admin Login Card -->
      <div
        class="bg-white rounded-2xl shadow-2xl border border-slate-200 p-8 relative overflow-hidden"
      >
        <!-- Background Pattern -->
        <div
          class="absolute inset-0 bg-gradient-to-br from-blue-50 to-transparent opacity-50"
        ></div>
        <div class="absolute -top-4 -right-4 w-20 h-20 bg-blue-100 rounded-full opacity-20"></div>
        <div class="absolute -bottom-4 -left-4 w-16 h-16 bg-blue-200 rounded-full opacity-30"></div>

        <!-- Content -->
        <div class="relative z-10">
          <!-- Header -->
          <div class="text-center mb-8">
            <div
              class="w-16 h-16 bg-gradient-to-r from-blue-600 to-blue-800 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg"
            >
              <Shield class="w-8 h-8 text-white" />
            </div>
            <h2 class="text-3xl font-bold text-slate-900 font-poppins">Admin Portal</h2>
            <p class="text-slate-600 mt-2 font-inter">Secure access to admin dashboard</p>

            <!-- Connection Status -->
            <div class="mt-4 flex items-center justify-center space-x-2">
              <div class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
              <span class="text-xs text-slate-500">System Online</span>
            </div>
          </div>

          <!-- Alert Messages -->
          <div
            v-if="route.query.expired"
            class="mb-6 p-4 bg-orange-50 border border-orange-200 rounded-lg"
          >
            <div class="flex items-center">
              <Clock class="w-5 h-5 text-orange-600 mr-3" />
              <p class="text-orange-800 text-sm font-medium">Session Expired</p>
            </div>
            <p class="text-orange-700 text-sm mt-1">
              Your admin session has expired. Please log in again.
            </p>
          </div>

          <div
            v-if="route.query.error === 'insufficient_permissions'"
            class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg"
          >
            <div class="flex items-center">
              <AlertTriangle class="w-5 h-5 text-red-600 mr-3" />
              <p class="text-red-800 text-sm font-medium">Access Denied</p>
            </div>
            <p class="text-red-700 text-sm mt-1">
              You don't have sufficient permissions to access the admin panel.
            </p>
          </div>

          <!-- Admin Login Form -->
          <form @submit.prevent="handleAdminLogin" class="space-y-6">
            <!-- Email Field -->
            <div>
              <label for="email" class="block text-sm font-medium text-slate-700 mb-2 font-inter">
                Admin Email
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Mail class="h-5 w-5 text-slate-400" />
                </div>
                <input
                  id="email"
                  v-model="loginForm.email"
                  type="email"
                  autocomplete="email"
                  required
                  class="w-full pl-10 pr-4 py-3 border border-slate-300 rounded-lg text-slate-900 placeholder-slate-400 focus:border-blue-600 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter"
                  placeholder="Enter admin email"
                  :disabled="isLoading"
                />
              </div>
            </div>

            <!-- Password Field -->
            <div>
              <label
                for="password"
                class="block text-sm font-medium text-slate-700 mb-2 font-inter"
              >
                Password
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock class="h-5 w-5 text-slate-400" />
                </div>
                <input
                  id="password"
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  autocomplete="current-password"
                  required
                  class="w-full pl-10 pr-12 py-3 border border-slate-300 rounded-lg text-slate-900 placeholder-slate-400 focus:border-blue-600 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter"
                  placeholder="Enter password"
                  :disabled="isLoading"
                />
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center"
                  :disabled="isLoading"
                >
                  <EyeOff v-if="showPassword" class="h-5 w-5 text-slate-400 hover:text-slate-600" />
                  <Eye v-else class="h-5 w-5 text-slate-400 hover:text-slate-600" />
                </button>
              </div>
            </div>

            <!-- Remember Me -->
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <input
                  id="remember-me"
                  v-model="loginForm.rememberMe"
                  type="checkbox"
                  class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-slate-300 rounded"
                  :disabled="isLoading"
                />
                <label for="remember-me" class="ml-2 block text-sm text-slate-700 font-inter">
                  Remember me
                </label>
              </div>
            </div>

            <!-- Submit Button -->
            <AppButton
              type="submit"
              variant="primary"
              class="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 shadow-lg hover:shadow-xl transform hover:scale-[1.02] transition-all duration-200"
              :disabled="isLoading"
              size="large"
            >
              <span v-if="!isLoading" class="flex items-center justify-center space-x-2">
                <LogIn class="w-5 h-5" />
                <span>Sign In to Admin</span>
              </span>
              <span v-else class="flex items-center justify-center space-x-2">
                <div
                  class="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"
                ></div>
                <span>Signing In...</span>
              </span>
            </AppButton>

            <!-- Error Message -->
            <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-lg">
              <div class="flex items-start">
                <AlertCircle class="w-5 h-5 text-red-600 mr-3 mt-0.5 flex-shrink-0" />
                <div>
                  <p class="text-red-800 text-sm font-medium">Login Failed</p>
                  <p class="text-red-700 text-sm mt-1">{{ errorMessage }}</p>
                </div>
              </div>

              <!-- Debug Token Info -->
              <div v-if="debugTokenInfo && isDevelopment" class="mt-4 pt-3 border-t border-red-200">
                <details class="text-red-700">
                  <summary class="cursor-pointer text-xs font-medium hover:text-red-900">
                    Debug Token Information
                  </summary>
                  <pre class="mt-2 bg-red-100 p-3 rounded text-xs overflow-x-auto font-mono">{{
                    debugTokenInfo
                  }}</pre>
                </details>
              </div>
            </div>

            <!-- Success Message -->
            <div v-if="successMessage" class="p-4 bg-green-50 border border-green-200 rounded-lg">
              <div class="flex items-center">
                <CheckCircle class="w-5 h-5 text-green-600 mr-3" />
                <p class="text-green-800 text-sm font-medium">{{ successMessage }}</p>
              </div>
            </div>
          </form>

          <!-- Footer Links -->
          <div class="mt-8 text-center space-y-4">
            <div class="flex items-center justify-center space-x-4 text-sm text-slate-500">
              <div class="flex items-center space-x-1">
                <Shield class="w-4 h-4" />
                <span>Secure Login</span>
              </div>
              <div class="w-1 h-1 bg-slate-300 rounded-full"></div>
              <div class="flex items-center space-x-1">
                <Clock class="w-4 h-4" />
                <span>24/7 Access</span>
              </div>
            </div>

            <router-link
              to="/"
              class="inline-flex items-center text-blue-600 hover:text-blue-700 font-medium transition-colors duration-200 text-sm"
            >
              <ArrowLeft class="w-4 h-4 mr-2" />
              Back to main site
            </router-link>
          </div>
        </div>
      </div>

      <!-- System Info -->
      <div class="text-center">
        <p class="text-slate-400 text-xs">Digital Insurance Management System v2.0</p>
        <p class="text-slate-500 text-xs mt-1">© 2025 All rights reserved</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { decodeJWT } from '@/utils/jwt'
import AppButton from '@/components/common/AppButton.vue'
import {
  Shield,
  LogIn,
  Mail,
  Lock,
  Eye,
  EyeOff,
  AlertCircle,
  CheckCircle,
  ArrowLeft,
  Clock,
  AlertTriangle,
} from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// Form state
const loginForm = reactive({
  email: 'admin@insurance.com',
  password: 'admin123',
  rememberMe: false,
})

// Component state
const isLoading = ref(false)
const showPassword = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const debugTokenInfo = ref('')

// Computed properties
const isDevelopment = computed(() => import.meta.env.DEV)

// Check if already authenticated and redirect
if (authStore.isAuthenticated && authStore.isAdmin) {
  router.push('/admin/dashboard')
}

// Methods
const handleAdminLogin = async () => {
  try {
    isLoading.value = true
    errorMessage.value = ''
    successMessage.value = ''
    debugTokenInfo.value = ''

    // Validate form
    if (!loginForm.email || !loginForm.password) {
      throw new Error('Please fill in all required fields')
    }

    // Attempt login
    await authStore.adminLogin({
      email: loginForm.email,
      password: loginForm.password,
    })

    // Verify admin role after login
    const payload = authStore.tokenPayload
    if (!payload || payload.role !== 'ADMIN') {
      throw new Error(`Invalid role: Expected ADMIN, got ${payload?.role || 'undefined'}`)
    }

    // Success
    successMessage.value = 'Login successful! Redirecting...'

    // Handle redirect
    const redirectPath = (route.query.redirect as string) || '/admin/dashboard'

    // Small delay for user feedback
    setTimeout(() => {
      router.push(redirectPath)
    }, 1000)
  } catch (error: any) {
    console.error('Admin login error:', error)

    // Set error message
    errorMessage.value = error.message || 'Admin login failed. Please try again.'

    // Debug information in development
    if (isDevelopment.value && authStore.token) {
      try {
        const payload = decodeJWT(authStore.token)
        debugTokenInfo.value = JSON.stringify(
          {
            payload,
            tokenExists: !!authStore.token,
            isAdmin: authStore.isAdmin,
            isAuthenticated: authStore.isAuthenticated,
          },
          null,
          2,
        )
      } catch (debugError) {
        debugTokenInfo.value = `Debug error: ${debugError}`
      }
    }

    // Clear form password on error
    loginForm.password = ''
  } finally {
    isLoading.value = false
  }
}

// Clear messages when form changes
const clearMessages = () => {
  errorMessage.value = ''
  successMessage.value = ''
  debugTokenInfo.value = ''
}

// Watch form changes
import { watch } from 'vue'
watch([() => loginForm.email, () => loginForm.password], clearMessages)
</script>

<style scoped>
/* Custom animations */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}

/* Focus styles */
input:focus {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* Loading state */
input:disabled {
  background-color: #f8fafc;
  cursor: not-allowed;
}

/* Button hover effects */
button:not(:disabled):hover {
  transform: translateY(-1px);
}
</style>

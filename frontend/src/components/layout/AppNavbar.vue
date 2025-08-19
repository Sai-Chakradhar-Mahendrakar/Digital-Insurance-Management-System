<!-- src/components/layout/AppNavbar.vue -->
<template>
  <header class="bg-white/80 backdrop-blur-md border-b border-slate-200 sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 lg:px-8 py-4">
      <nav class="flex items-center justify-between">
        <!-- Logo and Brand -->
        <router-link to="/" class="flex items-center space-x-2">
          <div class="w-8 h-8 bg-blue-700 rounded-lg flex items-center justify-center">
            <span class="text-white font-bold text-sm">DI</span>
          </div>
          <h1 class="text-xl font-semibold text-slate-900 font-poppins">Digital Insurance</h1>
        </router-link>

        <!-- Navigation Links (Always Visible) -->
        <div class="hidden md:flex items-center space-x-8">
          <router-link
            to="/policies"
            class="text-slate-600 hover:text-blue-700 font-medium transition-colors duration-200 relative hover:after:w-full after:w-0 after:h-0.5 after:bg-blue-700 after:absolute after:bottom-0 after:left-0 after:transition-all after:duration-200"
          >
            All Policies
          </router-link>
          <router-link
            v-if="isAuthenticated"
            to="/my-policies"
            class="text-slate-600 hover:text-blue-700 font-medium transition-colors duration-200 relative hover:after:w-full after:w-0 after:h-0.5 after:bg-blue-700 after:absolute after:bottom-0 after:left-0 after:transition-all after:duration-200"
          >
            My Policies
          </router-link>
          <router-link
            v-if="isAuthenticated"
            to="/claims"
            class="text-slate-600 hover:text-blue-700 font-medium transition-colors duration-200 relative hover:after:w-full after:w-0 after:h-0.5 after:bg-blue-700 after:absolute after:bottom-0 after:left-0 after:transition-all after:duration-200"
          >
            Claims
          </router-link>
        </div>

        <!-- Right Side Actions -->
        <div class="flex items-center space-x-4">
          <!-- Guest Actions -->
          <template v-if="!isAuthenticated">
            <AppButton variant="ghost" @click="navigateTo('/login')" class="hidden md:inline-flex">
              Login
            </AppButton>
            <AppButton variant="primary" @click="navigateTo('/register')"> Get Started </AppButton>
          </template>

          <!-- Authenticated Actions -->
          <template v-else>
            <!-- Notifications -->
            <div class="relative" ref="notificationRef">
              <button
                @click="toggleNotifications"
                class="relative p-2 text-slate-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg transition-colors duration-200"
              >
                <Bell class="w-5 h-5" />
                <!-- Notification Badge -->
                <span
                  v-if="notificationCount > 0"
                  class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center font-medium"
                >
                  {{ notificationCount > 9 ? '9+' : notificationCount }}
                </span>
              </button>

              <!-- Notifications Dropdown -->
              <div
                v-if="showNotifications"
                class="absolute right-0 top-12 w-80 bg-white rounded-xl shadow-lg border border-slate-200 py-2 z-50"
              >
                <div class="px-4 py-2 border-b border-slate-100">
                  <h3 class="font-semibold text-slate-900">Notifications</h3>
                </div>
                <div class="max-h-80 overflow-y-auto">
                  <div
                    v-for="notification in notifications"
                    :key="notification.id"
                    class="px-4 py-3 hover:bg-slate-50 border-b border-slate-50 last:border-b-0"
                  >
                    <div class="flex items-start space-x-3">
                      <div class="w-2 h-2 bg-blue-500 rounded-full mt-2 flex-shrink-0"></div>
                      <div>
                        <p class="text-sm font-medium text-slate-900">{{ notification.title }}</p>
                        <p class="text-xs text-slate-600 mt-1">{{ notification.message }}</p>
                        <p class="text-xs text-slate-400 mt-1">
                          {{ formatNotificationTime(notification.createdAt) }}
                        </p>
                      </div>
                    </div>
                  </div>
                  <div
                    v-if="notifications.length === 0"
                    class="px-4 py-8 text-center text-slate-500"
                  >
                    <Bell class="w-8 h-8 mx-auto mb-2 text-slate-300" />
                    <p class="text-sm">No new notifications</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Profile Dropdown -->
            <div class="relative" ref="profileRef">
              <button
                @click="toggleProfile"
                class="flex items-center space-x-2 p-2 text-slate-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg transition-colors duration-200"
              >
                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <User class="w-4 h-4 text-blue-700" />
                </div>
                <ChevronDown class="w-4 h-4" />
              </button>

              <!-- Profile Dropdown Menu -->
              <div
                v-if="showProfile"
                class="absolute right-0 top-12 w-64 bg-white rounded-xl shadow-lg border border-slate-200 py-2 z-50"
              >
                <!-- User Info -->
                <div class="px-4 py-3 border-b border-slate-100">
                  <p class="font-semibold text-slate-900">{{ user?.name || 'John Doe' }}</p>
                  <p class="text-sm text-slate-600">{{ user?.email || 'john@example.com' }}</p>
                </div>

                <!-- Menu Items -->
                <div class="py-1">
                  <button
                    @click="navigateTo('/help')"
                    class="w-full px-4 py-2 text-left text-slate-700 hover:bg-slate-50 flex items-center space-x-3"
                  >
                    <HelpCircle class="w-4 h-4" />
                    <span>Help & Support</span>
                  </button>
                </div>

                <!-- Logout -->
                <div class="border-t border-slate-100 pt-1">
                  <button
                    @click="handleLogout"
                    class="w-full px-4 py-2 text-left text-red-600 hover:bg-red-50 flex items-center space-x-3"
                  >
                    <LogOut class="w-4 h-4" />
                    <span>Logout</span>
                  </button>
                </div>
              </div>
            </div>
          </template>
        </div>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppButton from '@/components/common/AppButton.vue'
import {
  Bell,
  User,
  ChevronDown,
  Settings,
  Shield,
  CreditCard,
  HelpCircle,
  LogOut,
} from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const showNotifications = ref(false)
const showProfile = ref(false)
const notificationRef = ref<HTMLElement>()
const profileRef = ref<HTMLElement>()

const notifications = ref([
  {
    id: 1,
    title: 'Policy Renewal Due',
    message: 'Your health insurance policy expires in 30 days',
    createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
  },
  {
    id: 2,
    title: 'Claim Approved',
    message: 'Your recent claim has been approved and processed',
    createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
  },
])

const isAuthenticated = computed(() => authStore.isAuthenticated)
const user = computed(() => authStore.user)
const notificationCount = computed(() => notifications.value.length)

const navigateTo = (path: string) => {
  router.push(path)
  showProfile.value = false
  showNotifications.value = false
}

const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value
  showProfile.value = false
}

const toggleProfile = () => {
  showProfile.value = !showProfile.value
  showNotifications.value = false
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
  showProfile.value = false
}

const formatNotificationTime = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60))

  if (diffInHours < 1) return 'Just now'
  if (diffInHours < 24) return `${diffInHours}h ago`
  return date.toLocaleDateString()
}

// Handle clicks outside dropdowns
const handleClickOutside = (event: Event) => {
  if (notificationRef.value && !notificationRef.value.contains(event.target as Node)) {
    showNotifications.value = false
  }
  if (profileRef.value && !profileRef.value.contains(event.target as Node)) {
    showProfile.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

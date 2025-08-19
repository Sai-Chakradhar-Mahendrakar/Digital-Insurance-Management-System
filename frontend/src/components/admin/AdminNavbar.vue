<!-- src/components/admin/AdminNavbar.vue -->
<template>
  <header class="bg-white border-b border-slate-200 sticky top-0 z-40">
    <div class="px-6 py-4">
      <div class="flex items-center justify-between">
        <!-- Logo and Brand -->
        <div class="flex items-center space-x-4">
          <div
            class="w-8 h-8 bg-gradient-to-r from-blue-600 to-blue-800 rounded-lg flex items-center justify-center"
          >
            <span class="text-white font-bold text-sm">DI</span>
          </div>
          <div>
            <h1 class="text-xl font-bold text-slate-900 font-poppins">Admin Panel</h1>
            <p class="text-xs text-slate-500">Digital Insurance Management</p>
          </div>
        </div>

        <!-- Right Side Actions -->
        <div class="flex items-center space-x-4">
          <!-- Notifications -->
          <button
            class="p-2 text-slate-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg transition-colors"
          >
            <Bell class="w-5 h-5" />
          </button>

          <!-- Profile Dropdown -->
          <div class="relative" ref="profileRef">
            <button
              @click="showProfile = !showProfile"
              class="flex items-center space-x-3 p-2 text-slate-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg transition-colors"
            >
              <div
                class="w-8 h-8 bg-gradient-to-r from-blue-100 to-blue-200 rounded-full flex items-center justify-center"
              >
                <User class="w-4 h-4 text-blue-700" />
              </div>
              <div class="text-left hidden md:block">
                <p class="text-sm font-medium text-slate-900">{{ user?.name || 'Admin' }}</p>
                <p class="text-xs text-slate-500">Administrator</p>
              </div>
              <ChevronDown class="w-4 h-4" />
            </button>

            <!-- Dropdown Menu -->
            <div
              v-if="showProfile"
              class="absolute right-0 top-12 w-48 bg-white rounded-xl shadow-lg border border-slate-200 py-2 z-50"
            >
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
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Bell, User, ChevronDown, LogOut } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const showProfile = ref(false)
const profileRef = ref<HTMLElement>()

const user = computed(() => authStore.user)

const handleLogout = () => {
  authStore.logout()
  router.push('/auth/login')
}

const handleClickOutside = (event: Event) => {
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

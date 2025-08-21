import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'

const activeUsers = ref<number>(0)
const isLoadingActiveUsers = ref(false)
const errorActiveUsers = ref<string | null>(null)

const fetchActiveUsers = async () => {
  isLoadingActiveUsers.value = true
  errorActiveUsers.value = null
  try {
    const appStore = useAppStore()
    const authStore = useAuthStore()
    const response = await fetch(`${appStore.config.apiBaseUrl}/admin/activeUsers`, {
        headers: {
        Authorization: `Bearer ${authStore.token}`,
      },
      credentials: 'include',
    })
    if (!response.ok) throw new Error('Failed to fetch active users')
    const data = await response.json()
    // Fix: check for undefined/null, not falsy
    if (typeof data.totalElements === 'number') {
      activeUsers.value = data.totalElements
    } else if (Array.isArray(data.users)) {
      activeUsers.value = data.users.length
    } else {
      activeUsers.value = 0
    }
  } catch (err: any) {
    errorActiveUsers.value = err.message || 'Failed to fetch active users'
    activeUsers.value = 0
  } finally {
    isLoadingActiveUsers.value = false
  }
}

export function useAdminUserStore() {
  return {
    activeUsers,
    isLoadingActiveUsers,
    errorActiveUsers,
    fetchActiveUsers,
  }
}

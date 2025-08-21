<template>
  <div class="min-h-screen bg-slate-50">
    <AdminNavbar />

    <div class="flex">
      <AdminSidebar />

      <!-- Main Content -->
      <main class="flex-1 p-8">
        <div class="max-w-7xl mx-auto">
          <!-- Header -->
          <div class="mb-8">
            <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">Send Notification</h1>
            <p class="text-slate-600 font-inter">
              Send custom notifications to users or groups and view recent notifications
            </p>
          </div>

          <!-- Send Notification Form -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 mb-8">
            <h3 class="text-lg font-semibold text-slate-900 mb-4">Quick Notification</h3>
            <form @submit.prevent="sendNotification" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Title</label>
                <input
                  v-model="form.title"
                  type="text"
                  required
                  class="w-full border border-slate-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Message</label>
                <textarea
                  v-model="form.message"
                  required
                  rows="3"
                  class="w-full border border-slate-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                ></textarea>
              </div>
              <div class="flex items-center space-x-2">
                <input id="important" v-model="form.important" type="checkbox" class="h-4 w-4 text-blue-600" />
                <label for="important" class="text-sm text-slate-700">Mark as Important</label>
              </div>
              <AppButton
                type="submit"
                :disabled="notificationStore.isLoading"
                variant="primary"
                class="flex items-center space-x-2"
              >
                <span v-if="!notificationStore.isLoading">Send Notification</span>
                <span v-else>Sending...</span>
              </AppButton>
            </form>
            <div v-if="successMessage" class="mt-4 text-green-600 font-medium">
              {{ successMessage }}
            </div>
            <div v-if="notificationStore.error" class="mt-4 text-red-600 font-medium">
              {{ notificationStore.error }}
            </div>
          </div>

          <!-- Recent Notifications -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
            <h3 class="text-lg font-semibold text-slate-900 mb-4">Recent Notifications</h3>
            <div v-if="notificationStore.isLoading" class="text-slate-500">Loading...</div>
            <div v-else-if="notifications.length === 0" class="text-slate-500">No notifications sent yet.</div>
            <div v-else class="space-y-4">
              <div
                v-for="notification in notifications"
                :key="notification.id"
                class="flex items-center space-x-4 p-3 hover:bg-slate-50 rounded-lg"
              >
                <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                  <component
                    :is="notification.type === 'SYSTEM_ALERT' ? 'AlertTriangle' : 'Bell'"
                    class="w-5 h-5"
                    :class="notification.type === 'SYSTEM_ALERT' ? 'text-red-600' : 'text-blue-600'"
                  />
                </div>
                <div class="flex-1">
                  <p class="text-sm font-medium text-slate-900">
                    {{ notification.message.split(': ')[0] || 'Notification' }}
                  </p>
                  <p class="text-xs text-slate-500">{{ formatDate(notification.createdAt) }}</p>
                  <span
                    v-if="notification.type === 'SYSTEM_ALERT'"
                    class="ml-2 px-2 py-1 text-xs rounded-full bg-red-100 text-red-700"
                  >
                    Important
                  </span>
                </div>
                <div>
                  <span
                    class="inline-block px-2 py-1 text-xs rounded-full"
                    :class="notification.status === 'UNREAD' ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'"
                  >
                    {{ notification.status }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useNotificationStore } from '@/stores/notification'
import { useAuthStore } from '@/stores/auth'
import AdminNavbar from '@/components/admin/AdminNavbar.vue'
import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AppButton from '@/components/common/AppButton.vue'
import { Bell, AlertTriangle } from 'lucide-vue-next'

const notificationStore = useNotificationStore()
const authStore = useAuthStore()

const form = ref({
  title: '',
  message: '',
  important: false,
})

const successMessage = ref('')

const notifications = computed(() => notificationStore.notifications)

const sendNotification = async () => {
  if (!authStore.isAdmin) {
    notificationStore.error = 'Only admins can send notifications'
    setTimeout(() => (notificationStore.error = null), 3000)
    return
  }

  const success = await notificationStore.sendNotification({
    ...form.value,
    userId: undefined, // or set a specific user/group if needed
  })

  if (success) {
    successMessage.value = '✅ Notification sent successfully!'
    form.value = { title: '', message: '', important: false }
    setTimeout(() => (successMessage.value = ''), 3000)
    notificationStore.fetchNotifications()
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  notificationStore.fetchNotifications()
})
</script>
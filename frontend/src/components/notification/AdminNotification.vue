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

          <!-- Bulk Notification Form -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 mb-8">
            <h3 class="text-lg font-semibold text-slate-900 mb-4">Bulk Notification</h3>
            <form @submit.prevent="sendBulk" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1"
                  >User IDs (comma separated)</label
                >
                <input
                  v-model="bulkForm.userIds"
                  type="text"
                  required
                  class="w-full border border-slate-300 rounded-lg px-3 py-2"
                  placeholder="e.g. 2,3,4"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Message</label>
                <textarea
                  v-model="bulkForm.message"
                  required
                  rows="2"
                  class="w-full border border-slate-300 rounded-lg px-3 py-2"
                ></textarea>
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Type</label>
                <select
                  v-model="bulkForm.type"
                  class="w-full border border-slate-300 rounded-lg px-3 py-2"
                >
                  <option value="GENERAL">General</option>
                  <option value="SYSTEM_ALERT">Important</option>
                </select>
              </div>
              <AppButton type="submit" :disabled="notificationStore.isLoading">Send Bulk</AppButton>
            </form>
          </div>

          <!-- Policy Notification Form -->
          <!-- Policy Notification Form -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 mb-8">
            <h3 class="text-lg font-semibold text-slate-900 mb-4">Policy Notification</h3>
            <p class="text-sm text-slate-600 mb-4">
              Send notifications to all users who have purchased a specific policy
            </p>

            <form @submit.prevent="sendByPolicy" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Policy ID</label>
                <input
                  v-model="policyForm.policyId"
                  type="number"
                  required
                  min="1"
                  class="w-full border border-slate-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Enter policy ID (e.g., 1, 2, 3)"
                />
                <p class="text-xs text-slate-500 mt-1">
                  Enter the ID of the policy whose users should receive this notification
                </p>
              </div>

              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Message</label>
                <textarea
                  v-model="policyForm.message"
                  required
                  rows="3"
                  class="w-full border border-slate-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Enter message for all users with this policy..."
                ></textarea>
              </div>

              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1"
                  >Notification Type</label
                >
                <select
                  v-model="policyForm.type"
                  class="w-full border border-slate-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="GENERAL">General Notification</option>
                  <option value="POLICY_RENEWAL">Policy Renewal</option>
                  <option value="SYSTEM_ALERT">Important System Alert</option>
                </select>
              </div>

              <AppButton
                type="submit"
                :disabled="notificationStore.isLoading"
                variant="primary"
                class="flex items-center space-x-2"
              >
                <span v-if="!notificationStore.isLoading">Send to Policy Users</span>
                <span v-else class="flex items-center space-x-2">
                  <div
                    class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
                  ></div>
                  <span>Sending...</span>
                </span>
              </AppButton>
            </form>
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

const bulkForm = ref({
  userIds: '',
  message: '',
  type: 'GENERAL',
})

const policyForm = ref({
  policyId: '', // Keep as string for form handling
  message: '',
  type: 'GENERAL',
})

const successMessage = ref('')

const notifications = computed(() => notificationStore.notifications)

// Add the missing clearMessages function
const clearMessages = () => {
  setTimeout(() => {
    successMessage.value = ''
    notificationStore.error = null
  }, 5000)
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

const sendNotification = async () => {
  if (!authStore.isAdmin) {
    notificationStore.error = 'Only admins can send notifications'
    clearMessages()
    return
  }

  try {
    const success = await notificationStore.sendNotification({
      ...form.value,
      userId: undefined,
    })

    if (success) {
      successMessage.value = '✅ Notification sent successfully!'
      form.value = { title: '', message: '', important: false }
      await notificationStore.fetchNotifications()
      clearMessages()
    }
  } catch (error) {
    console.error('Failed to send notification:', error)
  }
}

const sendBulk = async () => {
  try {
    // Clear previous errors
    notificationStore.error = null

    // Validate input
    if (!bulkForm.value.userIds.trim()) {
      notificationStore.error = 'Please provide user IDs'
      clearMessages()
      return
    }

    if (!bulkForm.value.message.trim()) {
      notificationStore.error = 'Please provide a message'
      clearMessages()
      return
    }

    // Parse user IDs - convert to Long array as expected by backend
    const userIdArr = bulkForm.value.userIds
      .split(',')
      .map((id: string) => {
        const trimmedId = id.trim()
        if (!trimmedId) return null
        const parsed = Number(trimmedId)
        return isNaN(parsed) ? null : parsed
      })
      .filter((id): id is number => id !== null)

    if (userIdArr.length === 0) {
      notificationStore.error = 'Please provide valid user IDs (numbers only, comma-separated)'
      clearMessages()
      return
    }

    console.log('Sending bulk notification to users:', userIdArr)
    console.log('Message:', bulkForm.value.message)
    console.log('Type:', bulkForm.value.type)

    // Ensure payload matches backend expectations
    const success = await notificationStore.sendBulkNotification({
      userId: userIdArr,
      request: {
        message: bulkForm.value.message,
        type: bulkForm.value.type,
      },
    })

    if (success) {
      successMessage.value = '✅ Bulk notification sent successfully!'
      bulkForm.value = { userIds: '', message: '', type: 'GENERAL' }
      await notificationStore.fetchNotifications()
      clearMessages()
    }
  } catch (error) {
    console.error('Failed to send bulk notification:', error)
    notificationStore.error =
      'Failed to send bulk notification. Please check the console for details.'
    clearMessages()
  }
}

// Fixed sendByPolicy function
const sendByPolicy = async () => {
  try {
    // Clear previous errors
    notificationStore.error = null

    // Validate input - Fix: Check if policyId is empty string, not using .trim() on number
    if (!policyForm.value.policyId || policyForm.value.policyId.toString().trim() === '') {
      notificationStore.error = 'Please provide a policy ID'
      clearMessages()
      return
    }

    if (!policyForm.value.message.trim()) {
      notificationStore.error = 'Please provide a message'
      clearMessages()
      return
    }

    // Convert to number and validate
    const policyId = Number(policyForm.value.policyId)
    if (isNaN(policyId) || policyId <= 0) {
      notificationStore.error = 'Please provide a valid policy ID (must be a positive number)'
      clearMessages()
      return
    }

    console.log('Sending policy notification:', {
      policyId: policyId,
      message: policyForm.value.message,
      type: policyForm.value.type,
    })

    const success = await notificationStore.sendPolicyNotification({
      policyId: policyId,
      request: {
        message: policyForm.value.message,
        type: policyForm.value.type,
      },
    })

    if (success) {
      successMessage.value =
        '✅ Policy notification sent successfully to all users with this policy!'
      policyForm.value = { policyId: '', message: '', type: 'GENERAL' }
      await notificationStore.fetchNotifications()
      clearMessages()
    }
  } catch (error) {
    console.error('Failed to send policy notification:', error)
    notificationStore.error = 'Failed to send policy notification. Please try again.'
    clearMessages()
  }
}

const refreshNotifications = async () => {
  try {
    await notificationStore.fetchNotifications()
    successMessage.value = 'Notifications refreshed successfully!'
    clearMessages()
  } catch (error) {
    console.error('Failed to refresh notifications:', error)
    notificationStore.error = 'Failed to refresh notifications'
    clearMessages()
  }
}

// Load notifications on component mount
onMounted(async () => {
  await notificationStore.fetchNotifications()
})
</script>

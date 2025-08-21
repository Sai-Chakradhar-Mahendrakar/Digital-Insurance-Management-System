import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useAppStore } from './app'
import { useAuthStore } from './auth'

export interface Notification {
  id: number
  userId: number
  message: string
  type: 'POLICY_RENEWAL' | 'CLAIM_UPDATE' | 'SUPPORT_RESPONSE' | 'SYSTEM_ALERT' | 'GENERAL'
  status: 'UNREAD' | 'READ'
  createdAt: string
  readAt: string | null
}

export interface CreateNotification {
  title: string
  message: string
  important: boolean
  userId?: number
}
export interface BulkNotificationRequest {
  userId: number[] // This becomes List<Long> in Java
  request: {
    message: string
    type: string
  }
}
export interface PolicyNotificationRequest {
  policyId: number
  request: { message: string; type: string }
}

export const useNotificationStore = defineStore('notification', () => {
  const appStore = useAppStore()
  const authStore = useAuthStore()

  const notifications = ref<Notification[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const unreadNotifications = computed(() =>
    notifications.value.filter((n) => n.status === 'UNREAD'),
  )
  const importantNotifications = computed(() =>
    notifications.value.filter((n) => n.type === 'SYSTEM_ALERT'),
  )
  const filteredNotifications = (filter: 'ALL' | 'UNREAD' | 'IMPORTANT') =>
    computed(() => {
      if (filter === 'UNREAD') return unreadNotifications.value
      if (filter === 'IMPORTANT') return importantNotifications.value
      return notifications.value
    })

  // GET /notifications/{userId}
  const fetchNotifications = async () => {
    isLoading.value = true
    error.value = null
    try {
      const url = `${appStore.config.apiBaseUrl}/notifications/user`
      const response = await appStore.httpClient.get(url, {
        headers: { Authorization: `Bearer ${authStore.token}` },
      })
      if (!response.ok) throw new Error('Failed to fetch notifications')
      const data = await response.json()
      notifications.value = data.notifications.map((n: any) => ({
        ...n,
        createdAt:
          typeof n.createdAt === 'string' ? n.createdAt : new Date(n.createdAt).toISOString(),
        readAt: n.readAt
          ? typeof n.readAt === 'string'
            ? n.readAt
            : new Date(n.readAt).toISOString()
          : null,
      }))
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch notifications'
    } finally {
      isLoading.value = false
    }
  }

  // POST /notifications/send
  const sendNotification = async (notification: CreateNotification) => {
    if (!authStore.isAdmin) {
      error.value = 'Only admins can send notifications'
      return false
    }
    isLoading.value = true
    error.value = null
    try {
      const payload: any = {
        userId: notification.userId,
        message: notification.title
          ? `${notification.title}: ${notification.message}`
          : notification.message,
        type: notification.important ? 'SYSTEM_ALERT' : 'GENERAL',
      }
      const response = await appStore.httpClient.post(
        `${appStore.config.apiBaseUrl}/notifications/send`,
        payload,
      )
      if (!response.ok) throw new Error('Failed to send notification')
      const newNotification = await response.json()
      notifications.value.unshift(newNotification)
      return true
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to send notification'
      return false
    } finally {
      isLoading.value = false
    }
  }

  // PUT /notifications/{notificationId}/read

  const sendBulkNotification = async (payload: BulkNotificationRequest) => {
    isLoading.value = true
    error.value = null
    try {
      console.log('Sending bulk notification payload:', payload)

      const response = await appStore.httpClient.post(
        `${appStore.config.apiBaseUrl}/admin/sendNotifications/user`,
        payload,
        {
          headers: {
            Authorization: `Bearer ${authStore.token}`,
            'Content-Type': 'application/json',
          },
        },
      )

      console.log('Response status:', response.status)

      if (!response.ok) {
        const errorText = await response.text()
        console.error('Error response:', errorText)
        throw new Error(`Failed to send bulk notification: ${response.status} - ${errorText}`)
      }

      return true
    } catch (err) {
      console.error('Bulk notification error:', err)
      error.value = err instanceof Error ? err.message : 'Failed to send bulk notification'
      return false
    } finally {
      isLoading.value = false
    }
  }

  const sendPolicyNotification = async (payload: PolicyNotificationRequest) => {
    isLoading.value = true
    error.value = null
    try {
      console.log('Sending policy notification payload:', payload)

      const response = await appStore.httpClient.post(
        `${appStore.config.apiBaseUrl}/admin/sendNotifications/policy`,
        payload,
        {
          headers: {
            Authorization: `Bearer ${authStore.token}`,
            'Content-Type': 'application/json',
          },
        },
      )

      console.log('Policy notification response status:', response.status)

      if (!response.ok) {
        const errorText = await response.text()
        console.error('Policy notification error response:', errorText)
        throw new Error(`Failed to send policy notification: ${response.status} - ${errorText}`)
      }

      return true
    } catch (err) {
      console.error('Policy notification error:', err)
      error.value = err instanceof Error ? err.message : 'Failed to send policy notification'
      return false
    } finally {
      isLoading.value = false
    }
  }

  // In your notification store
  const markAsRead = async (notificationId: number) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await appStore.httpClient.put(
        `${appStore.config.apiBaseUrl}/notifications/${notificationId}/read`,
        undefined,
        {
          headers: {
            Authorization: `Bearer ${authStore.token}`,
          },
        },
      )

      if (!response.ok) {
        throw new Error('Failed to mark notification as read')
      }

      // Update the notification in the local state
      const notification = notifications.value.find((n) => n.id === notificationId)
      if (notification) {
        notification.status = 'READ'
        notification.readAt = new Date().toISOString()
      }

      return true
    } catch (err) {
      console.error('Mark as read error:', err)
      error.value = err instanceof Error ? err.message : 'Failed to mark notification as read'
      return false
    } finally {
      isLoading.value = false
    }
  }

  const markAllAsRead = async () => {
    isLoading.value = true
    error.value = null
    try {
      const response = await appStore.httpClient.put(
        `${appStore.config.apiBaseUrl}/notifications/read-all`,
        undefined,
        {
          headers: {
            Authorization: `Bearer ${authStore.token}`,
          },
        },
      )

      if (!response.ok) {
        throw new Error('Failed to mark all as read')
      }

      // Update all notifications to read in local state
      notifications.value.forEach((n) => {
        if (n.status === 'UNREAD') {
          n.status = 'READ'
          n.readAt = new Date().toISOString()
        }
      })

      return true
    } catch (err) {
      console.error('Mark all as read error:', err)
      error.value = err instanceof Error ? err.message : 'Failed to mark all as read'
      return false
    } finally {
      isLoading.value = false
    }
  }

  const onDropdownOpen = async (notification: Notification) => {
    if (notification.status === 'UNREAD') {
      await markAsRead(notification.id)
    }
  }

  return {
    notifications,
    isLoading,
    error,
    filteredNotifications,
    fetchNotifications,
    sendNotification,
    sendBulkNotification,
    sendPolicyNotification,
    markAsRead,
    markAllAsRead,
  }
})

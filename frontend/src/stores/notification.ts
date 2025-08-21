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
  userId: number[]
  request: { message: string; type: string }
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
          // If you need to send cookies, ensure httpClient is configured to send them (withCredentials: true)
        },
      )
      if (!response.ok) throw new Error('Failed to mark notification as read')
      const notification = notifications.value.find((n) => n.id === notificationId)
      if (notification) {
        notification.status = 'READ'
        notification.readAt = new Date().toISOString()
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to mark notification as read'
    } finally {
      isLoading.value = false
    }
  }

  const sendBulkNotification = async (payload: BulkNotificationRequest) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await appStore.httpClient.post(
        `${appStore.config.apiBaseUrl}/admin/sendNotifications/user`,
        payload,
        { headers: { Authorization: `Bearer ${authStore.token}` } },
      )
      if (!response.ok) throw new Error('Failed to send bulk notification')
      return true
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to send bulk notification'
      return false
    } finally {
      isLoading.value = false
    }
  }

  // Add sendPolicyNotification
  const sendPolicyNotification = async (payload: PolicyNotificationRequest) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await appStore.httpClient.post(
        `${appStore.config.apiBaseUrl}/admin/sendNotifications/policy`,
        payload,
        { headers: { Authorization: `Bearer ${authStore.token}` } },
      )
      if (!response.ok) throw new Error('Failed to send policy notification')
      return true
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to send policy notification'
      return false
    } finally {
      isLoading.value = false
    }
  }

  // Add markAllAsRead
  const markAllAsRead = async () => {
    isLoading.value = true
    error.value = null
    try {
      const response = await appStore.httpClient.put(
        `${appStore.config.apiBaseUrl}/notifications/read-all`,
        undefined,
        { headers: { Authorization: `Bearer ${authStore.token}` } },
      )
      if (!response.ok) throw new Error('Failed to mark all as read')
      notifications.value.forEach((n) => {
        n.status = 'READ'
        n.readAt = new Date().toISOString()
      })
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to mark all as read'
    } finally {
      isLoading.value = false
    }
  }

  const onDropdownOpen = async (notification: Notification) => {
    if (notification.status === 'UNREAD') {
      await notificationStore.markAsRead(notification.id)
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

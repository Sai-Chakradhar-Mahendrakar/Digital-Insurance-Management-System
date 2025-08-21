<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-slate-500 bg-opacity-50" @click="handleBackdropClick">
    <div class="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 relative mx-4" @click.stop>
      <!-- Close Button -->
      <button
        @click="$emit('close')"
        class="absolute top-4 right-4 text-slate-500 hover:text-slate-700 transition-colors duration-200 p-1 rounded-full hover:bg-slate-100"
      >
        <X class="w-5 h-5" />
      </button>
      
      <!-- Content -->
      <div class="pr-8"> <!-- Add padding right to avoid close button -->
        <h2 class="text-xl font-semibold text-slate-900 mb-2">
          {{ notification.message.split(': ')[0] || 'Notification' }}
        </h2>
        <p class="text-sm text-slate-500 mb-4">{{ formatDate(notification.createdAt) }}</p>
        <p class="text-slate-700 leading-relaxed mb-4">{{ notification.message }}</p>
        
        <!-- Notification Details -->
        <div class="space-y-2 border-t border-slate-100 pt-4">
          <div class="flex justify-between text-sm">
            <span class="text-slate-500">Type:</span>
            <span class="text-slate-700 font-medium">{{ formatNotificationType(notification.type) }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-slate-500">Status:</span>
            <span 
              :class="notification.status === 'UNREAD' ? 'text-blue-600' : 'text-green-600'"
              class="font-medium"
            >
              {{ notification.status }}
            </span>
          </div>
          <div v-if="notification.readAt" class="flex justify-between text-sm">
            <span class="text-slate-500">Read at:</span>
            <span class="text-slate-700">{{ formatDate(notification.readAt) }}</span>
          </div>
        </div>
      </div>
      
      <!-- Action Buttons (optional) -->
      <div class="flex justify-end space-x-3 mt-6 pt-4 border-t border-slate-100">
        <button 
          @click="$emit('close')"
          class="px-4 py-2 text-sm font-medium text-slate-600 hover:text-slate-800 bg-slate-100 hover:bg-slate-200 rounded-lg transition-colors duration-200"
        >
          Close
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { X } from 'lucide-vue-next'

interface Notification {
  id: number
  userId: number
  message: string
  type: 'POLICY_RENEWAL' | 'CLAIM_UPDATE' | 'SUPPORT_RESPONSE' | 'SYSTEM_ALERT' | 'GENERAL'
  status: 'UNREAD' | 'READ'
  createdAt: string
  readAt: string | null
}

defineProps<{ notification: Notification }>()

// Only declare emit once
const emit = defineEmits<{
  (e: 'close'): void
}>()

const handleBackdropClick = () => {
  // Close when clicking outside the modal
  emit('close')
}

const handleEscapeKey = (event: KeyboardEvent) => {
  if (event.key === 'Escape') {
    emit('close')
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('en-IN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const formatNotificationType = (type: string) => {
  const typeMap = {
    POLICY_RENEWAL: 'Policy Renewal',
    CLAIM_UPDATE: 'Claim Update', 
    SUPPORT_RESPONSE: 'Support Response',
    SYSTEM_ALERT: 'System Alert',
    GENERAL: 'General'
  }
  return typeMap[type as keyof typeof typeMap] || type
}

onMounted(() => {
  document.addEventListener('keydown', handleEscapeKey)
  // Prevent body scroll when modal is open
  document.body.style.overflow = 'hidden'
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscapeKey)
  // Restore body scroll
  document.body.style.overflow = 'unset'
})
</script>
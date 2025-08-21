<template>
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-slate-500 bg-opacity-50">
      <div class="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 relative">
        <button
          @click="$emit('close')"
          class="absolute top-4 right-4 text-slate-500 hover:text-slate-700"
        >
          ✕
        </button>
        <h2 class="text-xl font-semibold text-slate-900 mb-2">
          {{ notification.message.split(': ')[0] || 'Notification' }}
        </h2>
        <p class="text-sm text-slate-500 mb-4">{{ formatDate(notification.createdAt) }}</p>
        <p class="text-slate-700 leading-relaxed">{{ notification.message }}</p>
        <p class="text-sm text-slate-600 mt-2">Type: {{ notification.type }}</p>
        <p class="text-sm text-slate-600">Status: {{ notification.status }}</p>
        <p v-if="notification.readAt" class="text-sm text-slate-600">
          Read at: {{ formatDate(notification.readAt) }}
        </p>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  interface Notification {
    id: number;
    userId: number;
    message: string;
    type: 'POLICY_RENEWAL' | 'CLAIM_UPDATE' | 'SUPPORT_RESPONSE' | 'SYSTEM_ALERT';
    status: 'UNREAD' | 'READ';
    createdAt: string;
    readAt: string | null;
  }
  
  defineProps<{ notification: Notification }>();
  
  defineEmits<{
    (e: 'close'): void;
  }>();
  
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('en-IN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };
  </script>
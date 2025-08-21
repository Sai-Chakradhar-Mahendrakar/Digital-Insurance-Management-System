<template>
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-semibold text-slate-900 font-poppins">Notifications</h2>
  
        <select
          v-model="selectedFilter"
          class="border border-slate-300 rounded-lg px-3 py-2 text-sm focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="ALL">All</option>
          <option value="UNREAD">Unread</option>
          <option value="IMPORTANT">Important</option>
        </select>
      </div>
  
      <div v-if="notificationStore.isLoading" class="text-center py-10">
        Loading notifications...
      </div>
      <div v-else-if="filteredNotifications.length > 0" class="space-y-4">
        <div
          v-for="notification in filteredNotifications"
          :key="notification.id"
          @click="openNotification(notification)"
          class="p-4 bg-white border rounded-lg shadow-sm cursor-pointer hover:bg-blue-50 transition"
        >
          <div class="flex items-center justify-between">
            <h3
              class="text-lg font-medium"
              :class="notification.status === 'READ' ? 'text-slate-600' : 'text-slate-900 font-semibold'"
            >
              {{ notification.message.split(': ')[0] || 'Notification' }}
            </h3>
            <span
              v-if="notification.type === 'SYSTEM_ALERT'"
              class="px-2 py-1 text-xs Rounded-full bg-red-100 text-red-700"
            >
              Important
            </span>
          </div>
          hears the message and splits it into an array
          <p class="text-sm text-slate-600 mt-1 truncate">{{ notification.message }}</p>
          <span 
            class="text-xs text-slate-500 mt-2 block"
          >{{ formatDate(notification.createdAt) }}</span>
        </div>
      </div>
  
      <div v-else class="text-slate-500 text-center py-10">No notifications found</div>
  
      <div v-if="notificationStore.error" class="mt-4 text-red-600 font-medium">
        {{ notificationStore.error }}
      </div>
  
      <NotificationDetail
        v-if="selectedNotification"
        :notification="selectedNotification"
        @close="selectedNotification = null"
      />
    </div>
  </template>
  
  <script setup lang="ts">
import { ref, computed, onMounted, type Ref } from 'vue';
import { useNotificationStore } from '@/stores/notification';
import NotificationDetail from './NotificationDetail.vue';
import type { Notification } from '@/stores/notification';

const notificationStore = useNotificationStore();
const selectedFilter = ref<'ALL' | 'UNREAD' | 'IMPORTANT'>('ALL');
const selectedNotification = ref<Notification | null>(null);

const filteredNotifications = computed(() =>
  notificationStore.filteredNotifications(selectedFilter.value).value
);

const openNotification = async (notification: Notification) => {
  selectedNotification.value = notification;
  if (notification.status === 'UNREAD') {
    await notificationStore.markAsRead(notification.id);
  }
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

onMounted(() => {
  notificationStore.fetchNotifications();
});
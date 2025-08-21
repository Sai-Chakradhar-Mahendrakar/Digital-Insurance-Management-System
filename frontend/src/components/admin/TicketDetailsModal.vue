<!-- src/components/admin/TicketDetailsModal.vue -->
<template>
  <div class="fixed inset-0 z-50 overflow-y-auto">
    <div
      class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0"
    >
      <!-- Background overlay -->
      <div
        class="fixed inset-0 transition-opacity bg-slate-500 bg-opacity-75"
        @click="$emit('close')"
      ></div>

      <!-- Modal -->
      <div
        class="inline-block w-full max-w-4xl p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-xl"
      >
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-2xl font-semibold text-slate-900 font-poppins">
              Support Ticket Details
            </h3>
            <span class="text-sm text-slate-500">Ticket #{{ ticket.id }}</span>
          </div>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Ticket Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Basic Info -->
          <div class="bg-slate-50 border border-slate-200 rounded-lg p-4">
            <h4 class="font-semibold text-slate-900 mb-3">Basic Information</h4>
            <div class="space-y-2 text-sm">
              <div class="flex justify-between">
                <span class="text-slate-500">Ticket ID:</span>
                <span class="font-semibold">#{{ ticket.id }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-500">User ID:</span>
                <span class="font-semibold">#{{ ticket.userId }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-500">Status:</span>
                <span
                  :class="getStatusStyle(ticket.status)"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                >
                  {{ ticket.status }}
                </span>
              </div>
              <div v-if="ticket.policyId" class="flex justify-between">
                <span class="text-slate-500">Policy ID:</span>
                <span class="font-semibold">#{{ ticket.policyId }}</span>
              </div>
              <div v-if="ticket.claimId" class="flex justify-between">
                <span class="text-slate-500">Claim ID:</span>
                <span class="font-semibold">#{{ ticket.claimId }}</span>
              </div>
            </div>
          </div>

          <!-- Timeline -->
          <div class="bg-slate-50 border border-slate-200 rounded-lg p-4">
            <h4 class="font-semibold text-slate-900 mb-3">Timeline</h4>
            <div class="space-y-2 text-sm">
              <div class="flex justify-between">
                <span class="text-slate-500">Created:</span>
                <span class="font-semibold">{{ formatDate(ticket.createdAt) }}</span>
              </div>
              <div v-if="ticket.resolvedAt" class="flex justify-between">
                <span class="text-slate-500">Resolved:</span>
                <span class="font-semibold">{{ formatDate(ticket.resolvedAt) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Subject and Description -->
        <div class="mt-6 bg-slate-50 border border-slate-200 rounded-lg p-4">
          <h4 class="font-semibold text-slate-900 mb-3">{{ ticket.subject }}</h4>
          <p class="text-slate-700 leading-relaxed">{{ ticket.description }}</p>
        </div>

        <!-- Response -->
        <div v-if="ticket.response" class="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-4">
          <h4 class="font-semibold text-blue-900 mb-3">Admin Response</h4>
          <p class="text-blue-800 leading-relaxed">{{ ticket.response }}</p>
        </div>

        <!-- Footer -->
        <div class="flex justify-end pt-6 border-t border-slate-200 mt-6">
          <AppButton variant="ghost" @click="$emit('close')"> Close </AppButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { X } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { AdminSupportTicket } from '@/stores/adminSupport'

interface Props {
  ticket: AdminSupportTicket
}

defineProps<Props>()

defineEmits<{
  (e: 'close'): void
}>()

const getStatusStyle = (status: string) => {
  const styles = {
    OPEN: 'bg-yellow-100 text-yellow-800',
    RESOLVED: 'bg-green-100 text-green-800',
    CLOSED: 'bg-slate-100 text-slate-800',
  }
  return styles[status as keyof typeof styles] || styles.OPEN
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

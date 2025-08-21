<template>
  <div class="min-h-screen bg-slate-50">
    <AppNavbar />

    <main class="max-w-7xl mx-auto px-4 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">Support Center</h1>
        <p class="text-slate-600 font-inter">Get help with your policies and claims</p>
      </div>

      <!-- Quick Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <LifeBuoy class="w-6 h-6 text-blue-600" />
            </div>
            <div class="ml-4">
              <p class="text-2xl font-bold text-slate-900">{{ totalTickets }}</p>
              <p class="text-sm text-slate-500">Total Tickets</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
              <Clock class="w-6 h-6 text-yellow-600" />
            </div>
            <div class="ml-4">
              <p class="text-2xl font-bold text-slate-900">{{ openTickets }}</p>
              <p class="text-sm text-slate-500">Open Tickets</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <CheckCircle class="w-6 h-6 text-green-600" />
            </div>
            <div class="ml-4">
              <p class="text-2xl font-bold text-slate-900">{{ resolvedTickets }}</p>
              <p class="text-sm text-slate-500">Resolved</p>
            </div>
          </div>
        </div>
      </div>

      <!-- New Ticket Button -->
      <div class="mb-8 flex justify-between items-center">
        <h2 class="text-xl font-semibold text-slate-900">Your Support Tickets</h2>
        <AppButton variant="primary" @click="showCreateModal = true">
          Create New Ticket
        </AppButton>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center py-12">
        <div class="flex flex-col items-center">
          <div
            class="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"
          ></div>
          <p class="text-slate-600">Loading your tickets...</p>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="tickets.length === 0" class="text-center py-12">
        <div
          class="w-24 h-24 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-6"
        >
          <LifeBuoy class="w-12 h-12 text-slate-400" />
        </div>
        <h3 class="text-xl font-semibold text-slate-900 mb-2">No Support Tickets</h3>
        <p class="text-slate-600 mb-6">You haven't created any support tickets yet.</p>
        <AppButton variant="primary" @click="showCreateModal = true">
          Create Your First Ticket
        </AppButton>
      </div>

      <!-- Tickets List -->
      <div v-else class="space-y-4">
        <div
          v-for="ticket in sortedTickets"
          :key="ticket.id"
          class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 hover:shadow-md transition-shadow duration-200"
        >
          <div class="flex items-start justify-between mb-4">
            <div class="flex-1">
              <div class="flex items-center space-x-3 mb-2">
                <h3 class="text-lg font-semibold text-slate-900">{{ ticket.subject }}</h3>
                <span
                  :class="getStatusStyle(ticket.status)"
                  class="px-3 py-1 rounded-full text-xs font-medium"
                >
                  <component :is="getStatusIcon(ticket.status)" class="w-3 h-3 mr-1" />
                  {{ ticket.status }}
                </span>
              </div>
              <p class="text-sm text-slate-500">Ticket #{{ ticket.id }}</p>
            </div>
            <div class="text-right text-sm text-slate-500">
              <p>{{ formatDate(ticket.createdAt) }}</p>
              <p v-if="ticket.resolvedAt">Resolved: {{ formatDate(ticket.resolvedAt) }}</p>
            </div>
          </div>

          <div class="mb-4">
            <p class="text-slate-700 leading-relaxed">{{ ticket.description }}</p>
          </div>

          <div v-if="ticket.response" class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
            <div class="flex items-center mb-2">
              <MessageSquare class="w-4 h-4 text-blue-600 mr-2" />
              <span class="text-sm font-semibold text-blue-900">Support Response</span>
            </div>
            <p class="text-blue-800 whitespace-pre-wrap">{{ ticket.response }}</p>
          </div>

          <div
            class="flex items-center justify-between text-xs text-slate-500 pt-4 border-t border-slate-200"
          >
            <div class="flex items-center space-x-4">
              <span v-if="ticket.policyId">Policy ID: #{{ ticket.policyId }}</span>
              <span v-if="ticket.claimId">Claim ID: #{{ ticket.claimId }}</span>
            </div>
            <div class="flex items-center space-x-2">
              <Clock class="w-3 h-3" />
              <span>{{ getTimeAgo(ticket.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Create Ticket Modal -->
    <CreateSupportTicketModal
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @submitted="handleTicketCreated"
    />

    <!-- Toast Container -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useSupportTicketsStore } from '@/stores/supportTickets'
import { useToast } from '@/composables/useToast'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppButton from '@/components/common/AppButton.vue'
import CreateSupportTicketModal from '@/components/support/CreateSupportTicketModal.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import {
  LifeBuoy,
  Clock,
  CheckCircle,
  Plus,
  MessageSquare,
  AlertCircle,
  XCircle,
} from 'lucide-vue-next'
import type { SupportTicket } from '@/stores/supportTickets'

const supportTicketsStore = useSupportTicketsStore()
const toast = useToast()

const showCreateModal = ref(false)

const tickets = computed(() => supportTicketsStore.tickets)
const isLoading = computed(() => supportTicketsStore.isLoading)

const totalTickets = computed(() => tickets.value.length)
const openTickets = computed(() => tickets.value.filter((t) => t.status === 'OPEN').length)
const resolvedTickets = computed(() => tickets.value.filter((t) => t.status === 'RESOLVED').length)

const sortedTickets = computed(() => {
  return [...tickets.value].sort((a, b) => {
    // Sort by status (OPEN first) then by creation date (newest first)
    if (a.status !== b.status) {
      if (a.status === 'OPEN') return -1
      if (b.status === 'OPEN') return 1
    }
    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  })
})

const handleTicketCreated = (ticket: SupportTicket) => {
  showCreateModal.value = false
  toast.success(
    'Support Ticket Created',
    `Your ticket "${ticket.subject}" has been submitted successfully.`,
  )
}

const getStatusStyle = (status: string) => {
  const styles = {
    OPEN: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
    RESOLVED: 'bg-green-100 text-green-800 border border-green-200',
    CLOSED: 'bg-slate-100 text-slate-800 border border-slate-200',
  }
  return styles[status as keyof typeof styles] || styles.OPEN
}

const getStatusIcon = (status: string) => {
  const icons = {
    OPEN: Clock,
    RESOLVED: CheckCircle,
    CLOSED: XCircle,
  }
  return icons[status as keyof typeof icons] || Clock
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const getTimeAgo = (dateString: string): string => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = now.getTime() - date.getTime()
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  const diffHours = Math.floor(diffTime / (1000 * 60 * 60))
  const diffMinutes = Math.floor(diffTime / (1000 * 60))

  if (diffDays > 0) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`
  if (diffHours > 0) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`
  if (diffMinutes > 0) return `${diffMinutes} minute${diffMinutes > 1 ? 's' : ''} ago`
  return 'Just now'
}

onMounted(async () => {
  try {
    await supportTicketsStore.fetchUserTickets()
  } catch (error) {
    console.error('Failed to load support tickets:', error)
  }
})
</script>

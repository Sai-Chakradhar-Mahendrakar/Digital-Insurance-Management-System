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
            <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">Support Management</h1>
            <p class="text-slate-600 font-inter">Manage and respond to user support tickets</p>
          </div>

          <!-- Stats Cards -->
          <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
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
                  <p class="text-sm text-slate-500">Open</p>
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

            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
              <div class="flex items-center">
                <div class="w-12 h-12 bg-slate-100 rounded-lg flex items-center justify-center">
                  <XCircle class="w-6 h-6 text-slate-600" />
                </div>
                <div class="ml-4">
                  <p class="text-2xl font-bold text-slate-900">{{ closedTickets }}</p>
                  <p class="text-sm text-slate-500">Closed</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Loading State -->
          <div v-if="isLoading" class="flex items-center justify-center py-12">
            <div class="flex flex-col items-center">
              <div
                class="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"
              ></div>
              <p class="text-slate-600">Loading support tickets...</p>
            </div>
          </div>

          <!-- Error State -->
          <div v-else-if="errorMessage" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
            <div class="flex items-center">
              <AlertCircle class="w-5 h-5 text-red-600 mr-3" />
              <p class="text-red-800 text-sm font-medium">{{ errorMessage }}</p>
              <button @click="refreshTickets" class="ml-auto text-red-600 hover:text-red-800">
                <RefreshCw class="w-4 h-4" />
              </button>
            </div>
          </div>

          <!-- Support Tickets Table -->
          <div v-else class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div class="p-6 border-b border-slate-200">
              <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-slate-900">Support Tickets</h3>
                <AppButton variant="ghost" @click="refreshTickets" :disabled="isLoading">
                  <RefreshCw :class="{ 'animate-spin': isLoading }" class="w-4 h-4 mr-2" />
                  Refresh
                </AppButton>
              </div>
            </div>

            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-slate-200">
                <thead class="bg-slate-50">
                  <tr>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Ticket ID
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Subject
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      User ID
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Status
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Created
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-200">
                  <tr v-for="ticket in sortedTickets" :key="ticket.id" class="hover:bg-slate-50">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-slate-900">
                      #{{ ticket.id }}
                    </td>
                    <td class="px-6 py-4">
                      <div class="text-sm font-medium text-slate-900">{{ ticket.subject }}</div>
                      <div class="text-sm text-slate-500 truncate max-w-xs">
                        {{ ticket.description }}
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      User #{{ ticket.userId }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        :class="getStatusStyle(ticket.status)"
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      >
                        <component :is="getStatusIcon(ticket.status)" class="w-3 h-3 mr-1" />
                        {{ ticket.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ formatDate(ticket.createdAt) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex space-x-2">
                        <AppButton variant="ghost" size="small" @click="viewTicketDetails(ticket)">
                          <Eye class="w-4 h-4 mr-1" />
                          View
                        </AppButton>
                        <AppButton
                          v-if="ticket.status === 'OPEN'"
                          variant="primary"
                          size="small"
                          @click="openRespondModal(ticket)"
                        >
                          <MessageSquare class="w-4 h-4 mr-1" />
                          Respond
                        </AppButton>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Respond to Ticket Modal -->
    <RespondToTicketModal
      v-if="selectedTicket"
      :ticket="selectedTicket"
      @close="selectedTicket = null"
      @responded="handleTicketResponded"
    />

    <!-- Ticket Details Modal -->
    <TicketDetailsModal
      v-if="selectedTicketForDetails"
      :ticket="selectedTicketForDetails"
      @close="selectedTicketForDetails = null"
    />

    <!-- Toast Container -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAdminSupportStore } from '@/stores/adminSupport'
import { useToast } from '@/composables/useToast'
import AdminNavbar from '@/components/admin/AdminNavbar.vue'
import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AppButton from '@/components/common/AppButton.vue'
import RespondToTicketModal from '@/components/admin/RespondToTicketModal.vue'
import TicketDetailsModal from '@/components/admin/TicketDetailsModal.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import {
  LifeBuoy,
  Clock,
  CheckCircle,
  XCircle,
  RefreshCw,
  AlertCircle,
  Eye,
  MessageSquare,
} from 'lucide-vue-next'
import type { AdminSupportTicket } from '@/stores/adminSupport'

const adminSupportStore = useAdminSupportStore()
const toast = useToast()

const selectedTicket = ref<AdminSupportTicket | null>(null)
const selectedTicketForDetails = ref<AdminSupportTicket | null>(null)

const tickets = computed(() => adminSupportStore.tickets)
const isLoading = computed(() => adminSupportStore.isLoading)
const errorMessage = computed(() => adminSupportStore.error)

const totalTickets = computed(() => tickets.value.length)
const openTickets = computed(() => tickets.value.filter((t) => t.status === 'OPEN').length)
const resolvedTickets = computed(() => tickets.value.filter((t) => t.status === 'RESOLVED').length)
const closedTickets = computed(() => tickets.value.filter((t) => t.status === 'CLOSED').length)

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

const openRespondModal = (ticket: AdminSupportTicket) => {
  selectedTicket.value = ticket
}

const viewTicketDetails = (ticket: AdminSupportTicket) => {
  selectedTicketForDetails.value = ticket
}

const handleTicketResponded = (updatedTicket: AdminSupportTicket) => {
  selectedTicket.value = null
  toast.success(
    'Response Sent Successfully',
    `Your response to ticket #${updatedTicket.id} has been sent to the user.`,
  )
}

const refreshTickets = async () => {
  try {
    await adminSupportStore.fetchAllTickets()
    toast.success('Tickets Refreshed', `Loaded ${tickets.value.length} tickets.`)
  } catch (error) {
    toast.error('Refresh Failed', 'Failed to refresh tickets.')
  }
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

onMounted(async () => {
  try {
    await adminSupportStore.fetchAllTickets()
  } catch (error) {
    console.error('Failed to load support tickets on mount:', error)
  }
})
</script>

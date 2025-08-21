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
        class="inline-block w-full max-w-3xl p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-xl"
      >
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center space-x-3">
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
              <MessageSquare class="w-6 h-6 text-blue-700" />
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-slate-900 font-poppins">
                Respond to Support Ticket
              </h3>
              <span class="text-sm text-slate-500">Ticket #{{ ticket.id }}</span>
            </div>
          </div>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Ticket Information -->
        <div class="bg-slate-50 border border-slate-200 rounded-lg p-4 mb-6">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm mb-4">
            <div>
              <span class="text-slate-500">User ID:</span>
              <span class="font-semibold text-slate-800 ml-2">#{{ ticket.userId }}</span>
            </div>
            <div>
              <span class="text-slate-500">Status:</span>
              <span
                :class="getStatusStyle(ticket.status)"
                class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ml-2"
              >
                {{ ticket.status }}
              </span>
            </div>
            <div>
              <span class="text-slate-500">Created:</span>
              <span class="font-semibold text-slate-800 ml-2">{{
                formatDate(ticket.createdAt)
              }}</span>
            </div>
            <div v-if="ticket.policyId || ticket.claimId">
              <span class="text-slate-500">References:</span>
              <span class="font-semibold text-slate-800 ml-2">
                <span v-if="ticket.policyId">Policy #{{ ticket.policyId }}</span>
                <span v-if="ticket.policyId && ticket.claimId">, </span>
                <span v-if="ticket.claimId">Claim #{{ ticket.claimId }}</span>
              </span>
            </div>
          </div>
          <div>
            <h4 class="font-semibold text-slate-900 mb-2">{{ ticket.subject }}</h4>
            <p class="text-slate-700">{{ ticket.description }}</p>
          </div>
        </div>

        <!-- Current Response (if exists) -->
        <div v-if="ticket.response" class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
          <h4 class="font-semibold text-blue-900 mb-2">Current Response</h4>
          <p class="text-blue-800">{{ ticket.response }}</p>
          <p class="text-xs text-blue-600 mt-2">Resolved: {{ formatDate(ticket.resolvedAt!) }}</p>
        </div>

        <!-- Response Form -->
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Response Text -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">
              {{ ticket.response ? 'Update Response' : 'Response' }} *
            </label>
            <textarea
              v-model="form.response"
              required
              rows="6"
              maxlength="1000"
              class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100 resize-none transition-all duration-200"
              placeholder="Type your response to help the user..."
            ></textarea>
            <p class="text-xs text-slate-500 mt-1">{{ form.response.length }}/1000 characters</p>
          </div>

          <!-- Status Selection -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Ticket Status</label>
            <select
              v-model="form.status"
              class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all duration-200"
            >
              <option value="RESOLVED">Resolved (Recommended)</option>
              <option value="CLOSED">Closed</option>
            </select>
            <p class="text-xs text-slate-500 mt-1">
              Select "Resolved" if the issue is fixed, or "Closed" if no further action is needed.
            </p>
          </div>

          <!-- Error Display -->
          <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-lg">
            <div class="flex items-center">
              <AlertCircle class="w-5 h-5 text-red-600 mr-3" />
              <p class="text-red-800 text-sm font-medium">{{ errorMessage }}</p>
            </div>
          </div>

          <!-- Footer -->
          <div class="flex justify-end space-x-3 pt-6 border-t border-slate-200">
            <AppButton type="button" variant="ghost" @click="$emit('close')"> Cancel </AppButton>
            <AppButton
              type="submit"
              variant="primary"
              :disabled="isLoading || !form.response.trim()"
              class="flex items-center space-x-2"
            >
              <span v-if="!isLoading">Send Response</span>
              <span v-else class="flex items-center space-x-2">
                <div
                  class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
                ></div>
                <span>Sending...</span>
              </span>
            </AppButton>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useAdminSupportStore } from '@/stores/adminSupport'
import { MessageSquare, X, AlertCircle } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { AdminSupportTicket } from '@/stores/adminSupport'

interface Props {
  ticket: AdminSupportTicket
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'responded', ticket: AdminSupportTicket): void
}>()

const adminSupportStore = useAdminSupportStore()

const isLoading = ref(false)
const errorMessage = ref('')

const form = reactive({
  response: props.ticket.response || '',
  status: 'RESOLVED' as 'RESOLVED' | 'CLOSED',
})

const handleSubmit = async () => {
  try {
    errorMessage.value = ''
    isLoading.value = true

    const updateData = {
      response: form.response.trim(),
      status: form.status,
    }

    const updatedTicket = await adminSupportStore.updateTicket(props.ticket.id, updateData)
    emit('responded', updatedTicket)
  } catch (error: unknown) {
    if (typeof error === 'object' && error !== null) {
      const err = error as any
      errorMessage.value =
        err.response?.data?.errorMessage ||
        err.message ||
        'Failed to send response'
    }
  } finally {
    isLoading.value = false
  }
}

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

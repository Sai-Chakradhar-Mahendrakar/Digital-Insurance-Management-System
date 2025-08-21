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
        class="inline-block w-full max-w-2xl p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-xl"
      >
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center space-x-3">
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
              <LifeBuoy class="w-6 h-6 text-blue-700" />
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-slate-900 font-poppins">
                Create Support Ticket
              </h3>
              <span class="text-sm text-slate-500">We're here to help you</span>
            </div>
          </div>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
            data-testid="close-button"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Subject -->
          <div>
            <div class="flex items-center justify-between mb-2">
              <label class="block text-sm font-medium text-slate-700">Subject *</label>
              <!-- Tooltip for Subject Requirements -->
              <div class="relative group">
                <Info class="w-4 h-4 text-slate-400 cursor-help" />
                <div class="absolute right-0 top-6 z-10 invisible group-hover:visible">
                  <div class="bg-slate-800 text-white text-xs rounded-lg py-2 px-3 whitespace-nowrap">
                    Minimum 10 characters required
                    <div class="absolute -top-1 right-3 w-2 h-2 bg-slate-800 rotate-45"></div>
                  </div>
                </div>
              </div>
            </div>
            <input
              v-model="form.subject"
              type="text"
              required
              minlength="10"
              maxlength="200"
              :class="[
                'w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-100 transition-all duration-200',
                fieldErrors.subject 
                  ? 'border-red-300 focus:border-red-500 bg-red-50' 
                  : 'border-slate-200 focus:border-blue-500'
              ]"
              placeholder="Brief description of your issue (Min 10 characters)..."
            />
            <!-- Character Count and Validation Message -->
            <div class="flex justify-between items-center mt-1">
              <div class="text-xs">
                <span v-if="fieldErrors.subject" class="text-red-600 font-medium">
                  {{ fieldErrors.subject }}
                </span>
                <span v-else-if="form.subject.length < 10 && form.subject.length > 0" class="text-amber-600">
                  {{ 10 - form.subject.length }} more characters needed
                </span>
              </div>
              <span 
                :class="[
                  'text-xs',
                  form.subject.length < 10 ? 'text-red-500' : 
                  form.subject.length > 180 ? 'text-amber-500' : 'text-slate-500'
                ]"
              >
                {{ form.subject.length }}/80
              </span>
            </div>
          </div>

          <!-- Description -->
          <div>
            <div class="flex items-center justify-between mb-2">
              <label class="block text-sm font-medium text-slate-700">Description *</label>
              <!-- Tooltip for Description Requirements -->
              <div class="relative group">
                <Info class="w-4 h-4 text-slate-400 cursor-help" />
                <div class="absolute right-0 top-6 z-10 invisible group-hover:visible">
                  <div class="bg-slate-800 text-white text-xs rounded-lg py-2 px-3 whitespace-nowrap">
                    Minimum 10 characters required
                    <div class="absolute -top-1 right-3 w-2 h-2 bg-slate-800 rotate-45"></div>
                  </div>
                </div>
              </div>
            </div>
            <textarea
              v-model="form.description"
              required
              rows="6"
              minlength="10"
              maxlength="1000"
              :class="[
                'w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-100 resize-none transition-all duration-200',
                fieldErrors.description 
                  ? 'border-red-300 focus:border-red-500 bg-red-50' 
                  : 'border-slate-200 focus:border-blue-500'
              ]"
              placeholder="Please provide detailed information about your issue or question (min 10 characters)..."
            ></textarea>
            <!-- Character Count and Validation Message -->
            <div class="flex justify-between items-center mt-1">
              <div class="text-xs">
                <span v-if="fieldErrors.description" class="text-red-600 font-medium">
                  {{ fieldErrors.description }}
                </span>
                <span v-else-if="form.description.length < 10 && form.description.length > 0" class="text-amber-600">
                  {{ 10 - form.description.length }} more characters needed
                </span>
              </div>
              <span 
                :class="[
                  'text-xs',
                  form.description.length < 10 ? 'text-red-500' : 
                  form.description.length > 900 ? 'text-amber-500' : 'text-slate-500'
                ]"
              >
                {{ form.description.length }}/1000
              </span>
            </div>
          </div>

          <!-- Optional References -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-2"
                >Policy ID (Optional)</label
              >
              <input
                v-model.number="form.policyId"
                type="number"
                min="1"
                class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all duration-200"
                placeholder="Related policy ID"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700 mb-2"
                >Claim ID (Optional)</label
              >
              <input
                v-model.number="form.claimId"
                type="number"
                min="1"
                class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all duration-200"
                placeholder="Related claim ID"
              />
            </div>
          </div>

          <!-- Help Text -->
          <div class="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <div class="flex items-start space-x-3">
              <Info class="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
              <div>
                <h4 class="font-medium text-blue-900 mb-2">Tips for Better Support</h4>
                <ul class="text-sm text-blue-800 space-y-1">
                  <li>• Be specific about the issue you're experiencing</li>
                  <li>• Include relevant policy or claim IDs if applicable</li>
                  <li>• Mention any error messages you've encountered</li>
                  <li>• Our support team typically responds within 24 hours</li>
                </ul>
              </div>
            </div>
          </div>

          <!-- Backend Field Errors Display -->
          <div v-if="hasFieldErrors" class="space-y-2">
            <div 
              v-for="(error, field) in fieldErrors" 
              :key="field"
              class="p-3 bg-red-50 border border-red-200 rounded-lg"
            >
              <div class="flex items-center">
                <AlertCircle class="w-4 h-4 text-red-600 mr-2 flex-shrink-0" />
                <div>
                  <p class="text-red-800 text-sm font-medium capitalize">{{ field }} Error</p>
                  <p class="text-red-700 text-sm">{{ error }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- General Error Display -->
          <div v-if="errorMessage && !hasFieldErrors" class="p-4 bg-red-50 border border-red-200 rounded-lg">
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
              :disabled="isLoading || !isFormValid"
              class="flex items-center space-x-2"
            >
              <span v-if="!isLoading">Create Ticket</span>
              <span v-else class="flex items-center space-x-2">
                <div
                  class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
                ></div>
                <span>Creating...</span>
              </span>
            </AppButton>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useSupportTicketsStore } from '@/stores/supportTickets'
import { LifeBuoy, X, Info, AlertCircle } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { SupportTicket } from '@/stores/supportTickets'

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submitted', ticket: SupportTicket): void
}>()

const supportTicketsStore = useSupportTicketsStore()

const isLoading = ref(false)
const errorMessage = ref('')
const fieldErrors = ref<Record<string, string>>({})

const form = reactive({
  subject: '',
  description: '',
  policyId: null as number | null,
  claimId: null as number | null,
})

const hasFieldErrors = computed(() => Object.keys(fieldErrors.value).length > 0)

const isFormValid = computed(() => {
  return (
    form.subject.trim().length >= 10 &&
    form.description.trim().length >= 10 &&
    form.subject.length <= 200 &&
    form.description.length <= 1000
  )
})

// Parse backend validation errors
const parseBackendErrors = (error: any) => {
  fieldErrors.value = {}
  
  if (error.response?.data?.additionalInfo) {
    const violations = error.response.data.additionalInfo
    
    for (const [key, violation] of Object.entries(violations)) {
      if (typeof violation === 'string') {
        // Parse violation messages like "subject => size must be between 10 and 2147483647"
        const match = violation.match(/(\w+)\s*=>\s*(.+)/)
        if (match) {
          const [, fieldName, message] = match
          
          // Convert technical messages to user-friendly ones
          let friendlyMessage = message
          if (message.includes('size must be between')) {
            const sizeMatch = message.match(/size must be between (\d+) and (\d+)/)
            if (sizeMatch) {
              const [, min] = sizeMatch
              if (fieldName === 'subject') {
                friendlyMessage = `Subject must be at least ${min} characters long`
              } else if (fieldName === 'description') {
                friendlyMessage = `Description must be at least ${min} characters long`
              }
            }
          }
          
          fieldErrors.value[fieldName] = friendlyMessage
        }
      }
    }
  }
}

const handleSubmit = async () => {
  try {
    errorMessage.value = ''
    fieldErrors.value = {}
    isLoading.value = true

    const ticketData = {
      subject: form.subject.trim(),
      description: form.description.trim(),
      ...(form.policyId && { policyId: form.policyId }),
      ...(form.claimId && { claimId: form.claimId }),
    }

    const newTicket = await supportTicketsStore.createSupportTicket(ticketData)
    emit('submitted', newTicket)
  } catch (error: unknown) {
    if (typeof error === 'object' && error !== null) {
      const err = error as any
      errorMessage.value =
        err.response?.data?.errorMessage ||
        err.message ||
        'Failed to create support ticket'
    }
  } finally {
    isLoading.value = false
  }
}
</script>
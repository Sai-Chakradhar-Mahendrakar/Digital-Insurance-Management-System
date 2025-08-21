<!-- src/components/admin/UpdateClaimStatusModal.vue -->
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
            <div :class="headerStyle" class="w-12 h-12 rounded-xl flex items-center justify-center">
              <component :is="headerIcon" class="w-6 h-6" />
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-slate-900 font-poppins">
                {{ action === 'APPROVE' ? 'Approve Claim' : 'Reject Claim' }}
              </h3>
              <span class="text-sm text-slate-500">Claim #{{ claim.id }}</span>
            </div>
          </div>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Claim Info -->
        <div class="bg-slate-50 border border-slate-200 rounded-lg p-4 mb-6">
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span class="text-slate-500">Policy Name:</span>
              <span class="font-semibold text-slate-800 ml-2">{{ claim.policyName }}</span>
            </div>
            <div>
              <span class="text-slate-500">Policy Type:</span>
              <span class="font-semibold text-slate-800 ml-2">{{ claim.policyType }}</span>
            </div>
            <div>
              <span class="text-slate-500">Claim Amount:</span>
              <span class="font-semibold text-slate-800 ml-2">{{
                formatINR(claim.claimAmount)
              }}</span>
            </div>
            <div>
              <span class="text-slate-500">Claim Date:</span>
              <span class="font-semibold text-slate-800 ml-2">{{
                formatDate(claim.claimDate)
              }}</span>
            </div>
          </div>
          <div class="mt-3">
            <span class="text-slate-500">Reason:</span>
            <p class="text-slate-800 mt-1">{{ claim.reason }}</p>
          </div>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Status (Read-only) -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Status</label>
            <div class="w-full px-4 py-3 border border-slate-200 rounded-lg bg-slate-50">
              <span
                :class="action === 'APPROVE' ? 'text-green-700' : 'text-red-700'"
                class="font-semibold"
              >
                {{ action === 'APPROVE' ? 'APPROVED' : 'REJECTED' }}
              </span>
            </div>
          </div>

          <!-- Reviewer Comment -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">
              Reviewer Comment *
              <span class="text-slate-500 font-normal">(This will be visible to the user)</span>
            </label>
            <textarea
              v-model="form.reviewerComment"
              required
              rows="4"
              maxlength="500"
              class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100 resize-none transition-all duration-200"
              :placeholder="commentPlaceholder"
            ></textarea>
            <p class="text-xs text-slate-500 mt-1">
              {{ form.reviewerComment.length }}/500 characters
            </p>
          </div>

          <!-- Important Information -->
          <div :class="infoBoxStyle" class="border rounded-lg p-4">
            <div class="flex items-start space-x-3">
              <component :is="infoIcon" class="w-5 h-5 mt-0.5 flex-shrink-0" />
              <div>
                <h4 :class="infoTextStyle" class="font-medium mb-2">Important Information</h4>
                <ul :class="infoTextStyle" class="text-sm space-y-1">
                  <li v-if="action === 'APPROVE'">
                    • This claim will be marked as approved and processed for payment
                  </li>
                  <li v-else>
                    • This claim will be marked as rejected and the user will be notified
                  </li>
                  <li>• Your comment will be visible to the policyholder</li>
                  <li>• This action cannot be undone</li>
                  <li>• The user will receive an email notification about this decision</li>
                </ul>
              </div>
            </div>
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
              :variant="action === 'APPROVE' ? 'primary' : 'ghost'"
              :disabled="isLoading || !form.reviewerComment.trim()"
              class="flex items-center space-x-2"
            >
              <span v-if="!isLoading">
                {{ action === 'APPROVE' ? 'Approve Claim' : 'Reject Claim' }}
              </span>
              <span v-else class="flex items-center space-x-2">
                <div
                  class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
                ></div>
                <span>Processing...</span>
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
import { useAdminClaimsStore } from '@/stores/adminClaims'
import { CheckCircle, XCircle, X, AlertTriangle, AlertCircle, Info } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { AdminClaim } from '@/stores/adminClaims'

interface Props {
  claim: AdminClaim
  action: 'APPROVE' | 'REJECT'
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'updated', claim: AdminClaim): void
}>()

const adminClaimsStore = useAdminClaimsStore()

const isLoading = ref(false)
const errorMessage = ref('')

const form = reactive({
  reviewerComment: '',
})

const headerStyle = computed(() => (props.action === 'APPROVE' ? 'bg-green-100' : 'bg-red-100'))

const headerIcon = computed(() => (props.action === 'APPROVE' ? CheckCircle : XCircle))

const infoBoxStyle = computed(() =>
  props.action === 'APPROVE' ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200',
)

const infoIcon = computed(() => (props.action === 'APPROVE' ? Info : AlertTriangle))

const infoTextStyle = computed(() =>
  props.action === 'APPROVE' ? 'text-green-900' : 'text-red-900',
)

const commentPlaceholder = computed(() =>
  props.action === 'APPROVE'
    ? 'Enter approval comments (e.g., "Claim verified and approved for payment. All documents are in order.")'
    : 'Enter rejection reason (e.g., "Insufficient documentation provided. Please resubmit with medical receipts.")',
)

const handleSubmit = async () => {
  try {
    errorMessage.value = ''
    isLoading.value = true

    const updateData = {
      status: props.action === 'APPROVE' ? ('APPROVED' as const) : ('REJECTED' as const),
      reviewerComment: form.reviewerComment.trim(),
    }

    const updatedClaim = await adminClaimsStore.updateClaimStatus(props.claim.id, updateData)
    emit('updated', updatedClaim)
  } catch (error: unknown) {
    if (typeof error === 'object' && error !== null) {
      const err = error as any
      errorMessage.value =
        err.response?.data?.errorMessage ||
        err.message ||
        'Failed to update claim'
    }
  } finally {
    isLoading.value = false
  }
}

const formatINR = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount)
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}
</script>

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
            <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
              <FileText class="w-6 h-6 text-green-700" />
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-slate-900 font-poppins">
                Submit Insurance Claim
              </h3>
              <span class="text-sm text-slate-500">{{ userPolicy.policyName }}</span>
            </div>
          </div>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Policy Info Banner -->
        <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
          <div class="flex items-start space-x-3">
            <Shield class="w-5 h-5 text-blue-600 mt-0.5" />
            <div class="flex-1">
              <h4 class="font-medium text-blue-900">{{ userPolicy.policyName }}</h4>
              <p class="text-sm text-blue-700 mt-1">{{ userPolicy.policyType }} Insurance</p>
              <div class="grid grid-cols-2 gap-4 mt-3 text-xs">
                <div>
                  <span class="text-blue-600">Coverage Amount:</span>
                  <span class="font-semibold text-blue-800 ml-1">{{
                    formatINR(userPolicy.coverageAmount)
                  }}</span>
                </div>
                <div>
                  <span class="text-blue-600">Available:</span>
                  <span class="font-semibold text-blue-800 ml-1">{{
                    formatINR(availableCoverage)
                  }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Claim Date -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Claim Date *</label>
            <input
              v-model="form.claimDate"
              type="date"
              required
              :max="maxDate"
              class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-green-500 focus:ring-2 focus:ring-green-100 transition-all duration-200"
            />
          </div>

          <!-- Claim Amount -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Claim Amount *</label>
            <div class="relative">
              <span class="absolute left-3 top-3 text-slate-500">₹</span>
              <input
                v-model.number="form.claimAmount"
                type="number"
                required
                min="1"
                :max="availableCoverage"
                step="0.01"
                class="w-full pl-8 pr-4 py-3 border border-slate-200 rounded-lg focus:border-green-500 focus:ring-2 focus:ring-green-100 transition-all duration-200"
                placeholder="0.00"
              />
            </div>
            <p class="text-xs text-slate-500 mt-1">
              Maximum claimable: {{ formatINR(availableCoverage) }}
            </p>
            <div v-if="form.claimAmount > availableCoverage" class="text-xs text-red-600 mt-1">
              Claim amount exceeds available coverage
            </div>
          </div>

          <!-- Reason -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Reason for Claim *</label>
            <textarea
              v-model="form.reason"
              required
              rows="4"
              maxlength="500"
              class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-green-500 focus:ring-2 focus:ring-green-100 resize-none transition-all duration-200"
              placeholder="Please provide detailed reason for your claim (e.g., Hospitalization due to fever, Car accident on highway, etc.)"
            ></textarea>
            <p class="text-xs text-slate-500 mt-1">{{ form.reason.length }}/500 characters</p>
          </div>

          <!-- Important Information -->
          <div class="bg-amber-50 border border-amber-200 rounded-lg p-4">
            <div class="flex items-start space-x-3">
              <AlertTriangle class="w-5 h-5 text-amber-600 mt-0.5 flex-shrink-0" />
              <div>
                <h4 class="font-medium text-amber-900 mb-2">Important Information</h4>
                <ul class="text-sm text-amber-800 space-y-1">
                  <li>• All claims are subject to policy terms and conditions</li>
                  <li>• Supporting documents may be required for claim processing</li>
                  <li>• Claims are typically processed within 7-10 business days</li>
                  <li>• False claims may result in policy cancellation</li>
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
              variant="primary"
              :disabled="isLoading || !isFormValid"
              class="flex items-center space-x-2"
            >
              <span v-if="!isLoading">Submit Claim</span>
              <span v-else class="flex items-center space-x-2">
                <div
                  class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
                ></div>
                <span>Submitting...</span>
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
import { useClaimsStore } from '@/stores/claims'
import { FileText, X, Shield, AlertTriangle, AlertCircle } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { UserPolicy } from '@/stores/userPolicy'
import type { Claim } from '@/stores/claims'

interface Props {
  userPolicy: UserPolicy
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submitted', claim: Claim): void
}>()

const claimsStore = useClaimsStore()

const isLoading = ref(false)
const errorMessage = ref('')

// Get today's date for max date validation
const today = new Date()
const maxDate = today.toISOString().split('T')[0]

const form = reactive({
  claimDate: maxDate,
  claimAmount: 0,
  reason: '',
})

const availableCoverage = computed(() => {
  const claimed = props.userPolicy.totalAmountClaimed || 0
  return Math.max(0, props.userPolicy.coverageAmount - claimed)
})

const isFormValid = computed(() => {
  return (
    form.claimDate &&
    form.claimAmount > 0 &&
    form.claimAmount <= availableCoverage.value &&
    form.reason.trim().length > 0
  )
})

const handleSubmit = async () => {
  try {
    errorMessage.value = ''
    isLoading.value = true

    const claimData = {
      policyId: props.userPolicy.policyId,
      claimDate: form.claimDate,
      claimAmount: form.claimAmount,
      reason: form.reason.trim(),
    }

    const newClaim = await claimsStore.submitClaim(claimData)
    emit('submitted', newClaim)
  } catch (error: unknown) {
    if (typeof error === 'object' && error !== null) {
      const err = error as any
      errorMessage.value =
        err.response?.data?.errorMessage ||
        err.message ||
        'Failed to submit claim'
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
</script>

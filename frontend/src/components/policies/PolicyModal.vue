<!-- src/components/policies/PolicyModal.vue -->
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
              <Shield class="w-6 h-6 text-blue-700" />
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-slate-900 font-poppins">{{ policy.name }}</h3>
              <span
                class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-emerald-100 text-emerald-800"
              >
                {{ policy.type }} Insurance
              </span>
            </div>
          </div>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Policy Details -->
        <div class="space-y-6">
          <!-- Description -->
          <div>
            <h4 class="font-medium text-slate-900 mb-2 font-poppins">Description</h4>
            <p class="text-slate-600 font-inter">{{ policy.description }}</p>
          </div>

          <!-- Financial Details -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-4">
              <h4 class="font-medium text-slate-900 font-poppins">Financial Details</h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-slate-500">Premium Amount</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy.premiumAmount)
                  }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Coverage Amount</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy.coverageAmount)
                  }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Renewal Premium</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy.renewalPremiumRate)
                  }}</span>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <h4 class="font-medium text-slate-900 font-poppins">Policy Information</h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-slate-500">Policy ID</span>
                  <span class="font-semibold text-slate-900">#{{ policy.id }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Duration</span>
                  <span class="font-semibold text-slate-900"
                    >{{ policy.durationMonths }} months</span
                  >
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Created Date</span>
                  <span class="font-semibold text-slate-900">{{
                    formatDate(policy.createdAt)
                  }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Coverage Summary -->
          <div class="bg-blue-50 rounded-lg p-4">
            <h4 class="font-medium text-blue-900 mb-2 font-poppins">Coverage Summary</h4>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
              <div class="text-center">
                <div class="font-semibold text-blue-900">
                  {{ formatINR(policy.coverageAmount) }}
                </div>
                <div class="text-blue-600">Total Coverage</div>
              </div>
              <div class="text-center">
                <div class="font-semibold text-blue-900">{{ formatINR(policy.premiumAmount) }}</div>
                <div class="text-blue-600">Annual Premium</div>
              </div>
              <div class="text-center">
                <div class="font-semibold text-blue-900">
                  {{ Math.round((policy.coverageAmount / policy.premiumAmount) * 100) / 100 }}x
                </div>
                <div class="text-blue-600">Coverage Ratio</div>
              </div>
            </div>
          </div>

          <!-- Tax Information (India-specific) -->
          <div class="bg-green-50 border border-green-200 rounded-lg p-4">
            <h4 class="font-medium text-green-900 mb-2 font-poppins">Tax Benefits & Information</h4>
            <div class="text-sm text-green-700 space-y-1">
              <p>• Premium payments are eligible for tax deduction under Section 80C/80D</p>
              <p>• Additional benefits available for senior citizens and critical illness cover</p>
              <p>• GST: 18% applicable on premium amount as per Indian tax regulations</p>
            </div>
          </div>
        </div>

        <!-- Error Display -->
        <div v-if="errorMessage" class="mt-4 p-4 bg-red-50 border border-red-200 rounded-lg">
          <div class="flex items-center">
            <AlertCircle class="w-5 h-5 text-red-600 mr-3" />
            <p class="text-red-800 text-sm font-medium">{{ errorMessage }}</p>
          </div>
        </div>

        <!-- Footer -->
        <div class="flex justify-end space-x-3 mt-8">
          <AppButton variant="ghost" @click="$emit('close')"> Close </AppButton>
          <AppButton
            variant="primary"
            :disabled="isLoading"
            @click="handlePurchase"
            class="flex items-center space-x-2"
          >
            <span v-if="!isLoading">Purchase Policy</span>
            <span v-else class="flex items-center space-x-2">
              <div
                class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
              ></div>
              <span>Processing...</span>
            </span>
          </AppButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useUserPolicyStore } from '@/stores/userPolicy'
import { useToast } from '@/composables/useToast'
import { Shield, X, AlertCircle } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { Policy } from '@/types/policy'

interface Props {
  policy: Policy
  isAuthenticated?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isAuthenticated: false,
})

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'apply', policy: Policy): void
}>()

const router = useRouter()
const authStore = useAuthStore()
const userPolicyStore = useUserPolicyStore()
const toast = useToast()

const isLoading = ref(false)
const errorMessage = ref('')

const isUserAuthenticated = computed(() => authStore.isAuthenticated)

const handlePurchase = async () => {
  // Check if user is authenticated
  if (!isUserAuthenticated.value) {
    // Redirect to login with policy info
    router.push({
      path: '/login',
      query: {
        redirect: '/policies',
        policyId: props.policy.id.toString(),
      },
    })
    emit('close')
    return
  }

  try {
    isLoading.value = true
    errorMessage.value = ''

    // Call the purchase API
    await userPolicyStore.purchasePolicy({
      policyId: props.policy.id,
      premiumPaid: props.policy.premiumAmount,
    })

    // Show success message
    toast.success(
      'Policy Purchase Initiated',
      `Your purchase request for "${props.policy.name}" has been submitted and is pending approval.`,
    )

    // Close modal
    emit('close')

    // Navigate to user policies page after a short delay
    setTimeout(() => {
      router.push('/my-policies')
    }, 1500)
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : 'Failed to purchase policy'
    if (typeof error === 'object' && error !== null) {
      const err = error as any
      errorMessage.value =
        err.response?.data?.errorMessage ||
        err.message ||
        'Failed to purchase policy'
    }
    toast.error('Purchase Failed', errorMessage.value)
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
    month: 'long',
    day: 'numeric',
  })
}
</script>

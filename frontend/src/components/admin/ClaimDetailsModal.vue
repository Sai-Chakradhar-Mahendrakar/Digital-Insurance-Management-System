<!-- src/components/admin/ClaimDetailsModal.vue -->
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
            <h3 class="text-2xl font-semibold text-slate-900 font-poppins">Claim Details</h3>
            <span class="text-sm text-slate-500">Claim #{{ claim.id }}</span>
          </div>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Claim Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Basic Info -->
          <div class="bg-slate-50 border border-slate-200 rounded-lg p-4">
            <h4 class="font-semibold text-slate-900 mb-3">Basic Information</h4>
            <div class="space-y-2 text-sm">
              <div class="flex justify-between">
                <span class="text-slate-500">Claim ID:</span>
                <span class="font-semibold">#{{ claim.id }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-500">Policy ID:</span>
                <span class="font-semibold">#{{ claim.userPolicyId }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-500">Policy Name:</span>
                <span class="font-semibold">{{ claim.policyName }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-500">Policy Type:</span>
                <span
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                >
                  {{ claim.policyType }}
                </span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-500">Status:</span>
                <span
                  :class="getStatusStyle(claim.status)"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                >
                  {{ claim.status }}
                </span>
              </div>
            </div>
          </div>

          <!-- Financial Info -->
          <div class="bg-slate-50 border border-slate-200 rounded-lg p-4">
            <h4 class="font-semibold text-slate-900 mb-3">Financial Details</h4>
            <div class="space-y-2 text-sm">
              <div class="flex justify-between">
                <span class="text-slate-500">Claim Amount:</span>
                <span class="font-bold text-lg">{{ formatINR(claim.claimAmount) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-500">Claim Date:</span>
                <span class="font-semibold">{{ formatDate(claim.claimDate) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-slate-500">Resolved Date:</span>
                <span class="font-semibold">{{
                  claim.resolvedDate ? formatDate(claim.resolvedDate) : 'Not resolved'
                }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Claim Reason -->
        <div class="mt-6 bg-slate-50 border border-slate-200 rounded-lg p-4">
          <h4 class="font-semibold text-slate-900 mb-3">Claim Reason</h4>
          <p class="text-slate-700">{{ claim.reason }}</p>
        </div>

        <!-- Reviewer Comment -->
        <div
          v-if="claim.reviewerComment"
          class="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-4"
        >
          <h4 class="font-semibold text-blue-900 mb-3">Reviewer Comment</h4>
          <p class="text-blue-800">{{ claim.reviewerComment }}</p>
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
import type { AdminClaim } from '@/stores/adminClaims'

interface Props {
  claim: AdminClaim
}

defineProps<Props>()

defineEmits<{
  (e: 'close'): void
}>()

const getStatusStyle = (status: string) => {
  const styles = {
    PENDING: 'bg-yellow-100 text-yellow-800',
    APPROVED: 'bg-green-100 text-green-800',
    REJECTED: 'bg-red-100 text-red-800',
  }
  return styles[status as keyof typeof styles] || styles.PENDING
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

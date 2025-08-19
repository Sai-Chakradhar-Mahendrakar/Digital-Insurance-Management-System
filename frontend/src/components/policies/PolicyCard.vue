<!-- src/components/policies/PolicyCard.vue -->
<template>
  <div
    class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 hover:shadow-md transition-shadow duration-200 ease-in-out"
  >
    <div class="flex items-start justify-between mb-4">
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
          <Shield class="w-5 h-5 text-blue-700" />
        </div>
        <div>
          <h3 class="font-semibold text-slate-900 font-poppins">{{ policy.name }}</h3>
          <span
            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800"
          >
            {{ policy.type }}
          </span>
        </div>
      </div>
    </div>

    <p class="text-slate-600 text-sm mb-4 font-inter">{{ policy.description }}</p>

    <div class="space-y-2 mb-6">
      <div class="flex justify-between text-sm">
        <span class="text-slate-500">Premium Amount</span>
        <span class="font-semibold text-slate-900"
          >${{ formatCurrency(policy.premiumAmount) }}</span
        >
      </div>
      <div class="flex justify-between text-sm">
        <span class="text-slate-500">Coverage Amount</span>
        <span class="font-semibold text-slate-900"
          >${{ formatCurrency(policy.coverageAmount) }}</span
        >
      </div>
      <div class="flex justify-between text-sm">
        <span class="text-slate-500">Duration</span>
        <span class="font-semibold text-slate-900">{{ policy.durationMonths }} months</span>
      </div>
    </div>

    <!-- Action Buttons -->
    <div class="flex space-x-2 mb-4">
      <AppButton variant="ghost" size="small" @click="$emit('viewDetails', policy)" class="flex-1">
        View Details
      </AppButton>
      <AppButton
        v-if="showApplyButton"
        variant="primary"
        size="small"
        @click="$emit('apply', policy)"
        class="flex-1"
      >
        Apply Now
      </AppButton>
    </div>

    <div class="pt-4 border-t border-slate-100">
      <p class="text-xs text-slate-500">Created: {{ formatDate(policy.createdAt) }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Shield } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { Policy } from '@/types/policy'

interface Props {
  policy: Policy
  showApplyButton?: boolean
}

withDefaults(defineProps<Props>(), {
  showApplyButton: false,
})

const emit = defineEmits<{
  (e: 'viewDetails', policy: Policy): void
  (e: 'apply', policy: Policy): void
}>()

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount)
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}
</script>

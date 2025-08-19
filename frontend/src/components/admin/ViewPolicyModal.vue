<!-- src/components/admin/ViewPolicyModal.vue -->
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
          <div class="flex items-center space-x-3">
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
              <Shield class="w-6 h-6 text-blue-700" />
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-slate-900 font-poppins">{{ policy!.name }}</h3>
              <span
                class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-emerald-100 text-emerald-800"
              >
                {{ policy!.type }} Insurance
              </span>
            </div>
          </div>
          <div class="flex items-center space-x-2">
            <AppButton
              variant="secondary"
              @click="$emit('edit', policy!)"
              class="flex items-center space-x-2"
            >
              <Edit class="w-4 h-4" />
              <span>Edit</span>
            </AppButton>
            <button
              @click="$emit('close')"
              class="text-slate-400 hover:text-slate-600 transition-colors"
            >
              <X class="w-6 h-6" />
            </button>
          </div>
        </div>

        <!-- Policy Details -->
        <div class="space-y-6">
          <!-- Description -->
          <div>
            <h4 class="font-medium text-slate-900 mb-2 font-poppins">Description</h4>
            <p class="text-slate-600 font-inter">{{ policy!.description }}</p>
          </div>

          <!-- Financial Details Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-4">
              <h4 class="font-medium text-slate-900 font-poppins">Financial Details</h4>
              <div class="space-y-3">
                <div class="flex justify-between p-3 bg-slate-50 rounded-lg">
                  <span class="text-slate-500">Premium Amount</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy!.premiumAmount)
                  }}</span>
                </div>
                <div class="flex justify-between p-3 bg-slate-50 rounded-lg">
                  <span class="text-slate-500">Coverage Amount</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy!.coverageAmount)
                  }}</span>
                </div>
                <div class="flex justify-between p-3 bg-slate-50 rounded-lg">
                  <span class="text-slate-500">Renewal Premium</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy!.renewalPremiumRate)
                  }}</span>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <h4 class="font-medium text-slate-900 font-poppins">Policy Information</h4>
              <div class="space-y-3">
                <div class="flex justify-between p-3 bg-slate-50 rounded-lg">
                  <span class="text-slate-500">Policy ID</span>
                  <span class="font-semibold text-slate-900">#{{ policy!.id }}</span>
                </div>
                <div class="flex justify-between p-3 bg-slate-50 rounded-lg">
                  <span class="text-slate-500">Duration</span>
                  <span class="font-semibold text-slate-900"
                    >{{ policy!.durationMonths }} months</span
                  >
                </div>
                <div class="flex justify-between p-3 bg-slate-50 rounded-lg">
                  <span class="text-slate-500">Created Date</span>
                  <span class="font-semibold text-slate-900">{{
                    formatDate(policy!.createdAt)
                  }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Coverage Summary -->
          <div class="bg-blue-50 rounded-lg p-6">
            <h4 class="font-medium text-blue-900 mb-4 font-poppins">Coverage Summary</h4>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
              <div class="text-center p-4 bg-white rounded-lg">
                <div class="font-semibold text-blue-900 text-lg">
                  {{ formatINR(policy!.coverageAmount) }}
                </div>
                <div class="text-blue-600">Total Coverage</div>
              </div>
              <div class="text-center p-4 bg-white rounded-lg">
                <div class="font-semibold text-blue-900 text-lg">
                  {{ formatINR(policy!.premiumAmount) }}
                </div>
                <div class="text-blue-600">Annual Premium</div>
              </div>
              <div class="text-center p-4 bg-white rounded-lg">
                <div class="font-semibold text-blue-900 text-lg">
                  {{ Math.round((policy!.coverageAmount / policy!.premiumAmount) * 100) / 100 }}x
                </div>
                <div class="text-blue-600">Coverage Ratio</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="flex justify-end space-x-3 mt-8 pt-6 border-t border-slate-200">
          <AppButton variant="ghost" @click="$emit('close')"> Close </AppButton>
          <AppButton
            variant="primary"
            @click="$emit('edit', policy!)"
            class="flex items-center space-x-2"
          >
            <Edit class="w-4 h-4" />
            <span>Edit Policy</span>
          </AppButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Shield, X, Edit } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { Policy } from '@/types/policy'

interface Props {
  policy: Policy | null
}

defineProps<Props>()

defineEmits<{
  (e: 'close'): void
  (e: 'edit', policy: Policy): void
}>()

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

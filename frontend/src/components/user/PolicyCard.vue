<!-- src/components/user/PolicyCard.vue -->
<template>
  <div
    class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 hover:shadow-md transition-shadow duration-200"
  >
    <!-- Header with Status -->
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
          <Shield class="w-5 h-5 text-blue-600" />
        </div>
        <div>
          <h3 class="font-semibold text-slate-900">{{ userPolicy.policyName }}</h3>
          <p class="text-sm text-slate-500">{{ userPolicy.policyType }} Insurance</p>
        </div>
      </div>

      <!-- Status Badge -->
      <div
        :class="getStatusStyle(userPolicy.status)"
        class="px-3 py-1 rounded-full text-xs font-medium flex items-center"
      >
        <component :is="getStatusIcon(userPolicy.status)" class="w-3 h-3 mr-1" />
        {{ getStatusText(userPolicy.status) }}
      </div>
    </div>

    <!-- Policy Details -->
    <div class="space-y-3 mb-4">
      <div class="flex justify-between text-sm">
        <span class="text-slate-500">Premium Paid</span>
        <span class="font-semibold text-slate-900">{{ formatINR(userPolicy.premiumPaid) }}</span>
      </div>
      <div class="flex justify-between text-sm">
        <span class="text-slate-500">Policy ID</span>
        <span class="font-semibold text-slate-900">#{{ userPolicy.policyId }}</span>
      </div>
      <div class="flex justify-between text-sm">
        <span class="text-slate-500">Start Date</span>
        <span class="font-semibold text-slate-900">{{ formatDate(userPolicy.startDate) }}</span>
      </div>
      <div class="flex justify-between text-sm">
        <span class="text-slate-500">End Date</span>
        <span class="font-semibold text-slate-900">{{ formatDate(userPolicy.endDate) }}</span>
      </div>
      <div class="flex justify-between text-sm">
        <span class="text-slate-500">Duration</span>
        <span class="font-semibold text-slate-900">{{
          calculateDuration(userPolicy.startDate, userPolicy.endDate)
        }}</span>
      </div>
    </div>

    <!-- Policy Description -->
    <div class="mb-4">
      <p class="text-slate-600 text-sm">
        {{ userPolicy.policyType }} insurance policy providing comprehensive coverage and
        protection.
      </p>
    </div>

    <!-- Action Button -->
    <AppButton variant="ghost" size="small" @click="$emit('view', userPolicy)" class="w-full">
      View Details
    </AppButton>

    <!-- Status Timeline -->
    <div v-if="userPolicy.status === 'PENDING'" class="mt-4 pt-4 border-t border-slate-100">
      <div class="flex items-center text-sm text-slate-500">
        <Clock class="w-4 h-4 mr-2" />
        Waiting for approval...
      </div>
    </div>

    <div v-if="userPolicy.status === 'ACTIVE'" class="mt-4 pt-4 border-t border-slate-100">
      <div class="flex items-center text-sm text-green-600">
        <CheckCircle class="w-4 h-4 mr-2" />
        Policy is active
      </div>
    </div>

    <div v-if="userPolicy.status === 'APPROVED'" class="mt-4 pt-4 border-t border-slate-100">
      <div class="flex items-center text-sm text-green-600">
        <CheckCircle class="w-4 h-4 mr-2" />
        Policy approved
      </div>
    </div>

    <div v-if="userPolicy.status === 'REJECTED'" class="mt-4 pt-4 border-t border-slate-100">
      <div class="flex items-center text-sm text-red-600">
        <XCircle class="w-4 h-4 mr-2" />
        Application rejected
      </div>
    </div>

    <div v-if="userPolicy.status === 'EXPIRED'" class="mt-4 pt-4 border-t border-slate-100">
      <div class="flex items-center text-sm text-gray-600">
        <AlertTriangle class="w-4 h-4 mr-2" />
        Policy expired
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Clock, CheckCircle, XCircle, AlertTriangle, Shield } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { UserPolicy } from '@/stores/userPolicy'

interface Props {
  userPolicy: UserPolicy
}

defineProps<Props>()

defineEmits<{
  (e: 'view', userPolicy: UserPolicy): void
}>()

const getStatusStyle = (status: string) => {
  const styles = {
    PENDING: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
    APPROVED: 'bg-green-100 text-green-800 border border-green-200',
    REJECTED: 'bg-red-100 text-red-800 border border-red-200',
    ACTIVE: 'bg-blue-100 text-blue-800 border border-blue-200',
    EXPIRED: 'bg-gray-100 text-gray-800 border border-gray-200',
  }
  return styles[status] || styles.PENDING
}

const getStatusIcon = (status: string) => {
  const icons = {
    PENDING: Clock,
    APPROVED: CheckCircle,
    REJECTED: XCircle,
    ACTIVE: Shield,
    EXPIRED: AlertTriangle,
  }
  return icons[status] || Clock
}

const getStatusText = (status: string) => {
  const texts = {
    PENDING: 'Pending Approval',
    APPROVED: 'Approved',
    REJECTED: 'Rejected',
    ACTIVE: 'Active',
    EXPIRED: 'Expired',
  }
  return texts[status] || status
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

const calculateDuration = (startDate: string, endDate: string): string => {
  const start = new Date(startDate)
  const end = new Date(endDate)
  const diffTime = Math.abs(end.getTime() - start.getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  const diffMonths = Math.round(diffDays / 30)

  if (diffMonths >= 12) {
    const years = Math.floor(diffMonths / 12)
    const remainingMonths = diffMonths % 12
    if (remainingMonths === 0) {
      return `${years} year${years > 1 ? 's' : ''}`
    }
    return `${years}y ${remainingMonths}m`
  }

  return `${diffMonths} month${diffMonths > 1 ? 's' : ''}`
}
</script>

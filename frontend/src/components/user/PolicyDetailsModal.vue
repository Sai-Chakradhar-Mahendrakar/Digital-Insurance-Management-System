<!-- src/components/user/PolicyDetailsModal.vue -->
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
              <h3 class="text-2xl font-semibold text-slate-900 font-poppins">
                {{ userPolicy.policyName }}
              </h3>
              <span
                class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-emerald-100 text-emerald-800"
              >
                {{ userPolicy.policyType }} Insurance
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
            <h4 class="font-medium text-slate-900 mb-2 font-poppins">Policy Description</h4>
            <p class="text-slate-600 font-inter">
              {{ userPolicy.policyType }} insurance policy providing comprehensive coverage and
              protection for your needs. This policy covers various risks and provides financial
              security with professional claim support.
            </p>
          </div>

          <!-- Financial & Policy Details Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Financial Information -->
            <div class="space-y-4">
              <h4 class="font-medium text-slate-900 font-poppins">Financial Details</h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-slate-500">Premium Paid</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(userPolicy.premiumPaid)
                  }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Policy Duration</span>
                  <span class="font-semibold text-slate-900">{{
                    calculateDuration(userPolicy.startDate, userPolicy.endDate)
                  }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Policy Status</span>
                  <span :class="getStatusTextColor(userPolicy.status)" class="font-semibold">{{
                    getStatusText(userPolicy.status)
                  }}</span>
                </div>
              </div>
            </div>

            <!-- Policy Information -->
            <div class="space-y-4">
              <h4 class="font-medium text-slate-900 font-poppins">Policy Information</h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-slate-500">Policy ID</span>
                  <span class="font-semibold text-slate-900">#{{ userPolicy.policyId }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">User Policy ID</span>
                  <span class="font-semibold text-slate-900">#{{ userPolicy.id }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Start Date</span>
                  <span class="font-semibold text-slate-900">{{
                    formatDate(userPolicy.startDate)
                  }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">End Date</span>
                  <span class="font-semibold text-slate-900">{{
                    formatDate(userPolicy.endDate)
                  }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Coverage Summary -->
          <div class="bg-blue-50 rounded-lg p-4">
            <h4 class="font-medium text-blue-900 mb-2 font-poppins">Policy Summary</h4>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
              <div class="text-center">
                <div class="font-semibold text-blue-900">
                  {{ formatINR(userPolicy.premiumPaid) }}
                </div>
                <div class="text-blue-600">Premium Paid</div>
              </div>
              <div class="text-center">
                <div class="font-semibold text-blue-900">
                  {{ calculateDuration(userPolicy.startDate, userPolicy.endDate) }}
                </div>
                <div class="text-blue-600">Coverage Period</div>
              </div>
              <div class="text-center">
                <div class="font-semibold text-blue-900">{{ userPolicy.policyType }}</div>
                <div class="text-blue-600">Insurance Type</div>
              </div>
            </div>
          </div>

          <!-- Status Information -->
          <div
            v-if="userPolicy.status === 'PENDING'"
            class="bg-yellow-50 border border-yellow-200 rounded-lg p-4"
          >
            <h4 class="font-medium text-yellow-900 mb-2 font-poppins flex items-center">
              <Clock class="w-4 h-4 mr-2" />
              Policy Under Review
            </h4>
            <div class="text-sm text-yellow-700 space-y-1">
              <p>• Your policy application is currently being reviewed by our team</p>
              <p>• This process typically takes 24-48 hours</p>
              <p>• You will receive an email notification once approved</p>
            </div>
          </div>

          <div
            v-if="userPolicy.status === 'ACTIVE'"
            class="bg-green-50 border border-green-200 rounded-lg p-4"
          >
            <h4 class="font-medium text-green-900 mb-2 font-poppins flex items-center">
              <CheckCircle class="w-4 h-4 mr-2" />
              Policy Active & Coverage Details
            </h4>
            <div class="text-sm text-green-700 space-y-1">
              <p>• Your policy is now active and provides full coverage</p>
              <p>
                • Coverage period: {{ formatDate(userPolicy.startDate) }} to
                {{ formatDate(userPolicy.endDate) }}
              </p>
              <p>• 24/7 claim support available through our customer service</p>
            </div>
          </div>

          <div
            v-if="userPolicy.status === 'APPROVED'"
            class="bg-green-50 border border-green-200 rounded-lg p-4"
          >
            <h4 class="font-medium text-green-900 mb-2 font-poppins flex items-center">
              <CheckCircle class="w-4 h-4 mr-2" />
              Policy Approved
            </h4>
            <div class="text-sm text-green-700 space-y-1">
              <p>• Your policy has been approved and will be activated soon</p>
              <p>• You will receive a policy document via email</p>
              <p>• Coverage will begin from {{ formatDate(userPolicy.startDate) }}</p>
            </div>
          </div>

          <div
            v-if="userPolicy.status === 'REJECTED'"
            class="bg-red-50 border border-red-200 rounded-lg p-4"
          >
            <h4 class="font-medium text-red-900 mb-2 font-poppins flex items-center">
              <XCircle class="w-4 h-4 mr-2" />
              Application Status
            </h4>
            <div class="text-sm text-red-700 space-y-1">
              <p>• Unfortunately, your policy application has been rejected</p>
              <p>• Please contact our support team for more information</p>
              <p>• You may reapply after addressing the mentioned concerns</p>
            </div>
          </div>

          <div
            v-if="userPolicy.status === 'EXPIRED'"
            class="bg-gray-50 border border-gray-200 rounded-lg p-4"
          >
            <h4 class="font-medium text-gray-900 mb-2 font-poppins flex items-center">
              <AlertTriangle class="w-4 h-4 mr-2" />
              Policy Renewal Required
            </h4>
            <div class="text-sm text-gray-700 space-y-1">
              <p>• This policy expired on {{ formatDate(userPolicy.endDate) }}</p>
              <p>• Contact us to renew your coverage and maintain protection</p>
              <p>• Renewal may be available with updated terms and premium</p>
            </div>
          </div>

          <!-- Timeline -->
          <div class="bg-slate-50 rounded-lg p-4">
            <h4 class="font-medium text-slate-900 mb-3 font-poppins flex items-center">
              <Clock class="w-4 h-4 mr-2" />
              Policy Timeline
            </h4>
            <div class="space-y-3">
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                  <ShoppingCart class="w-4 h-4 text-white" />
                </div>
                <div class="flex-1">
                  <p class="text-sm font-medium text-slate-900">Policy Purchased</p>
                  <p class="text-xs text-slate-500">{{ formatDateTime(userPolicy.startDate) }}</p>
                </div>
              </div>
              <div class="flex items-center space-x-3">
                <div
                  :class="getTimelineIconBg(userPolicy.status)"
                  class="w-8 h-8 rounded-full flex items-center justify-center"
                >
                  <component :is="getStatusIcon(userPolicy.status)" class="w-4 h-4 text-white" />
                </div>
                <div class="flex-1">
                  <p class="text-sm font-medium text-slate-900">
                    {{ getTimelineTitle(userPolicy.status) }}
                  </p>
                  <p class="text-xs text-slate-500">
                    {{ getTimelineDescription(userPolicy.status) }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="flex justify-end space-x-3 mt-8">
          <AppButton variant="ghost" @click="$emit('close')"> Close </AppButton>
          <AppButton v-if="userPolicy.status === 'ACTIVE'" variant="primary">
            <FileText class="w-4 h-4 mr-2" />
            Download Policy Document
          </AppButton>
          <AppButton v-if="userPolicy.status === 'PENDING'" variant="primary">
            <Phone class="w-4 h-4 mr-2" />
            Contact Support
          </AppButton>
          <AppButton v-if="userPolicy.status === 'EXPIRED'" variant="primary">
            <RefreshCw class="w-4 h-4 mr-2" />
            Renew Policy
          </AppButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  Shield,
  X,
  Clock,
  CheckCircle,
  XCircle,
  AlertTriangle,
  ShoppingCart,
  FileText,
  Phone,
  RefreshCw,
} from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { UserPolicy } from '@/stores/userPolicy'

interface Props {
  userPolicy: UserPolicy
}

defineProps<Props>()

defineEmits<{
  (e: 'close'): void
}>()

const getStatusText = (status: string) => {
  const texts = {
    PENDING: 'Pending Approval',
    APPROVED: 'Approved',
    REJECTED: 'Rejected',
    ACTIVE: 'Active',
    EXPIRED: 'Expired',
  }
  return texts[status as keyof typeof texts] || status
}

const getStatusTextColor = (status: string) => {
  const colors = {
    PENDING: 'text-yellow-700',
    APPROVED: 'text-green-700',
    REJECTED: 'text-red-700',
    ACTIVE: 'text-blue-700',
    EXPIRED: 'text-gray-700',
  }
  return colors[status as keyof typeof colors] || 'text-slate-900'
}

const getStatusIcon = (status: string) => {
  const icons = {
    PENDING: Clock,
    APPROVED: CheckCircle,
    REJECTED: XCircle,
    ACTIVE: CheckCircle,
    EXPIRED: AlertTriangle,
  }
  return icons[status as keyof typeof icons] || Clock
}

const getTimelineIconBg = (status: string) => {
  const bgs = {
    PENDING: 'bg-yellow-500',
    APPROVED: 'bg-green-500',
    REJECTED: 'bg-red-500',
    ACTIVE: 'bg-green-500',
    EXPIRED: 'bg-gray-500',
  }
  return bgs[status as keyof typeof bgs] || 'bg-yellow-500'
}

const getTimelineTitle = (status: string) => {
  const titles = {
    PENDING: 'Under Review',
    APPROVED: 'Application Approved',
    REJECTED: 'Application Rejected',
    ACTIVE: 'Policy Activated',
    EXPIRED: 'Policy Expired',
  }
  return titles[status as keyof typeof titles] || 'Processing'
}

const getTimelineDescription = (status: string) => {
  const descriptions = {
    PENDING: 'Waiting for admin review and approval',
    APPROVED: 'Policy approved and ready for activation',
    REJECTED: 'Application was not approved',
    ACTIVE: 'Policy is currently providing coverage',
    EXPIRED: 'Policy coverage has ended',
  }
  return descriptions[status as keyof typeof descriptions] || 'Processing your request'
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

const formatDateTime = (dateString: string): string => {
  return new Date(dateString).toLocaleString('en-IN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
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

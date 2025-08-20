<!-- src/components/user/PolicyDetailsModal.vue -->
<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 overflow-y-auto">
      <div
        class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0"
      >
        <!-- Background overlay -->
        <div
          class="fixed inset-0 transition-opacity bg-slate-500 bg-opacity-75"
          @click="$emit('close')"
        ></div>

        <!-- Modal container -->
        <div
          class="inline-block w-full max-w-4xl p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-2xl rounded-2xl relative"
        >
          <!-- Header -->
          <div class="flex items-center justify-between mb-6 pb-4 border-b border-slate-200">
            <div class="flex items-center space-x-4">
              <div
                class="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center shadow-lg"
              >
                <Shield class="w-8 h-8 text-white" />
              </div>
              <div>
                <h2 class="text-2xl font-bold text-slate-900 font-poppins">
                  {{ userPolicy.policyName }}
                </h2>
                <div class="flex items-center space-x-3 mt-1">
                  <span
                    class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
                  >
                    {{ userPolicy.policyType }} Insurance
                  </span>
                  <div
                    :class="getStatusStyle(userPolicy.status)"
                    class="px-3 py-1 rounded-full text-sm font-medium flex items-center"
                  >
                    <component :is="getStatusIcon(userPolicy.status)" class="w-4 h-4 mr-1" />
                    {{ getStatusText(userPolicy.status) }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Close button -->
            <button
              @click="$emit('close')"
              class="text-slate-400 hover:text-slate-600 transition-colors p-2 hover:bg-slate-100 rounded-full"
            >
              <X class="w-6 h-6" />
            </button>
          </div>

          <!-- Content -->
          <div class="space-y-8">
            <!-- Policy Description -->
            <div class="bg-slate-50 rounded-xl p-6">
              <h3 class="text-lg font-semibold text-slate-900 mb-3 font-poppins flex items-center">
                <FileText class="w-5 h-5 mr-2 text-slate-600" />
                Policy Description
              </h3>
              <p class="text-slate-700 leading-relaxed">
                {{ userPolicy.policyType }} insurance policy providing comprehensive coverage and
                protection for your needs. This policy covers various risks and provides financial
                security with professional claim support.
              </p>
            </div>

            <!-- Policy Details Grid -->
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <!-- Financial Information -->
              <div class="bg-white border border-slate-200 rounded-xl p-6">
                <h3
                  class="text-lg font-semibold text-slate-900 mb-4 font-poppins flex items-center"
                >
                  <CreditCard class="w-5 h-5 mr-2 text-green-600" />
                  Financial Details
                </h3>
                <div class="space-y-4">
                  <div class="flex justify-between items-center p-3 bg-green-50 rounded-lg">
                    <span class="text-slate-600 font-medium">Premium Paid</span>
                    <span class="text-xl font-bold text-green-700">{{
                      formatINR(userPolicy.premiumPaid)
                    }}</span>
                  </div>
                  <div class="flex justify-between items-center p-3 bg-blue-50 rounded-lg">
                    <span class="text-slate-600 font-medium">Policy Duration</span>
                    <span class="text-xl font-bold text-blue-700">{{
                      calculateDuration(userPolicy.startDate, userPolicy.endDate)
                    }}</span>
                  </div>
                  <div class="flex justify-between items-center p-3 bg-purple-50 rounded-lg">
                    <span class="text-slate-600 font-medium">Policy Status</span>
                    <span class="text-lg font-bold text-purple-700">{{ userPolicy.status }}</span>
                  </div>
                </div>
              </div>

              <!-- Policy Information -->
              <div class="bg-white border border-slate-200 rounded-xl p-6">
                <h3
                  class="text-lg font-semibold text-slate-900 mb-4 font-poppins flex items-center"
                >
                  <Info class="w-5 h-5 mr-2 text-blue-600" />
                  Policy Information
                </h3>
                <div class="space-y-4">
                  <div class="flex justify-between items-center">
                    <span class="text-slate-600">Policy ID</span>
                    <span class="font-semibold text-slate-900">#{{ userPolicy.policyId }}</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-slate-600">User Policy ID</span>
                    <span class="font-semibold text-slate-900">#{{ userPolicy.id }}</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-slate-600">Start Date</span>
                    <span class="font-semibold text-slate-900">{{
                      formatDate(userPolicy.startDate)
                    }}</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-slate-600">End Date</span>
                    <span class="font-semibold text-slate-900">{{
                      formatDate(userPolicy.endDate)
                    }}</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-slate-600">Policy Type</span>
                    <span class="font-semibold text-slate-900">{{ userPolicy.policyType }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Status Timeline -->
            <div class="bg-gradient-to-r from-slate-50 to-blue-50 rounded-xl p-6">
              <h3 class="text-lg font-semibold text-slate-900 mb-4 font-poppins flex items-center">
                <Clock class="w-5 h-5 mr-2 text-slate-600" />
                Policy Status & Timeline
              </h3>

              <div class="space-y-4">
                <!-- Purchase Status -->
                <div class="flex items-center space-x-4">
                  <div class="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center">
                    <ShoppingCart class="w-5 h-5 text-white" />
                  </div>
                  <div class="flex-1">
                    <h4 class="font-medium text-slate-900">Policy Purchased</h4>
                    <p class="text-sm text-slate-600">{{ formatDateTime(userPolicy.startDate) }}</p>
                  </div>
                  <CheckCircle class="w-5 h-5 text-blue-500" />
                </div>

                <!-- Current Status -->
                <div class="flex items-center space-x-4">
                  <div
                    :class="getTimelineIconBg(userPolicy.status)"
                    class="w-10 h-10 rounded-full flex items-center justify-center"
                  >
                    <component
                      :is="getTimelineIcon(userPolicy.status)"
                      class="w-5 h-5 text-white"
                    />
                  </div>
                  <div class="flex-1">
                    <h4 class="font-medium text-slate-900">
                      {{ getTimelineTitle(userPolicy.status) }}
                    </h4>
                    <p class="text-sm text-slate-600">
                      {{ getTimelineDescription(userPolicy.status) }}
                    </p>
                  </div>
                  <component
                    :is="getTimelineStatusIcon(userPolicy.status)"
                    :class="getTimelineStatusColor(userPolicy.status)"
                    class="w-5 h-5"
                  />
                </div>
              </div>
            </div>

            <!-- Status-specific Information -->
            <div
              v-if="userPolicy.status === 'PENDING'"
              class="bg-yellow-50 border border-yellow-200 rounded-xl p-6"
            >
              <div class="flex items-start space-x-3">
                <AlertTriangle class="w-6 h-6 text-yellow-600 mt-0.5" />
                <div>
                  <h3 class="font-medium text-yellow-900 mb-1">Policy Under Review</h3>
                  <p class="text-yellow-800 text-sm mb-3">
                    Your policy application is currently being reviewed by our team. This process
                    typically takes 24-48 hours.
                  </p>
                  <div class="text-sm text-yellow-700">
                    <p><strong>What happens next:</strong></p>
                    <ul class="list-disc list-inside mt-1 space-y-1">
                      <li>Document verification</li>
                      <li>Risk assessment</li>
                      <li>Final approval decision</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>

            <div
              v-if="userPolicy.status === 'APPROVED'"
              class="bg-green-50 border border-green-200 rounded-xl p-6"
            >
              <div class="flex items-start space-x-3">
                <CheckCircle class="w-6 h-6 text-green-600 mt-0.5" />
                <div>
                  <h3 class="font-medium text-green-900 mb-1">Policy Approved</h3>
                  <p class="text-green-800 text-sm">
                    Your policy has been approved and will be activated soon. You will receive a
                    policy document via email.
                  </p>
                </div>
              </div>
            </div>

            <div
              v-if="userPolicy.status === 'ACTIVE'"
              class="bg-green-50 border border-green-200 rounded-xl p-6"
            >
              <div class="flex items-start space-x-3">
                <CheckCircle class="w-6 h-6 text-green-600 mt-0.5" />
                <div>
                  <h3 class="font-medium text-green-900 mb-1">Policy Active</h3>
                  <p class="text-green-800 text-sm">
                    Your policy is now active and provides full coverage as per the terms and
                    conditions. Coverage is valid from {{ formatDate(userPolicy.startDate) }} to
                    {{ formatDate(userPolicy.endDate) }}.
                  </p>
                </div>
              </div>
            </div>

            <div
              v-if="userPolicy.status === 'REJECTED'"
              class="bg-red-50 border border-red-200 rounded-xl p-6"
            >
              <div class="flex items-start space-x-3">
                <XCircle class="w-6 h-6 text-red-600 mt-0.5" />
                <div>
                  <h3 class="font-medium text-red-900 mb-1">Policy Application Rejected</h3>
                  <p class="text-red-800 text-sm">
                    Unfortunately, your policy application has been rejected. Please contact our
                    support team for more information about the decision.
                  </p>
                </div>
              </div>
            </div>

            <div
              v-if="userPolicy.status === 'EXPIRED'"
              class="bg-gray-50 border border-gray-200 rounded-xl p-6"
            >
              <div class="flex items-start space-x-3">
                <AlertTriangle class="w-6 h-6 text-gray-600 mt-0.5" />
                <div>
                  <h3 class="font-medium text-gray-900 mb-1">Policy Expired</h3>
                  <p class="text-gray-800 text-sm">
                    This policy has expired on {{ formatDate(userPolicy.endDate) }}. Please contact
                    us to renew your coverage.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer Actions -->
          <div class="flex justify-end space-x-4 pt-6 mt-8 border-t border-slate-200">
            <AppButton variant="ghost" @click="$emit('close')"> Close </AppButton>
            <AppButton v-if="userPolicy.status === 'ACTIVE'" variant="primary">
              <FileText class="w-4 h-4 mr-2" />
              Download Policy Document
            </AppButton>
            <AppButton v-if="userPolicy.status === 'PENDING'" variant="secondary">
              <Info class="w-4 h-4 mr-2" />
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
  </Teleport>
</template>

<script setup lang="ts">
import {
  Shield,
  X,
  FileText,
  CreditCard,
  Info,
  Clock,
  CheckCircle,
  ShoppingCart,
  AlertTriangle,
  XCircle,
  UserCheck,
  Hourglass,
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

const getStatusStyle = (status: string) => {
  const styles = {
    PENDING: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
    APPROVED: 'bg-green-100 text-green-800 border border-green-200',
    REJECTED: 'bg-red-100 text-red-800 border border-red-200',
    ACTIVE: 'bg-blue-100 text-blue-800 border border-blue-200',
    EXPIRED: 'bg-gray-100 text-gray-800 border border-gray-200',
  }
  return styles[status as keyof typeof styles] || styles.PENDING
}

const getStatusIcon = (status: string) => {
  const icons = {
    PENDING: Clock,
    APPROVED: CheckCircle,
    REJECTED: XCircle,
    ACTIVE: Shield,
    EXPIRED: AlertTriangle,
  }
  return icons[status as keyof typeof icons] || Clock
}

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

const getTimelineIcon = (status: string) => {
  const icons = {
    PENDING: Hourglass,
    APPROVED: UserCheck,
    REJECTED: XCircle,
    ACTIVE: Shield,
    EXPIRED: AlertTriangle,
  }
  return icons[status as keyof typeof icons] || Hourglass
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
    APPROVED: 'Your policy has been approved and is being activated',
    REJECTED: 'Policy application was not approved',
    ACTIVE: 'Your policy is currently active and providing coverage',
    EXPIRED: 'Policy coverage has ended and requires renewal',
  }
  return descriptions[status as keyof typeof descriptions] || 'Processing your request'
}

const getTimelineStatusIcon = (status: string) => {
  const icons = {
    PENDING: Clock,
    APPROVED: CheckCircle,
    REJECTED: XCircle,
    ACTIVE: CheckCircle,
    EXPIRED: XCircle,
  }
  return icons[status as keyof typeof icons] || Clock
}

const getTimelineStatusColor = (status: string) => {
  const colors = {
    PENDING: 'text-yellow-500',
    APPROVED: 'text-green-500',
    REJECTED: 'text-red-500',
    ACTIVE: 'text-green-500',
    EXPIRED: 'text-gray-500',
  }
  return colors[status as keyof typeof colors] || 'text-yellow-500'
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

<style scoped>
/* Modal animation */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* Smooth hover effects */
.hover\:shadow-md:hover {
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* Custom scrollbar for modal content */
.modal-content::-webkit-scrollbar {
  width: 6px;
}

.modal-content::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.modal-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>

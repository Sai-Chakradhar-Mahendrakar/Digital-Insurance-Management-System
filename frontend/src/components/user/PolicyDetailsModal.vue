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
                  {{ userPolicy.policy.name }}
                </h2>
                <div class="flex items-center space-x-3 mt-1">
                  <span
                    class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
                  >
                    {{ userPolicy.policy.type }} Insurance
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
              <p class="text-slate-700 leading-relaxed">{{ userPolicy.policy.description }}</p>
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
                    <span class="text-slate-600 font-medium">Coverage Amount</span>
                    <span class="text-xl font-bold text-blue-700">{{
                      formatINR(userPolicy.policy.coverageAmount)
                    }}</span>
                  </div>
                  <div class="flex justify-between items-center p-3 bg-purple-50 rounded-lg">
                    <span class="text-slate-600 font-medium">Coverage Ratio</span>
                    <span class="text-lg font-bold text-purple-700">
                      {{
                        Math.round(
                          (userPolicy.policy.coverageAmount / userPolicy.premiumPaid) * 100,
                        ) / 100
                      }}x
                    </span>
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
                    <span class="text-slate-600">Duration</span>
                    <span class="font-semibold text-slate-900"
                      >{{ userPolicy.policy.durationMonths }} months</span
                    >
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-slate-600">Purchase Date</span>
                    <span class="font-semibold text-slate-900">{{
                      formatDate(userPolicy.purchaseDate)
                    }}</span>
                  </div>
                  <div v-if="userPolicy.approvalDate" class="flex justify-between items-center">
                    <span class="text-slate-600">Approval Date</span>
                    <span class="font-semibold text-green-700">{{
                      formatDate(userPolicy.approvalDate)
                    }}</span>
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
                    <p class="text-sm text-slate-600">
                      {{ formatDateTime(userPolicy.purchaseDate) }}
                    </p>
                  </div>
                  <CheckCircle class="w-5 h-5 text-blue-500" />
                </div>

                <!-- Approval Status -->
                <div class="flex items-center space-x-4">
                  <div
                    :class="
                      userPolicy.status === 'PENDING'
                        ? 'bg-yellow-500'
                        : userPolicy.status === 'APPROVED' || userPolicy.status === 'ACTIVE'
                          ? 'bg-green-500'
                          : 'bg-red-500'
                    "
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
                      {{
                        userPolicy.approvalDate
                          ? formatDateTime(userPolicy.approvalDate)
                          : 'Waiting for admin review...'
                      }}
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

            <!-- Additional Information -->
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
              v-if="userPolicy.status === 'APPROVED' || userPolicy.status === 'ACTIVE'"
              class="bg-green-50 border border-green-200 rounded-xl p-6"
            >
              <div class="flex items-start space-x-3">
                <CheckCircle class="w-6 h-6 text-green-600 mt-0.5" />
                <div>
                  <h3 class="font-medium text-green-900 mb-1">
                    Policy {{ userPolicy.status === 'ACTIVE' ? 'Active' : 'Approved' }}
                  </h3>
                  <p class="text-green-800 text-sm">
                    {{
                      userPolicy.status === 'ACTIVE'
                        ? 'Your policy is now active and provides full coverage as per the terms and conditions.'
                        : 'Your policy has been approved and will be activated soon. You will receive a policy document via email.'
                    }}
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
          </div>

          <!-- Footer Actions -->
          <div class="flex justify-end space-x-4 pt-6 mt-8 border-t border-slate-200">
            <AppButton variant="ghost" @click="$emit('close')"> Close </AppButton>
            <AppButton v-if="userPolicy.status === 'ACTIVE'" variant="primary">
              Download Policy Document
            </AppButton>
            <AppButton v-if="userPolicy.status === 'PENDING'" variant="secondary">
              Contact Support
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
} from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { UserPolicy } from '@/stores/userPolicy'

type PolicyStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'ACTIVE' | 'EXPIRED'

interface Props {
  userPolicy: Omit<UserPolicy, 'status'> & { status: PolicyStatus }
}

defineProps<Props>()
defineEmits<{ (e: 'close'): void }>()

const STATUS_STYLES: Record<PolicyStatus, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
  APPROVED: 'bg-green-100 text-green-800 border border-green-200',
  REJECTED: 'bg-red-100 text-red-800 border border-red-200',
  ACTIVE: 'bg-blue-100 text-blue-800 border border-blue-200',
  EXPIRED: 'bg-gray-100 text-gray-800 border border-gray-200',
}

const STATUS_ICONS: Record<PolicyStatus, any> = {
  PENDING: Clock,
  APPROVED: CheckCircle,
  REJECTED: XCircle,
  ACTIVE: Shield,
  EXPIRED: AlertTriangle,
}

const STATUS_TEXTS: Record<PolicyStatus, string> = {
  PENDING: 'Pending Approval',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
  ACTIVE: 'Active',
  EXPIRED: 'Expired',
}

const TIMELINE_ICONS: Record<PolicyStatus, any> = {
  PENDING: Hourglass,
  APPROVED: UserCheck,
  REJECTED: XCircle,
  ACTIVE: Shield,
  EXPIRED: AlertTriangle,
}

const TIMELINE_TITLES: Record<PolicyStatus, string> = {
  PENDING: 'Under Review',
  APPROVED: 'Application Approved',
  REJECTED: 'Application Rejected',
  ACTIVE: 'Policy Activated',
  EXPIRED: 'Policy Expired',
}

const TIMELINE_STATUS_ICONS: Record<PolicyStatus, any> = {
  PENDING: Clock,
  APPROVED: CheckCircle,
  REJECTED: XCircle,
  ACTIVE: CheckCircle,
  EXPIRED: XCircle,
}

const TIMELINE_STATUS_COLORS: Record<PolicyStatus, string> = {
  PENDING: 'text-yellow-500',
  APPROVED: 'text-green-500',
  REJECTED: 'text-red-500',
  ACTIVE: 'text-green-500',
  EXPIRED: 'text-gray-500',
}

const getStatusStyle = (status: PolicyStatus) => STATUS_STYLES[status]
const getStatusIcon = (status: PolicyStatus) => STATUS_ICONS[status]
const getStatusText = (status: PolicyStatus) => STATUS_TEXTS[status]

const getTimelineIcon = (status: PolicyStatus) => TIMELINE_ICONS[status]
const getTimelineTitle = (status: PolicyStatus) => TIMELINE_TITLES[status]
const getTimelineStatusIcon = (status: PolicyStatus) => TIMELINE_STATUS_ICONS[status]
const getTimelineStatusColor = (status: PolicyStatus) => TIMELINE_STATUS_COLORS[status]

const formatINR = (amount: number): string =>
  new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount)

const formatDate = (dateString: string): string =>
  new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })

const formatDateTime = (dateString: string): string =>
  new Date(dateString).toLocaleString('en-IN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>

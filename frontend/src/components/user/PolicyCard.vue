<!-- src/components/user/PolicyCard.vue -->
<template>
  <div
    v-if="userPolicy && userPolicy.policyType"
    class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 hover:shadow-md transition-all duration-300 hover:-translate-y-1"
  >
    <!-- Header with Status -->
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-3">
        <div
          class="w-12 h-12 bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl flex items-center justify-center"
        >
          <component :is="getPolicyTypeIcon(userPolicy.policyType)" class="w-6 h-6 text-blue-600" />
        </div>
        <div>
          <h3 class="font-bold text-slate-900 text-lg">
            {{ userPolicy.policyName || 'Unknown Policy' }}
          </h3>
          <p class="text-sm text-slate-500 font-medium">
            {{ userPolicy.policyType || 'Unknown' }} Insurance
          </p>
        </div>
      </div>

      <!-- Status Badge -->
      <div
        :class="getStatusStyle(userPolicy.status)"
        class="px-3 py-1.5 rounded-full text-xs font-semibold flex items-center shadow-sm"
      >
        <component :is="getStatusIcon(userPolicy.status)" class="w-3 h-3 mr-1.5" />
        {{ getStatusText(userPolicy.status) }}
      </div>
    </div>

    <!-- Expiration Warning for Active Policies -->
    <div v-if="userPolicy.status === 'ACTIVE' && isExpiringSoon(userPolicy.endDate)" class="mb-4">
      <div class="bg-orange-50 border border-orange-200 rounded-lg p-3">
        <div class="flex items-center">
          <AlertTriangle class="w-4 h-4 text-orange-600 mr-2" />
          <span class="text-sm text-orange-800 font-medium">
            Policy expires in {{ getDaysUntilExpiry(userPolicy.endDate) }} days - Consider renewal
          </span>
        </div>
      </div>
    </div>

    <!-- Coverage Amount & Premium -->
    <div class="grid grid-cols-2 gap-4 mb-6">
      <div
        class="bg-gradient-to-br from-emerald-50 to-emerald-100 rounded-xl p-4 border border-emerald-200"
      >
        <p
          class="text-xs text-emerald-600 uppercase tracking-wide font-semibold mb-2 flex items-center"
        >
          <Shield class="w-3 h-3 mr-1" />
          Coverage Amount
        </p>
        <p class="text-xl font-bold text-emerald-700">
          {{ formatINR(userPolicy.coverageAmount || 0) }}
        </p>
        <p class="text-xs text-emerald-600 mt-1">Full protection limit</p>
      </div>
      <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4 border border-blue-200">
        <p
          class="text-xs text-blue-600 uppercase tracking-wide font-semibold mb-2 flex items-center"
        >
          <TrendingUp class="w-3 h-3 mr-1" />
          Premium Paid
        </p>
        <p class="text-xl font-bold text-blue-700">{{ formatINR(userPolicy.premiumPaid || 0) }}</p>
        <p class="text-xs text-blue-600 mt-1">Investment secured</p>
      </div>
    </div>

    <!-- Claims Progress Bar -->
    <div v-if="userPolicy.status === 'ACTIVE'" class="mb-6">
      <div class="flex items-center justify-between mb-3">
        <span class="text-sm font-semibold text-slate-700 flex items-center">
          <BarChart3 class="w-4 h-4 mr-2" />
          Claims Utilization
        </span>
        <div class="text-right">
          <span class="text-sm font-bold text-slate-900">{{ claimsPercentage }}%</span>
          <span class="text-xs text-slate-500 block">of coverage used</span>
        </div>
      </div>

      <div class="relative">
        <!-- Progress Bar Background -->
        <div class="w-full bg-slate-200 rounded-full h-4 overflow-hidden shadow-inner">
          <!-- Progress Bar Fill -->
          <div
            :class="getProgressBarColor(claimsPercentage)"
            class="h-4 rounded-full transition-all duration-1000 ease-out relative overflow-hidden"
            :style="{ width: `${Math.min(claimsPercentage, 100)}%` }"
          >
            <!-- Progress Bar Shimmer Effect -->
            <div
              v-if="claimsPercentage > 0"
              class="absolute inset-0 bg-gradient-to-r from-transparent via-white/40 to-transparent shimmer"
            ></div>

            <!-- Progress Bar Stripes for High Usage -->
            <div v-if="claimsPercentage > 75" class="absolute inset-0 bg-stripes opacity-20"></div>
          </div>

          <!-- Progress Bar Glow Effect -->
          <div
            v-if="claimsPercentage > 0"
            :class="getProgressBarGlow(claimsPercentage)"
            class="absolute top-0 left-0 h-4 rounded-full blur-sm opacity-30"
            :style="{ width: `${Math.min(claimsPercentage, 100)}%` }"
          ></div>
        </div>

        <!-- Claims Amount Display -->
        <div class="flex justify-between text-sm font-medium text-slate-700 mt-3">
          <span
            class="bg-white px-3 py-1.5 rounded-lg border border-slate-200 shadow-sm flex items-center"
          >
            <span class="text-red-500 mr-1">💰</span>
            Claimed:
            {{ userPolicy.totalAmountClaimed ? formatINR(userPolicy.totalAmountClaimed) : '₹0' }}
          </span>
          <span
            class="bg-white px-3 py-1.5 rounded-lg border border-slate-200 shadow-sm flex items-center"
          >
            <span class="text-emerald-500 mr-1">💎</span>
            Available: {{ formatINR(availableCoverage) }}
          </span>
        </div>
      </div>

      <!-- Coverage Utilization Insights -->
      <div class="mt-4 p-3 bg-slate-50 rounded-lg border border-slate-200">
        <div class="flex items-center justify-between text-xs">
          <span class="text-slate-600 font-medium">Coverage Health Score</span>
          <div class="flex items-center space-x-2">
            <div class="flex space-x-1">
              <div
                v-for="i in 5"
                :key="i"
                :class="
                  i <= getCoverageHealthScore(claimsPercentage)
                    ? getCoverageHealthColor(getCoverageHealthScore(claimsPercentage))
                    : 'bg-slate-300'
                "
                class="w-1.5 h-4 rounded-full transition-colors duration-300"
              ></div>
            </div>
            <span
              :class="getCoverageHealthTextColor(getCoverageHealthScore(claimsPercentage))"
              class="font-bold text-xs"
            >
              {{ getCoverageHealthText(getCoverageHealthScore(claimsPercentage)) }}
            </span>
          </div>
        </div>

        <!-- Smart Recommendations -->
        <div class="mt-2">
          <p class="text-xs text-slate-600 leading-relaxed">
            {{ getCoverageRecommendation(claimsPercentage) }}
          </p>
        </div>
      </div>
    </div>

    <!-- Policy Timeline Info -->
    <div
      class="mb-4 p-4 bg-gradient-to-r from-slate-50 to-blue-50 rounded-xl border border-slate-200"
    >
      <div class="grid grid-cols-2 gap-4 text-sm">
        <div>
          <p class="text-slate-500 font-medium mb-1 flex items-center">
            <Hash class="w-3 h-3 mr-1" />
            Policy ID
          </p>
          <p class="font-bold text-slate-900">#{{ userPolicy.policyId || 'N/A' }}</p>
        </div>
        <div>
          <p class="text-slate-500 font-medium mb-1 flex items-center">
            <User class="w-3 h-3 mr-1" />
            Policy Holder
          </p>
          <p class="font-bold text-slate-900">{{ userPolicy.userName || 'Unknown' }}</p>
        </div>
        <div>
          <p class="text-slate-500 font-medium mb-1 flex items-center">
            <Calendar class="w-3 h-3 mr-1" />
            Started
          </p>
          <p class="font-bold text-slate-900">
            {{ userPolicy.startDate ? formatDate(userPolicy.startDate) : 'N/A' }}
          </p>
        </div>
        <div>
          <p class="text-slate-500 font-medium mb-1 flex items-center">
            <CalendarX class="w-3 h-3 mr-1" />
            Expires
          </p>
          <p
            :class="userPolicy.endDate ? getExpiryDateClass(userPolicy.endDate) : ''"
            class="font-bold"
          >
            {{ userPolicy.endDate ? formatDate(userPolicy.endDate) : 'N/A' }}
          </p>
        </div>
      </div>

      <!-- Policy Progress Timeline -->
      <div
        v-if="userPolicy.status === 'ACTIVE' && userPolicy.startDate && userPolicy.endDate"
        class="mt-4"
      >
        <div class="flex justify-between text-xs text-slate-500 mb-2">
          <span class="font-medium">Policy Term Progress</span>
          <span class="font-bold">{{ policyProgressPercentage }}% complete</span>
        </div>
        <div class="w-full bg-slate-300 rounded-full h-2 overflow-hidden">
          <div
            class="bg-gradient-to-r from-blue-500 to-blue-600 h-2 rounded-full transition-all duration-700 ease-out"
            :style="{ width: `${policyProgressPercentage}%` }"
          ></div>
        </div>
        <div class="flex justify-between text-xs text-slate-500 mt-1">
          <span>{{ formatDate(userPolicy.startDate) }}</span>
          <span class="font-medium">{{ getTimeRemaining(userPolicy.endDate) }}</span>
        </div>
      </div>
    </div>

    <!-- Action Buttons -->
    <div class="flex space-x-2 mb-4">
      <AppButton
        variant="primary"
        size="medium"
        @click="$emit('view', userPolicy)"
        class="flex-1 font-semibold"
      >
        View Details
      </AppButton>
      <AppButton
        v-if="userPolicy.status === 'ACTIVE'"
        variant="secondary"
        size="medium"
        @click="$emit('makeClaim', userPolicy)"
        class="flex-1 font-semibold"
        :disabled="claimsPercentage >= 100"
      >
        {{ claimsPercentage >= 100 ? 'Coverage Maxed' : 'File Claim' }}
      </AppButton>
    </div>

    <!-- Renewal Call-to-Action for Expiring Policies -->
    <div v-if="userPolicy.status === 'ACTIVE' && isExpiringSoon(userPolicy.endDate)" class="mb-4">
      <div
        class="bg-gradient-to-r from-orange-50 to-yellow-50 border border-orange-200 rounded-lg p-4"
      >
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center">
              <RefreshCw class="w-5 h-5 text-orange-600" />
            </div>
            <div>
              <h4 class="font-semibold text-orange-900">Renewal Available</h4>
              <p class="text-sm text-orange-700">
                Expires in {{ getDaysUntilExpiry(userPolicy.endDate) }} days
              </p>
            </div>
          </div>
          <AppButton
            variant="ghost"
            size="small"
            @click="confirmRenewal"
            :disabled="isRenewing"
            class="flex items-center space-x-2"
          >
            <span v-if="!isRenewing" class="flex items-center">
              <RefreshCw class="w-4 h-4 mr-1" />
              Renew Now
            </span>
            <span v-else class="flex items-center">
              <div
                class="w-4 h-4 border-2 border-orange-600 border-t-transparent rounded-full animate-spin mr-1"
              ></div>
              Renewing...
            </span>
          </AppButton>
        </div>
      </div>
    </div>

    <!-- Status Timeline -->
    <div class="pt-4 border-t border-slate-200">
      <div
        v-if="userPolicy.status === 'PENDING'"
        class="flex items-center text-sm text-amber-600 bg-amber-50 p-3 rounded-lg"
      >
        <Clock class="w-4 h-4 mr-2 flex-shrink-0" />
        <span class="font-medium"
          >Application under review... Expected approval within 2-3 business days.</span
        >
      </div>

      <div v-else-if="userPolicy.status === 'ACTIVE'" class="space-y-3">
        <div class="flex items-center justify-between text-sm bg-emerald-50 p-3 rounded-lg">
          <div class="flex items-center text-emerald-700">
            <CheckCircle class="w-4 h-4 mr-2" />
            <span class="font-semibold">Policy Active & Protected</span>
          </div>
          <div
            v-if="userPolicy.endDate && isExpiringSoon(userPolicy.endDate)"
            class="flex items-center text-amber-600 bg-amber-100 px-2 py-1 rounded-full"
          >
            <AlertTriangle class="w-3 h-3 mr-1" />
            <span class="text-xs font-bold"
              >Expires in {{ getDaysUntilExpiry(userPolicy.endDate) }} days</span
            >
          </div>
        </div>
      </div>

      <div
        v-else-if="userPolicy.status === 'APPROVED'"
        class="flex items-center text-sm text-emerald-600 bg-emerald-50 p-3 rounded-lg"
      >
        <CheckCircle class="w-4 h-4 mr-2" />
        <span class="font-medium">Policy approved! Activation will begin shortly.</span>
      </div>

      <div v-else-if="userPolicy.status === 'REJECTED'" class="space-y-2">
        <div class="flex items-center text-sm text-red-600 bg-red-50 p-3 rounded-lg">
          <XCircle class="w-4 h-4 mr-2" />
          <span class="font-medium">Application was rejected. Contact support for details.</span>
        </div>
        <AppButton
          variant="primary"
          size="small"
          @click="$emit('reapply', userPolicy)"
          class="w-full"
        >
          Submit New Application
        </AppButton>
      </div>

      <div v-else-if="userPolicy.status === 'EXPIRED'" class="space-y-2">
        <div class="flex items-center text-sm text-slate-600 bg-slate-50 p-3 rounded-lg">
          <AlertTriangle class="w-4 h-4 mr-2" />
          <span class="font-medium"
            >Policy expired {{ userPolicy.endDate ? getTimeAgo(userPolicy.endDate) : '' }}</span
          >
        </div>
        <AppButton
          variant="primary"
          size="small"
          @click="$emit('renew', userPolicy)"
          class="w-full"
          :disabled="isRenewing"
        >
          <span v-if="!isRenewing" class="flex items-center justify-center">
            <RefreshCw class="w-4 h-4 mr-2" />
            Renew Policy
          </span>
          <span v-else class="flex items-center justify-center">
            <div
              class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"
            ></div>
            Renewing...
          </span>
        </AppButton>
      </div>
    </div>

    <!-- Renewal Confirmation Modal -->
    <div v-if="showRenewalModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div
        class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0"
      >
        <!-- Background overlay -->
        <div
          class="fixed inset-0 transition-opacity bg-slate-500 bg-opacity-75"
          @click="closeRenewalModal"
        ></div>

        <!-- Modal -->
        <div
          class="inline-block w-full max-w-lg p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-xl"
        >
          <!-- Header -->
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <RefreshCw class="w-5 h-5 text-green-600" />
              </div>
              <div>
                <h3 class="text-lg font-semibold text-slate-900">Confirm Renewal</h3>
                <span class="text-sm text-slate-500">{{ userPolicy.policyName }}</span>
              </div>
            </div>
            <button @click="closeRenewalModal" class="text-slate-400 hover:text-slate-600">
              <X class="w-5 h-5" />
            </button>
          </div>

          <!-- Renewal Details -->
          <div class="bg-green-50 border border-green-200 rounded-lg p-4 mb-4">
            <div class="text-sm space-y-2">
              <div class="flex justify-between">
                <span class="text-green-700">Renewal Premium:</span>
                <span class="font-semibold text-green-900">{{
                  formatINR(userPolicy.premiumPaid)
                }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-green-700">New Term:</span>
                <span class="font-semibold text-green-900">12 Months</span>
              </div>
              <div class="flex justify-between">
                <span class="text-green-700">Coverage:</span>
                <span class="font-semibold text-green-900">{{
                  formatINR(userPolicy.coverageAmount)
                }}</span>
              </div>
            </div>
          </div>

          <div class="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-6">
            <p class="text-sm text-blue-800">
              Your policy will be renewed for another 12 months with continuous coverage and no gaps
              in protection.
            </p>
          </div>

          <!-- Footer -->
          <div class="flex justify-end space-x-3">
            <AppButton variant="ghost" @click="closeRenewalModal"> Cancel </AppButton>
            <AppButton variant="primary" @click="confirmRenewal" :disabled="isRenewing">
              <span v-if="!isRenewing">Confirm Renewal</span>
              <span v-else class="flex items-center">
                <div
                  class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"
                ></div>
                Processing...
              </span>
            </AppButton>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Error Fallback -->
  <div v-else-if="userPolicy" class="bg-red-50 border border-red-200 rounded-xl p-6">
    <div class="flex items-center mb-4">
      <AlertTriangle class="w-6 h-6 text-red-600 mr-3" />
      <h3 class="font-semibold text-red-900">Invalid Policy Data</h3>
    </div>
    <p class="text-red-700 text-sm">
      This policy has incomplete data. Please contact support for assistance.
    </p>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useToast } from '@/composables/useToast'
import { usePolicyRenewalStore } from '@/stores/policyRenewal'
import { useAuthStore } from '@/stores/auth'
import {
  Clock,
  CheckCircle,
  XCircle,
  AlertTriangle,
  Shield,
  Heart,
  Car,
  Home,
  FileText,
  Plus,
  Calendar,
  CalendarX,
  RefreshCw,
  TrendingUp,
  BarChart3,
  Hash,
  User,
  X,
} from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { UserPolicy } from '@/stores/userPolicy'

interface Props {
  userPolicy: UserPolicy
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'view', userPolicy: UserPolicy): void
  (e: 'makeClaim', userPolicy: UserPolicy): void
  (e: 'reapply', userPolicy: UserPolicy): void
  (e: 'renew', userPolicy: UserPolicy): void
}>()

const toast = useToast()
const policyRenewalStore = usePolicyRenewalStore()
const authStore = useAuthStore()
const showRenewalModal = ref(false)
const isRenewing = ref(false)

// Computed values with null safety
const availableCoverage = computed(() => {
  if (!props.userPolicy) return 0
  const claimed = props.userPolicy.totalAmountClaimed || 0
  const coverage = props.userPolicy.coverageAmount || 0
  return Math.max(0, coverage - claimed)
})

const claimsPercentage = computed(() => {
  if (!props.userPolicy) return 0
  const claimed = props.userPolicy.totalAmountClaimed || 0
  const coverage = props.userPolicy.coverageAmount || 0
  if (coverage === 0) return 0
  return Math.round((claimed / coverage) * 100)
})

const policyProgressPercentage = computed(() => {
  if (!props.userPolicy?.startDate || !props.userPolicy?.endDate) return 0

  const start = new Date(props.userPolicy.startDate).getTime()
  const end = new Date(props.userPolicy.endDate).getTime()
  const now = new Date().getTime()

  if (now < start) return 0
  if (now > end) return 100

  const totalDuration = end - start
  const elapsed = now - start

  return Math.round((elapsed / totalDuration) * 100)
})

const closeRenewalModal = () => {
  showRenewalModal.value = false
}

const confirmRenewal = async () => {
  if (!props.userPolicy?.policyId) return

  isRenewing.value = true
  try {
    // Use the store method instead of direct API call
    const renewedPolicy = await policyRenewalStore.renewPolicy(props.userPolicy.policyId)

    toast.success(
      'Policy Renewed Successfully!',
      `${renewedPolicy.policyName} has been renewed until ${formatDate(renewedPolicy.endDate)}`,
    )

    // Emit the renew event with the updated policy to refresh parent component
    emit('renew', renewedPolicy as UserPolicy)
    closeRenewalModal()
  } catch (error) {
    toast.error('Renewal Failed', 'Failed to renew your policy. Please try again.')
    console.error('Renewal error:', error)
  } finally {
    isRenewing.value = false
  }
}

// Helper functions with null safety
const getDaysUntilExpiry = (endDate: string): number => {
  if (!endDate) return 0
  return Math.ceil((new Date(endDate).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24))
}

const getPolicyTypeIcon = (type: string) => {
  const icons = {
    Health: Heart,
    Auto: Car,
    Life: Shield,
    Home: Home,
    health: Heart,
    auto: Car,
    life: Shield,
    home: Home,
  }
  return icons[type as keyof typeof icons] || FileText
}

const getProgressBarColor = (percentage: number) => {
  if (percentage === 0) return 'bg-slate-400'
  if (percentage <= 25) return 'bg-gradient-to-r from-emerald-500 to-emerald-600'
  if (percentage <= 50) return 'bg-gradient-to-r from-blue-500 to-blue-600'
  if (percentage <= 75) return 'bg-gradient-to-r from-amber-500 to-amber-600'
  return 'bg-gradient-to-r from-red-500 to-red-600'
}

const getProgressBarGlow = (percentage: number) => {
  if (percentage <= 25) return 'bg-emerald-400'
  if (percentage <= 50) return 'bg-blue-400'
  if (percentage <= 75) return 'bg-amber-400'
  return 'bg-red-400'
}

const getCoverageHealthScore = (percentage: number): number => {
  if (percentage <= 20) return 5
  if (percentage <= 40) return 4
  if (percentage <= 60) return 3
  if (percentage <= 80) return 2
  return 1
}

const getCoverageHealthColor = (score: number) => {
  const colors = {
    5: 'bg-emerald-500',
    4: 'bg-green-500',
    3: 'bg-amber-500',
    2: 'bg-orange-500',
    1: 'bg-red-500',
  }
  return colors[score as keyof typeof colors]
}

const getCoverageHealthTextColor = (score: number) => {
  const colors = {
    5: 'text-emerald-600',
    4: 'text-green-600',
    3: 'text-amber-600',
    2: 'text-orange-600',
    1: 'text-red-600',
  }
  return colors[score as keyof typeof colors]
}

const getCoverageHealthText = (score: number) => {
  const texts = {
    5: 'Excellent',
    4: 'Good',
    3: 'Fair',
    2: 'Poor',
    1: 'Critical',
  }
  return texts[score as keyof typeof texts]
}

const getCoverageRecommendation = (percentage: number): string => {
  if (percentage === 0) return '🎯 No claims yet! Your coverage is fully available for any needs.'
  if (percentage <= 25)
    return '✨ Great usage! You have plenty of coverage remaining for future needs.'
  if (percentage <= 50)
    return '⚡ Moderate usage. Consider planning for remaining coverage throughout the policy term.'
  if (percentage <= 75)
    return '⚠️ High usage detected. Monitor remaining coverage carefully to avoid exceeding limits.'
  if (percentage < 100)
    return '🚨 Coverage nearly exhausted! Consider upgrading policy or managing future claims carefully.'
  return '❌ Coverage fully utilized. No additional claims can be processed under this policy.'
}

const getStatusStyle = (status: string) => {
  const styles = {
    PENDING: 'bg-gradient-to-r from-amber-100 to-amber-200 text-amber-800 border border-amber-300',
    APPROVED:
      'bg-gradient-to-r from-emerald-100 to-emerald-200 text-emerald-800 border border-emerald-300',
    REJECTED: 'bg-gradient-to-r from-red-100 to-red-200 text-red-800 border border-red-300',
    ACTIVE: 'bg-gradient-to-r from-blue-100 to-blue-200 text-blue-800 border border-blue-300',
    EXPIRED: 'bg-gradient-to-r from-slate-100 to-slate-200 text-slate-800 border border-slate-300',
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
    PENDING: 'Under Review',
    APPROVED: 'Approved',
    REJECTED: 'Rejected',
    ACTIVE: 'Active',
    EXPIRED: 'Expired',
  }
  return texts[status as keyof typeof texts] || status
}

const getExpiryDateClass = (endDate: string) => {
  const daysUntilExpiry = getDaysUntilExpiry(endDate)

  if (daysUntilExpiry <= 30) return 'text-red-600 font-bold'
  if (daysUntilExpiry <= 90) return 'text-amber-600 font-semibold'
  return 'text-slate-900'
}

const isExpiringSoon = (endDate: string): boolean => {
  const daysUntilExpiry = getDaysUntilExpiry(endDate)
  return daysUntilExpiry <= 45 && daysUntilExpiry > 0
}

const getTimeRemaining = (endDate: string): string => {
  if (!endDate) return 'N/A'

  const now = new Date()
  const end = new Date(endDate)
  const diffTime = end.getTime() - now.getTime()

  if (diffTime <= 0) return 'Expired'

  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  if (diffDays === 1) return '1 day left'
  if (diffDays < 30) return `${diffDays} days left`

  const diffMonths = Math.round(diffDays / 30)
  if (diffMonths === 1) return '1 month left'
  if (diffMonths < 12) return `${diffMonths} months left`

  const diffYears = Math.floor(diffMonths / 12)
  const remainingMonths = diffMonths % 12

  if (remainingMonths === 0) return `${diffYears} year${diffYears > 1 ? 's' : ''} left`
  return `${diffYears}y ${remainingMonths}m left`
}

const getTimeAgo = (dateString: string): string => {
  if (!dateString) return ''

  const date = new Date(dateString)
  const now = new Date()
  const diffTime = now.getTime() - date.getTime()
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))

  if (diffDays === 0) return 'today'
  if (diffDays === 1) return 'yesterday'
  if (diffDays < 30) return `${diffDays} days ago`

  const diffMonths = Math.floor(diffDays / 30)
  if (diffMonths === 1) return '1 month ago'
  if (diffMonths < 12) return `${diffMonths} months ago`

  const diffYears = Math.floor(diffMonths / 12)
  return `${diffYears} year${diffYears > 1 ? 's' : ''} ago`
}

const formatINR = (amount: number): string => {
  if (amount >= 10000000000) {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
      notation: 'compact',
      compactDisplay: 'short',
    }).format(amount)
  }

  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount)
}

const formatDate = (dateString: string): string => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}
</script>

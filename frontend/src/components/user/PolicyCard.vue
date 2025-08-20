<template>
  <div
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
          <h3 class="font-bold text-slate-900 text-lg">{{ userPolicy.policyName }}</h3>
          <p class="text-sm text-slate-500 font-medium">{{ userPolicy.policyType }} Insurance</p>
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
          {{ formatINR(userPolicy.coverageAmount) }}
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
        <p class="text-xl font-bold text-blue-700">{{ formatINR(userPolicy.premiumPaid) }}</p>
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
          <p class="font-bold text-slate-900">#{{ userPolicy.policyId }}</p>
        </div>
        <div>
          <p class="text-slate-500 font-medium mb-1 flex items-center">
            <User class="w-3 h-3 mr-1" />
            Policy Holder
          </p>
          <p class="font-bold text-slate-900">{{ userPolicy.userName }}</p>
        </div>
        <div>
          <p class="text-slate-500 font-medium mb-1 flex items-center">
            <Calendar class="w-3 h-3 mr-1" />
            Started
          </p>
          <p class="font-bold text-slate-900">{{ formatDate(userPolicy.startDate) }}</p>
        </div>
        <div>
          <p class="text-slate-500 font-medium mb-1 flex items-center">
            <CalendarX class="w-3 h-3 mr-1" />
            Expires
          </p>
          <p :class="getExpiryDateClass(userPolicy.endDate)" class="font-bold">
            {{ formatDate(userPolicy.endDate) }}
          </p>
        </div>
      </div>

      <!-- Policy Progress Timeline -->
      <div v-if="userPolicy.status === 'ACTIVE'" class="mt-4">
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

    <!-- Contact Information -->
    <div class="mb-4 p-3 bg-blue-50 rounded-lg border border-blue-200">
      <div class="grid grid-cols-2 gap-3 text-xs">
        <div class="flex items-center">
          <Mail class="w-3 h-3 text-blue-600 mr-2" />
          <span class="text-blue-700 font-medium truncate">{{ userPolicy.userEmail }}</span>
        </div>
        <div class="flex items-center">
          <Phone class="w-3 h-3 text-blue-600 mr-2" />
          <span class="text-blue-700 font-medium">{{ userPolicy.userPhone }}</span>
        </div>
      </div>
      <div class="mt-2 flex items-start">
        <MapPin class="w-3 h-3 text-blue-600 mr-2 mt-0.5 flex-shrink-0" />
        <span class="text-blue-700 font-medium text-xs leading-relaxed">{{
          userPolicy.userAddress
        }}</span>
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
        <FileText class="w-4 h-4 mr-2" />
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
        <Plus class="w-4 h-4 mr-2" />
        {{ claimsPercentage >= 100 ? 'Coverage Maxed' : 'File Claim' }}
      </AppButton>
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
            v-if="isExpiringSoon(userPolicy.endDate)"
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
          <span class="font-medium">Policy expired {{ getTimeAgo(userPolicy.endDate) }}</span>
        </div>
        <AppButton
          variant="primary"
          size="small"
          @click="$emit('renew', userPolicy)"
          class="w-full"
        >
          <RefreshCw class="w-4 h-4 mr-2" />
          Renew Policy
        </AppButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
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
  Mail,
  Phone,
  MapPin,
} from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { UserPolicy } from '@/stores/userPolicy'

interface Props {
  userPolicy: UserPolicy
}

const props = defineProps<Props>()

defineEmits<{
  (e: 'view', userPolicy: UserPolicy): void
  (e: 'makeClaim', userPolicy: UserPolicy): void
  (e: 'reapply', userPolicy: UserPolicy): void
  (e: 'renew', userPolicy: UserPolicy): void
}>()

// Direct calculations using userPolicy data (no external store dependency)
const availableCoverage = computed(() => {
  const claimed = props.userPolicy.totalAmountClaimed || 0
  return Math.max(0, props.userPolicy.coverageAmount - claimed)
})

const claimsPercentage = computed(() => {
  const claimed = props.userPolicy.totalAmountClaimed || 0
  if (props.userPolicy.coverageAmount === 0) return 0
  return Math.round((claimed / props.userPolicy.coverageAmount) * 100)
})

// Policy term progress
const policyProgressPercentage = computed(() => {
  const start = new Date(props.userPolicy.startDate).getTime()
  const end = new Date(props.userPolicy.endDate).getTime()
  const now = new Date().getTime()

  if (now < start) return 0
  if (now > end) return 100

  const totalDuration = end - start
  const elapsed = now - start

  return Math.round((elapsed / totalDuration) * 100)
})

// Helper functions
const getDaysUntilExpiry = (endDate: string): number => {
  return Math.ceil((new Date(endDate).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24))
}

const getPolicyTypeIcon = (type: string) => {
  const icons = {
    Health: Heart,
    Auto: Car,
    Life: Shield,
    Home: Home,
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
  if (percentage === 0)
    return '🎯 No claims yet! Your coverage is fully available for any medical needs.'
  if (percentage <= 25)
    return '✨ Great usage! You have plenty of coverage remaining for future healthcare needs.'
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
  // Handle large amounts properly
  if (amount >= 10000000000) {
    // 1000 crores
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
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}
</script>

<style scoped>
@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.shimmer {
  animation: shimmer 2.5s infinite linear;
}

.bg-stripes {
  background-image: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 4px,
    rgba(255, 255, 255, 0.1) 4px,
    rgba(255, 255, 255, 0.1) 8px
  );
}
</style>

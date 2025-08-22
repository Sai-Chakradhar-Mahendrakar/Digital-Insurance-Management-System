<template>
  <div class="min-h-screen bg-slate-50">
    <AppNavbar />

    <main class="max-w-7xl mx-auto px-4 lg:px-8 py-8">
      <!-- Header with Navigation -->
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">My Policies</h1>
          <p class="text-slate-600 font-inter">Track your insurance policies and their status</p>
        </div>

        <!-- Quick Actions -->
        <div class="flex space-x-3">
          <button
            @click="refreshPolicies"
            :disabled="isLoading"
            class="inline-flex items-center px-4 py-2 bg-slate-200 text-slate-700 rounded-lg hover:bg-slate-300 transition-colors disabled:opacity-50"
          >
            <RefreshCw :class="{ 'animate-spin': isLoading }" class="w-4 h-4 mr-2" />
            Refresh
          </button>

          <router-link
            to="/policies"
            class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Plus class="w-4 h-4 mr-2" />
            Browse More Policies
          </router-link>
        </div>
      </div>

      <!-- Policy Statistics -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
              <Shield class="w-5 h-5 text-blue-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-slate-600">Total Policies</p>
              <p class="text-2xl font-bold text-slate-900">{{ userPolicies.length }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-10 h-10 bg-yellow-100 rounded-lg flex items-center justify-center">
              <Clock class="w-5 h-5 text-yellow-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-slate-600">Pending</p>
              <p class="text-2xl font-bold text-slate-900">{{ pendingCount }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
              <CheckCircle class="w-5 h-5 text-green-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-slate-600">Active</p>
              <p class="text-2xl font-bold text-slate-900">{{ activeCount }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
              <IndianRupee class="w-5 h-5 text-purple-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-slate-600">Total Premium</p>
              <p class="text-lg font-bold text-slate-900">{{ formatINR(totalPremium) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Error Display -->
      <div v-if="errorMessage" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
        <div class="flex items-center">
          <AlertCircle class="w-5 h-5 text-red-600 mr-3" />
          <p class="text-red-800 text-sm font-medium">{{ errorMessage }}</p>
          <button @click="clearError" class="ml-auto text-red-600 hover:text-red-800">
            <X class="w-4 h-4" />
          </button>
        </div>
      </div>

      <!-- Success Message -->
      <div v-if="successMessage" class="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg">
        <div class="flex items-center">
          <CheckCircle class="w-5 h-5 text-green-600 mr-3" />
          <p class="text-green-800 text-sm font-medium">{{ successMessage }}</p>
          <button @click="clearSuccess" class="ml-auto text-green-600 hover:text-green-800">
            <X class="w-4 h-4" />
          </button>
        </div>
      </div>

      <!-- Filter and Sort Options -->
      <div
        v-if="userPolicies.length > 0"
        class="mb-6 flex flex-wrap items-center justify-between gap-4"
      >
        <div class="flex items-center space-x-4">
          <!-- Status Filter -->
          <select
            v-model="statusFilter"
            class="px-3 py-2 border border-slate-200 rounded-lg text-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          >
            <option value="">All Status</option>
            <option value="PENDING">Pending</option>
            <option value="ACTIVE">Active</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
            <option value="EXPIRED">Expired</option>
          </select>

          <!-- Type Filter -->
          <select
            v-model="typeFilter"
            class="px-3 py-2 border border-slate-200 rounded-lg text-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          >
            <option value="">All Types</option>
            <option value="Health">Health</option>
            <option value="Life">Life</option>
            <option value="Auto">Auto</option>
            <option value="Home">Home</option>
            <option value="Travel">Travel</option>
          </select>
        </div>

        <div class="text-sm text-slate-600">
          Showing {{ filteredPolicies.length }} of {{ userPolicies.length }} policies
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center py-12">
        <div class="flex flex-col items-center">
          <div
            class="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"
          ></div>
          <p class="text-slate-600">Loading your policies...</p>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="userPolicies.length === 0" class="text-center py-12">
        <div
          class="w-24 h-24 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-6"
        >
          <Shield class="w-12 h-12 text-slate-400" />
        </div>
        <h3 class="text-xl font-semibold text-slate-900 mb-2">No Policies Yet</h3>
        <p class="text-slate-600 mb-6">You haven't purchased any insurance policies yet.</p>
        <div class="space-y-3">
          <router-link
            to="/policies"
            class="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Shield class="w-4 h-4 mr-2" />
            Browse Policies
          </router-link>
          <div>
            <button
              @click="refreshPolicies"
              class="text-blue-600 hover:text-blue-700 text-sm font-medium"
            >
              Or refresh to check for updates
            </button>
          </div>
        </div>
      </div>

      <!-- No Filtered Results -->
      <div v-else-if="filteredPolicies.length === 0" class="text-center py-12">
        <Search class="w-16 h-16 text-slate-300 mx-auto mb-6" />
        <h3 class="text-lg font-semibold text-slate-900 mb-2">No Matching Policies</h3>
        <p class="text-slate-600 mb-6">No policies match your current filter criteria.</p>
        <button
          @click="clearFilters"
          class="inline-flex items-center px-4 py-2 bg-slate-600 text-white rounded-lg hover:bg-slate-700 transition-colors"
        >
          Clear Filters
        </button>
      </div>

      <!-- Policies Grid -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <PolicyCard
          v-for="policy in paginatedPolicies"
          :key="policy.id"
          :user-policy="policy"
          @view=""
          @make-claim="handleMakeClaim"
          @reapply="handleReapply"
          @renew="handlePolicyRenewed"
        />
      </div>

      <!-- Pagination -->
      <div v-if="filteredPolicies.length > 10" class="mt-8 flex justify-center">
        <nav class="flex items-center space-x-2">
          <button
            v-for="page in totalPages"
            :key="page"
            @click="currentPage = page"
            :class="[
              'px-3 py-2 text-sm font-medium rounded-md transition-colors',
              currentPage === page
                ? 'bg-blue-600 text-white'
                : 'bg-white text-slate-700 hover:bg-slate-100 border border-slate-200',
            ]"
          >
            {{ page }}
          </button>
        </nav>
      </div>
    </main>

    <!-- Policy Details Modal -->
    <PolicyDetailsModal
      v-if="selectedUserPolicy"
      :userPolicy="selectedUserPolicy"
      @close="selectedUserPolicy = null"
    />

    <!-- Submit Claim Modal -->
    <SubmitClaimModal
      v-if="selectedUserPolicyForClaim"
      :userPolicy="selectedUserPolicyForClaim"
      @close="selectedUserPolicyForClaim = null"
      @submitted="handleClaimSubmitted"
    />

    <!-- Toast Container -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useUserPolicyStore } from '@/stores/userPolicy'
import { useToast } from '@/composables/useToast'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import PolicyCard from '@/components/user/PolicyCard.vue'
import PolicyDetailsModal from '@/components/user/PolicyDetailsModal.vue'
import SubmitClaimModal from '@/components/claims/SubmitClaimModal.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import {
  Shield,
  Clock,
  CheckCircle,
  IndianRupee,
  Plus,
  RefreshCw,
  AlertCircle,
  X,
  Search,
} from 'lucide-vue-next'
import type { UserPolicy } from '@/stores/userPolicy'
import type { Claim } from '@/stores/claims'
import { onUnmounted } from 'vue'

const userPolicyStore = useUserPolicyStore()
const toast = useToast()

const selectedUserPolicy = ref<UserPolicy | null>(null)
const selectedUserPolicyForClaim = ref<UserPolicy | null>(null)
const errorMessage = ref('')
const successMessage = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const currentPage = ref(1)
const itemsPerPage = 10

const userPolicies = computed(() => userPolicyStore.userPolicies || [])
const isLoading = computed(() => userPolicyStore.isLoading)

const pendingCount = computed(() => userPolicies.value.filter((p) => p.status === 'PENDING').length)
const activeCount = computed(() => userPolicies.value.filter((p) => p.status === 'ACTIVE').length)
const totalPremium = computed(() => userPolicies.value.reduce((sum, p) => sum + p.premiumPaid, 0))

// Filtered policies based on status and type filters
const filteredPolicies = computed(() => {
  let filtered = [...userPolicies.value]

  // Status filter
  if (statusFilter.value) {
    filtered = filtered.filter((policy) => policy.status === statusFilter.value)
  }

  // Type filter
  if (typeFilter.value) {
    filtered = filtered.filter((policy) => policy.policyType === typeFilter.value)
  }

  return filtered
})

const handlePolicyRenewed = async (renewedPolicy: UserPolicy) => {
  // Refresh the policies list to show updated data
  try {
    await userPolicyStore.fetchUserPolicies()
  } catch (error) {
    console.error('Failed to refresh policies after renewal:', error)
  }
}

// Pagination
const totalPages = computed(() => Math.ceil(filteredPolicies.value.length / itemsPerPage))
const paginatedPolicies = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredPolicies.value.slice(start, end)
})

const formatINR = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount)
}

const viewPolicyDetails = (userPolicy: UserPolicy) => {
  selectedUserPolicy.value = userPolicy
}

const handleMakeClaim = (userPolicy: UserPolicy) => {
  selectedUserPolicyForClaim.value = userPolicy
}

const handleReapply = (userPolicy: UserPolicy) => {
  toast.info(
    'Reapplication',
    `Reapplication process for ${userPolicy.policyName} will be available soon.`,
  )
}

const handleRenew = (userPolicy: UserPolicy) => {
  toast.info(
    'Policy Renewal',
    `Renewal process for ${userPolicy.policyName} will be available soon.`,
  )
}

const handleClaimSubmitted = (claim: Claim) => {
  selectedUserPolicyForClaim.value = null
  toast.success(
    'Claim Submitted Successfully',
    `Your claim for ${formatINR(claim.claimAmount)} has been submitted for ${claim.policyName} and is under review.`,
  )

  // Optionally refresh policies to get updated claim amounts
  refreshPolicies()
}

const refreshPolicies = async () => {
  try {
    errorMessage.value = ''
    await userPolicyStore.fetchUserPolicies()
    successMessage.value = 'Policies refreshed successfully'

    // Clear success message after 3 seconds
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } catch (error) {
    if (typeof error === 'object' && error !== null) {
      const err = error as any
      errorMessage.value =
        err.response?.data?.errorMessage || err.message || 'Failed to load policies'
    }
    toast.error('Refresh Failed', errorMessage.value)
  }
}

const clearError = () => {
  errorMessage.value = ''
}

const clearSuccess = () => {
  successMessage.value = ''
}

const clearFilters = () => {
  statusFilter.value = ''
  typeFilter.value = ''
  currentPage.value = 1
}

// Watch for filter changes to reset pagination
watch([statusFilter, typeFilter], () => {
  currentPage.value = 1
})

// Load policies on component mount
onMounted(async () => {
  try {
    await userPolicyStore.fetchUserPolicies()
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to load your policies'
    errorMessage.value = message
    toast.error('Loading Failed', message)
  }
})

// Auto-refresh policies every 30 seconds when component is active
const refreshInterval = ref<number | null>(null)

onMounted(() => {
  refreshInterval.value = setInterval(async () => {
    if (document.visibilityState === 'visible') {
      try {
        await userPolicyStore.fetchUserPolicies()
      } catch (error) {
        console.warn('Background refresh failed:', error)
      }
    }
  }, 30000)
})

onUnmounted(() => {
  if (refreshInterval.value !== null) {
    clearInterval(refreshInterval.value)
  }
})
</script>

<style scoped>
/* Custom animations for loading states */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.policy-card {
  animation: slideIn 0.3s ease-out;
}

/* Hover effects for interactive elements */
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

/* Focus styles for accessibility */
select:focus,
button:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}
</style>

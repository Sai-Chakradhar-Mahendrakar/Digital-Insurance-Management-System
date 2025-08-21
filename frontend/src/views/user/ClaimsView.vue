<template>
  <div class="min-h-screen bg-slate-50">
    <AppNavbar />

    <main class="max-w-7xl mx-auto px-4 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">My Claims</h1>
        <p class="text-slate-600 font-inter">Track and manage your insurance claims</p>
      </div>

      <!-- Claims Statistics -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <FileText class="w-6 h-6 text-blue-600" />
            </div>
            <div class="ml-4">
              <p class="text-2xl font-bold text-slate-900">{{ totalClaims }}</p>
              <p class="text-sm text-slate-500">Total Claims</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <CheckCircle class="w-6 h-6 text-green-600" />
            </div>
            <div class="ml-4">
              <p class="text-2xl font-bold text-slate-900">{{ approvedClaims }}</p>
              <p class="text-sm text-slate-500">Approved</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
              <Clock class="w-6 h-6 text-yellow-600" />
            </div>
            <div class="ml-4">
              <p class="text-2xl font-bold text-slate-900">{{ pendingClaims }}</p>
              <p class="text-sm text-slate-500">Pending</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-emerald-100 rounded-lg flex items-center justify-center">
              <TrendingUp class="w-6 h-6 text-emerald-600" />
            </div>
            <div class="ml-4">
              <p class="text-lg font-bold text-slate-900">{{ formatINR(totalClaimAmount) }}</p>
              <p class="text-sm text-slate-500">Total Claimed</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Claims List -->
      <div class="bg-white rounded-xl shadow-sm border border-slate-200">
        <div class="p-6 border-b border-slate-200">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-slate-900">Claims History</h3>
            <AppButton variant="ghost" @click="refreshClaims" :disabled="isLoading">
              <RefreshCw :class="{ 'animate-spin': isLoading }" class="w-4 h-4 mr-2" />
              Refresh
            </AppButton>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="flex items-center justify-center py-12">
          <div class="flex flex-col items-center">
            <div
              class="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"
            ></div>
            <p class="text-slate-600">Loading your claims...</p>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="claims.length === 0" class="text-center py-12">
          <FileText class="w-16 h-16 text-slate-300 mx-auto mb-4" />
          <h3 class="text-lg font-semibold text-slate-900 mb-2">No Claims Found</h3>
          <p class="text-slate-600 mb-6">You haven't submitted any insurance claims yet.</p>
        </div>

        <!-- Claims List -->
        <div v-else class="divide-y divide-slate-200">
          <div
            v-for="claim in claims"
            :key="claim.id"
            class="p-6 hover:bg-slate-50 transition-colors duration-200"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center space-x-3 mb-2">
                  <h4 class="font-medium text-slate-900">Claim #{{ claim.id }}</h4>
                  <span
                    :class="getStatusStyle(claim.status)"
                    class="px-2 py-1 rounded-full text-xs font-medium"
                  >
                    {{ getStatusText(claim.status) }}
                  </span>
                </div>

                <p class="text-slate-600 text-sm mb-3">{{ claim.reason }}</p>

                <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-xs text-slate-500 mb-3">
                  <div>
                    <span class="font-medium">Policy:</span>
                    <p class="text-slate-900 font-semibold">{{ claim.policyName }}</p>
                  </div>
                  <div>
                    <span class="font-medium">Amount:</span>
                    <p class="text-slate-900 font-semibold">{{ formatINR(claim.claimAmount) }}</p>
                  </div>
                  <div>
                    <span class="font-medium">Date:</span>
                    <p class="text-slate-900 font-semibold">{{ formatDate(claim.claimDate) }}</p>
                  </div>
                  <div>
                    <span class="font-medium">Type:</span>
                    <p class="text-slate-900 font-semibold">{{ claim.policyType }}</p>
                  </div>
                </div>

                <div
                  v-if="claim.reviewerComment"
                  class="bg-blue-50 border border-blue-200 rounded-lg p-3 mt-3"
                >
                  <div class="flex items-center mb-1">
                    <MessageSquare class="w-4 h-4 text-blue-600 mr-2" />
                    <span class="text-sm font-medium text-blue-900">Reviewer Comment</span>
                  </div>
                  <p class="text-sm text-blue-800">{{ claim.reviewerComment }}</p>
                  <p v-if="claim.resolvedDate" class="text-xs text-blue-600 mt-1">
                    Resolved on {{ formatDate(claim.resolvedDate) }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useClaimsStore } from '@/stores/claims'
import { useToast } from '@/composables/useToast'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppButton from '@/components/common/AppButton.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import { FileText, CheckCircle, Clock, TrendingUp, RefreshCw, MessageSquare } from 'lucide-vue-next'

const claimsStore = useClaimsStore()
const toast = useToast()

const claims = computed(() => claimsStore.claims)
const isLoading = computed(() => claimsStore.isLoading)

const totalClaims = computed(() => claims.value.length)
const approvedClaims = computed(() => claims.value.filter((c) => c.status === 'APPROVED').length)
const pendingClaims = computed(() => claims.value.filter((c) => c.status === 'PENDING').length)
const totalClaimAmount = computed(() =>
  claims.value.reduce((sum, claim) => sum + claim.claimAmount, 0),
)

const refreshClaims = async () => {
  try {
    await claimsStore.fetchUserClaims()
    toast.success('Claims Refreshed', `Loaded ${claims.value.length} claims.`)
  } catch (error) {
    toast.error('Refresh Failed', 'Failed to refresh claims.')
  }
}

const getStatusStyle = (status: string) => {
  const styles = {
    PENDING: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
    APPROVED: 'bg-green-100 text-green-800 border border-green-200',
    REJECTED: 'bg-red-100 text-red-800 border border-red-200',
  }
  return styles[status as keyof typeof styles] || styles.PENDING
}

const getStatusText = (status: string) => {
  const texts = {
    PENDING: 'Pending Review',
    APPROVED: 'Approved',
    REJECTED: 'Rejected',
  }
  return texts[status as keyof typeof texts] || status
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

onMounted(async () => {
  try {
    await claimsStore.fetchUserClaims()
  } catch (error) {
    console.error('Failed to load claims on mount:', error)
  }
})
</script>

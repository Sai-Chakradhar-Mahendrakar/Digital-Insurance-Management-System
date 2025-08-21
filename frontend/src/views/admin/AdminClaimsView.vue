<template>
  <div class="min-h-screen bg-slate-50">
    <AdminNavbar />

    <div class="flex">
      <AdminSidebar />

      <!-- Main Content -->
      <main class="flex-1 p-8">
        <div class="max-w-7xl mx-auto">
          <!-- Header -->
          <div class="mb-8">
            <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">Claims Management</h1>
            <p class="text-slate-600 font-inter">Review and manage insurance claims</p>
          </div>

          <!-- Stats Cards -->
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
                <div class="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                  <XCircle class="w-6 h-6 text-red-600" />
                </div>
                <div class="ml-4">
                  <p class="text-2xl font-bold text-slate-900">{{ rejectedClaims }}</p>
                  <p class="text-sm text-slate-500">Rejected</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Loading State -->
          <div v-if="isLoading" class="flex items-center justify-center py-12">
            <div class="flex flex-col items-center">
              <div
                class="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"
              ></div>
              <p class="text-slate-600">Loading claims...</p>
            </div>
          </div>

          <!-- Error State -->
          <div v-else-if="errorMessage" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
            <div class="flex items-center">
              <AlertCircle class="w-5 h-5 text-red-600 mr-3" />
              <p class="text-red-800 text-sm font-medium">{{ errorMessage }}</p>
              <button @click="refreshClaims" class="ml-auto text-red-600 hover:text-red-800">
                <RefreshCw class="w-4 h-4" />
              </button>
            </div>
          </div>

          <!-- Claims Table -->
          <div v-else class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div class="p-6 border-b border-slate-200">
              <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-slate-900">All Claims</h3>
                <AppButton variant="ghost" @click="refreshClaims" :disabled="isLoading">
                  <RefreshCw :class="{ 'animate-spin': isLoading }" class="w-4 h-4 mr-2" />
                  Refresh
                </AppButton>
              </div>
            </div>

            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-slate-200">
                <thead class="bg-slate-50">
                  <tr>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Claim ID
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Policy Name
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Policy Type
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Claim Date
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Amount
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Status
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-200">
                  <tr v-for="claim in claims" :key="claim.id" class="hover:bg-slate-50">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-slate-900">
                      #{{ claim.id }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="text-sm font-medium text-slate-900">{{ claim.policyName }}</div>
                      <div class="text-sm text-slate-500">{{ claim.reason }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                      >
                        {{ claim.policyType }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ formatDate(claim.claimDate) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-slate-900">
                      {{ formatINR(claim.claimAmount) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        :class="getStatusStyle(claim.status)"
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      >
                        <component :is="getStatusIcon(claim.status)" class="w-3 h-3 mr-1" />
                        {{ claim.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex space-x-2">
                        <AppButton
                          v-if="claim.status === 'PENDING'"
                          variant="primary"
                          size="small"
                          @click="openApproveModal(claim)"
                        >
                          <CheckCircle class="w-4 h-4 mr-1" />
                          Approve
                        </AppButton>
                        <AppButton
                          v-if="claim.status === 'PENDING'"
                          variant="secondary"
                          size="small"
                          @click="openRejectModal(claim)"
                        >
                          <XCircle class="w-4 h-4 mr-1" />
                          Reject
                        </AppButton>
                        <AppButton
                          v-if="claim.status !== 'PENDING'"
                          variant="ghost"
                          size="small"
                          @click="viewClaimDetails(claim)"
                        >
                          <Eye class="w-4 h-4 mr-1" />
                          View
                        </AppButton>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Update Claim Status Modal -->
    <UpdateClaimStatusModal
      v-if="selectedClaim"
      :claim="selectedClaim"
      :action="modalAction"
      @close="selectedClaim = null"
      @updated="handleClaimUpdated"
    />

    <!-- Claim Details Modal -->
    <ClaimDetailsModal
      v-if="selectedClaimForDetails"
      :claim="selectedClaimForDetails"
      @close="selectedClaimForDetails = null"
    />

    <!-- Toast Container -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAdminClaimsStore } from '@/stores/adminClaims'
import { useToast } from '@/composables/useToast'
import AdminNavbar from '@/components/admin/AdminNavbar.vue'
import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AppButton from '@/components/common/AppButton.vue'
import UpdateClaimStatusModal from '@/components/admin/UpdateClaimStatusModal.vue'
import ClaimDetailsModal from '@/components/admin/ClaimDetailsModal.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import { FileText, Clock, CheckCircle, XCircle, RefreshCw, AlertCircle, Eye } from 'lucide-vue-next'
import type { AdminClaim } from '@/stores/adminClaims'

const adminClaimsStore = useAdminClaimsStore()
const toast = useToast()

const selectedClaim = ref<AdminClaim | null>(null)
const selectedClaimForDetails = ref<AdminClaim | null>(null)
const modalAction = ref<'APPROVE' | 'REJECT'>('APPROVE')

const claims = computed(() => adminClaimsStore.claims)
const isLoading = computed(() => adminClaimsStore.isLoading)
const errorMessage = computed(() => adminClaimsStore.error)

const totalClaims = computed(() => claims.value.length)
const pendingClaims = computed(() => claims.value.filter((c) => c.status === 'PENDING').length)
const approvedClaims = computed(() => claims.value.filter((c) => c.status === 'APPROVED').length)
const rejectedClaims = computed(() => claims.value.filter((c) => c.status === 'REJECTED').length)

const openApproveModal = (claim: AdminClaim) => {
  selectedClaim.value = claim
  modalAction.value = 'APPROVE'
}

const openRejectModal = (claim: AdminClaim) => {
  selectedClaim.value = claim
  modalAction.value = 'REJECT'
}

const viewClaimDetails = (claim: AdminClaim) => {
  selectedClaimForDetails.value = claim
}

const handleClaimUpdated = (updatedClaim: AdminClaim) => {
  selectedClaim.value = null
  toast.success(
    'Claim Updated Successfully',
    `Claim #${updatedClaim.id} has been ${updatedClaim.status.toLowerCase()}.`,
  )
}

const refreshClaims = async () => {
  try {
    await adminClaimsStore.fetchAllClaims()
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

const getStatusIcon = (status: string) => {
  const icons = {
    PENDING: Clock,
    APPROVED: CheckCircle,
    REJECTED: XCircle,
  }
  return icons[status as keyof typeof icons] || Clock
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
    await adminClaimsStore.fetchAllClaims()
  } catch (error) {
    console.error('Failed to load claims on mount:', error)
  }
})
</script>

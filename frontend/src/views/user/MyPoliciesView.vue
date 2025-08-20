<!-- src/views/user/MyPoliciesView.vue -->
<template>
  <div class="min-h-screen bg-slate-50">
    <AppNavbar />

    <main class="max-w-7xl mx-auto px-4 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">My Policies</h1>
        <p class="text-slate-600 font-inter">Track your insurance policies and their status</p>
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
        <router-link
          to="/policies"
          class="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Shield class="w-4 h-4 mr-2" />
          Browse Policies
        </router-link>
      </div>

      <!-- Policies Grid -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <PolicyCard
          v-for="userPolicy in userPolicies"
          :key="userPolicy.id"
          :user-policy="userPolicy"
          @view="viewPolicyDetails"
        />
      </div>
    </main>

    <!-- Policy Details Modal -->
    <PolicyDetailsModal
      v-if="selectedUserPolicy"
      :user-policy="selectedUserPolicy"
      @close="selectedUserPolicy = null"
    />

    <!-- Toast Container -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserPolicyStore } from '@/stores/userPolicy'
import { useToast } from '@/composables/useToast'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import PolicyCard from '@/components/user/PolicyCard.vue'
import PolicyDetailsModal from '@/components/user/PolicyDetailsModal.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import { Shield, Clock, CheckCircle, IndianRupee } from 'lucide-vue-next'
import type { UserPolicy } from '@/stores/userPolicy'

const userPolicyStore = useUserPolicyStore()
const toast = useToast()

const selectedUserPolicy = ref<UserPolicy | null>(null)

const userPolicies = computed(() => userPolicyStore.userPolicies)
const isLoading = computed(() => userPolicyStore.isLoading)

const pendingCount = computed(() => userPolicies.value.filter((p) => p.status === 'PENDING').length)

const activeCount = computed(() => userPolicies.value.filter((p) => p.status === 'ACTIVE').length)

const totalPremium = computed(() => userPolicies.value.reduce((sum, p) => sum + p.premiumPaid, 0))

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

onMounted(async () => {
  try {
    await userPolicyStore.fetchUserPolicies()
  } catch (error) {
    toast.error('Loading Failed', 'Failed to load your policies')
  }
})
</script>

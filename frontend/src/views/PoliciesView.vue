<!-- src/views/PoliciesView.vue -->
<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-slate-100">
    <AppNavbar />

    <main class="pt-16 pb-24">
      <div class="max-w-7xl mx-auto px-4 lg:px-8">
        <!-- Header -->
        <div class="text-center mb-12">
          <h1 class="text-4xl lg:text-6xl font-bold text-slate-900 font-poppins mb-6">
            Insurance
            <span class="block text-blue-700">Policies</span>
          </h1>
          <p class="text-xl text-slate-600 mb-8 max-w-3xl mx-auto font-inter">
            Choose from our comprehensive range of insurance policies designed to protect what
            matters most to you.
          </p>

          <!-- User Status Message -->
          <div
            v-if="isAuthenticated && userPolicies.length > 0"
            class="mb-8 p-4 bg-blue-50 border border-blue-200 rounded-lg max-w-2xl mx-auto"
          >
            <div class="flex items-center justify-center space-x-2">
              <CheckCircle class="w-5 h-5 text-blue-600" />
              <span class="text-blue-800 font-medium">
                You have {{ userPolicies.length }} active
                {{ userPolicies.length === 1 ? 'policy' : 'policies' }}.
              </span>
              <router-link
                to="/my-policies"
                class="text-blue-700 hover:text-blue-900 underline font-medium"
              >
                View My Policies
              </router-link>
            </div>
          </div>
        </div>

        <!-- Search and Filter -->
        <div class="max-w-2xl mx-auto mb-12">
          <div class="relative">
            <Search
              class="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-slate-400"
            />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search available policies..."
              class="w-full pl-12 pr-4 py-4 border border-slate-200 rounded-xl text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 font-inter shadow-sm"
            />
          </div>
        </div>

        <!-- Filter Tabs -->
        <div class="flex justify-center mb-12">
          <div class="flex space-x-1 bg-slate-100 rounded-xl p-1">
            <button
              v-for="filter in filterOptions"
              :key="filter.value"
              @click="selectedFilter = filter.value"
              :class="[
                'px-6 py-3 rounded-lg font-medium transition-all duration-200',
                selectedFilter === filter.value
                  ? 'bg-white text-blue-700 shadow-sm'
                  : 'text-slate-600 hover:text-slate-900',
              ]"
            >
              {{ filter.label }}
            </button>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="text-center py-12">
          <div class="inline-flex items-center space-x-2">
            <div
              class="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"
            ></div>
            <span class="text-slate-600">Loading available policies...</span>
          </div>
        </div>

        <!-- No Available Policies -->
        <div
          v-else-if="
            filteredPolicies.length === 0 && availablePolicies.length === 0 && isAuthenticated
          "
          class="text-center py-12"
        >
          <Shield class="w-24 h-24 text-slate-300 mx-auto mb-6" />
          <h3 class="text-2xl font-semibold text-slate-900 mb-4">All Policies Purchased!</h3>
          <p class="text-slate-600 mb-8">
            You have purchased all available policies. Check your policy dashboard for details.
          </p>
          <router-link
            to="/my-policies"
            class="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Shield class="w-4 h-4 mr-2" />
            View My Policies
          </router-link>
        </div>

        <!-- No Search Results -->
        <div v-else-if="filteredPolicies.length === 0 && searchQuery" class="text-center py-12">
          <Search class="w-24 h-24 text-slate-300 mx-auto mb-6" />
          <h3 class="text-2xl font-semibold text-slate-900 mb-4">No Policies Found</h3>
          <p class="text-slate-600 mb-8">
            No policies match your search criteria "{{ searchQuery }}"
          </p>
          <button
            @click="clearSearch"
            class="inline-flex items-center px-6 py-3 bg-slate-600 text-white rounded-lg hover:bg-slate-700 transition-colors"
          >
            Clear Search
          </button>
        </div>

        <!-- Policies Grid -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <PolicyCard
            v-for="policy in filteredPolicies"
            :key="policy.id"
            :policy="policy"
            :show-apply-button="isAuthenticated"
            @view-details="handleViewDetails"
            @apply="handleApplyForPolicy"
          />
        </div>

        <!-- Call to Action for Non-Authenticated Users -->
        <div
          v-if="!isAuthenticated && filteredPolicies.length > 0"
          class="text-center mt-16 py-12 bg-gradient-to-r from-blue-600 to-blue-800 rounded-2xl"
        >
          <h3 class="text-3xl font-bold text-white mb-4">Ready to Get Protected?</h3>
          <p class="text-blue-100 mb-8 max-w-2xl mx-auto">
            Sign up today to purchase any of our comprehensive insurance policies and secure your
            future.
          </p>
          <div class="flex justify-center space-x-4">
            <router-link
              to="/register"
              class="inline-flex items-center px-8 py-4 bg-white text-blue-700 rounded-lg hover:bg-blue-50 transition-colors font-semibold"
            >
              Get Started
            </router-link>
            <router-link
              to="/login"
              class="inline-flex items-center px-8 py-4 border-2 border-white text-white rounded-lg hover:bg-white hover:text-blue-700 transition-colors font-semibold"
            >
              Sign In
            </router-link>
          </div>
        </div>
      </div>
    </main>

    <!-- Policy Details Modal -->
    <PolicyModal
      v-if="selectedPolicy"
      :policy="selectedPolicy"
      :is-authenticated="isAuthenticated"
      @close="selectedPolicy = null"
      @apply="handleApplyForPolicy"
    />

    <!-- Toast Container -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { usePolicyStore } from '@/stores/policy'
import { useUserPolicyStore } from '@/stores/userPolicy'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import PolicyCard from '@/components/policies/PolicyCard.vue'
import PolicyModal from '@/components/policies/PolicyModal.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import { Search, Shield, CheckCircle } from 'lucide-vue-next'
import type { Policy } from '@/types/policy'

const policyStore = usePolicyStore()
const userPolicyStore = useUserPolicyStore()
const authStore = useAuthStore()
const toast = useToast()

const selectedPolicy = ref<Policy | null>(null)
const searchQuery = ref('')
const selectedFilter = ref('all')

const filterOptions = [
  { label: 'All Policies', value: 'all' },
  { label: 'Health', value: 'Health' },
  { label: 'Life', value: 'Life' },
  { label: 'Auto', value: 'Auto' },
  { label: 'Home', value: 'Home' },
  { label: 'Travel', value: 'Travel' },
]

// Computed properties
const isLoading = computed(() => policyStore.isLoading)
const isAuthenticated = computed(() => authStore.isAuthenticated)
const availablePolicies = computed(() => policyStore.availablePolicies)
const userPolicies = computed(() => userPolicyStore.userPolicies)

const filteredPolicies = computed(() => {
  let policies = availablePolicies.value

  // Filter by search query
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    policies = policies.filter(
      (policy) =>
        policy.name.toLowerCase().includes(query) ||
        policy.description.toLowerCase().includes(query) ||
        policy.type.toLowerCase().includes(query),
    )
  }

  // Filter by type
  if (selectedFilter.value !== 'all') {
    policies = policies.filter((policy) => policy.type.toLowerCase() === selectedFilter.value.toLowerCase())
  }

  return policies
})

// Methods
const handleViewDetails = (policy: Policy) => {
  selectedPolicy.value = policy
}

const handleApplyForPolicy = (policy: Policy) => {
  selectedPolicy.value = policy
}

const clearSearch = () => {
  searchQuery.value = ''
}

// Load policies on component mount
onMounted(async () => {
  try {
    await policyStore.fetchPolicies()
  } catch (error) {
    toast.error('Loading Failed', 'Failed to load available policies')
  }
})
</script>

<!-- src/views/PoliciesView.vue -->
<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Header -->
    <AppNavbar />

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 lg:px-8 py-8">
      <!-- Page Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-semibold text-slate-900 font-poppins mb-2">Insurance Policies</h1>
        <p class="text-slate-600 font-inter">
          {{
            isAuthenticated
              ? 'Manage and view your insurance policies'
              : 'Browse available insurance policies'
          }}
        </p>
      </div>

      <!-- Auth Prompt Banner (for non-authenticated users) -->
      <div v-if="!isAuthenticated" class="mb-8 bg-blue-50 border border-blue-200 rounded-lg p-6">
        <div class="flex items-center space-x-4">
          <div
            class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0"
          >
            <Shield class="w-6 h-6 text-blue-700" />
          </div>
          <div class="flex-1">
            <h3 class="text-lg font-semibold text-blue-900 mb-1">
              Want to manage your own policies?
            </h3>
            <p class="text-blue-700 text-sm mb-3">
              Create an account to apply for policies, track claims, and manage your insurance
              portfolio.
            </p>
            <div class="flex space-x-3">
              <AppButton variant="primary" size="small" @click="navigateTo('/register')">
                Get Started
              </AppButton>
              <AppButton variant="ghost" size="small" @click="navigateTo('/login')">
                Sign In
              </AppButton>
            </div>
          </div>
        </div>
      </div>

      <!-- Search and Filters -->
      <div class="mb-8 space-y-4">
        <!-- Search Bar -->
        <div class="relative max-w-md">
          <Search
            class="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-slate-400"
          />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search policies..."
            class="w-full pl-12 pr-4 py-3 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter"
            @input="handleSearch"
          />
        </div>

        <!-- Filter Buttons -->
        <div class="flex flex-wrap gap-2">
          <button
            v-for="filter in filterOptions"
            :key="filter.value"
            @click="setActiveFilter(filter.value)"
            :class="[
              'px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200',
              activeFilter === filter.value
                ? 'bg-blue-700 text-white'
                : 'bg-white text-slate-600 border border-slate-200 hover:border-blue-300 hover:text-blue-700',
            ]"
          >
            {{ filter.label }}
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center py-12">
        <div
          class="w-8 h-8 border-2 border-blue-700 border-t-transparent rounded-full animate-spin"
        ></div>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredPolicies.length === 0" class="text-center py-12">
        <Shield class="w-16 h-16 text-slate-400 mx-auto mb-4" />
        <h3 class="text-xl font-semibold text-slate-900 mb-2">
          {{ searchQuery ? 'No policies found' : 'No Policies Available' }}
        </h3>
        <p class="text-slate-600">
          {{
            searchQuery
              ? `No policies match "${searchQuery}"`
              : 'There are no insurance policies available at the moment.'
          }}
        </p>
        <AppButton v-if="searchQuery" variant="ghost" @click="clearSearch" class="mt-4">
          Clear Search
        </AppButton>
      </div>

      <!-- Policies Grid -->
      <div v-else>
        <!-- Results Info -->
        <div class="mb-6 text-sm text-slate-600">
          Showing {{ filteredPolicies.length }} of {{ policies.length }} policies
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
          <PolicyCard
            v-for="policy in filteredPolicies"
            :key="policy.id"
            :policy="policy"
            :show-apply-button="!isAuthenticated"
            @view-details="viewPolicyDetails"
            @apply="handleApplyForPolicy"
          />
        </div>
      </div>

      <!-- Policy Details Modal -->
      <PolicyModal
        v-if="selectedPolicy"
        :policy="selectedPolicy"
        :is-authenticated="isAuthenticated"
        @close="selectedPolicy = null"
        @apply="handleApplyForPolicy"
      />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePolicyStore } from '@/stores/policy'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppButton from '@/components/common/AppButton.vue'
import PolicyCard from '@/components/policies/PolicyCard.vue'
import PolicyModal from '@/components/policies/PolicyModal.vue'
import { Shield, Search } from 'lucide-vue-next'
import type { Policy } from '@/types/policy'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const policyStore = usePolicyStore()

const isLoading = ref(true)
const selectedPolicy = ref<Policy | null>(null)
const searchQuery = ref('')
const activeFilter = ref('all')

const filterOptions = [
  { label: 'All Policies', value: 'all' },
  { label: 'Health', value: 'health' },
  { label: 'Auto', value: 'auto' },
  { label: 'Home', value: 'home' },
  { label: 'Life', value: 'life' },
]

const policies = computed(() => policyStore.policies)
const isAuthenticated = computed(() => authStore.isAuthenticated)

const filteredPolicies = computed(() => {
  let filtered = policies.value

  // Filter by search query
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (policy) =>
        policy.name.toLowerCase().includes(query) ||
        policy.type.toLowerCase().includes(query) ||
        policy.description.toLowerCase().includes(query),
    )
  }

  // Filter by type
  if (activeFilter.value !== 'all') {
    filtered = filtered.filter(
      (policy) => policy.type.toLowerCase() === activeFilter.value.toLowerCase(),
    )
  }

  return filtered
})

const navigateTo = (path: string) => {
  router.push(path)
}

const viewPolicyDetails = (policy: Policy) => {
  selectedPolicy.value = policy
}

const handleApplyForPolicy = (policy: Policy) => {
  if (!isAuthenticated.value) {
    // Redirect to registration with policy info
    router.push({
      path: '/register',
      query: {
        redirect: '/policies',
        policyId: policy.id.toString(),
      },
    })
  } else {
    // Handle application process for authenticated users
    console.log('Applying for policy:', policy.name)
    // Add your application logic here
  }
}

const setActiveFilter = (filter: string) => {
  activeFilter.value = filter
}

const handleSearch = () => {
  // Update URL with search parameter
  const query = { ...route.query }
  if (searchQuery.value.trim()) {
    query.search = searchQuery.value
  } else {
    delete query.search
  }
  router.replace({ query })
}

const clearSearch = () => {
  searchQuery.value = ''
  const query = { ...route.query }
  delete query.search
  router.replace({ query })
}

// Watch for route changes to update search
watch(
  () => route.query.search,
  (newSearch) => {
    if (newSearch && typeof newSearch === 'string') {
      searchQuery.value = newSearch
    }
  },
  { immediate: true },
)

onMounted(async () => {
  try {
    await policyStore.fetchPolicies()
  } finally {
    isLoading.value = false
  }
})
</script>

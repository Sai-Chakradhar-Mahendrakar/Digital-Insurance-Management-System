<!-- src/views/admin/AdminPoliciesView.vue -->
<template>
  <div class="min-h-screen bg-slate-50">
    <AdminNavbar />

    <div class="flex">
      <AdminSidebar />

      <!-- Main Content -->
      <main class="flex-1 p-8">
        <div class="max-w-7xl mx-auto">
          <!-- Header -->
          <div class="flex justify-between items-center mb-8">
            <div>
              <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">Manage Policies</h1>
              <p class="text-slate-600 font-inter">Create, edit, and manage insurance policies</p>
            </div>
            <AppButton
              variant="primary"
              @click="showAddPolicyModal = true"
              class="flex items-center space-x-2"
            >
              <Plus class="w-4 h-4" />
              <span>Add New Policy</span>
            </AppButton>
          </div>

          <!-- Error Display -->
          <InlineError
            :error="errorMessage"
            title="Operation Failed"
            :dismissible="true"
            @dismiss="clearError"
          />

          <!-- Success Message -->
          <div
            v-if="successMessage"
            class="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg"
          >
            <div class="flex items-center">
              <CheckCircle class="w-5 h-5 text-green-600 mr-3" />
              <p class="text-green-800 text-sm font-medium">{{ successMessage }}</p>
              <button @click="clearSuccess" class="ml-auto text-green-600 hover:text-green-800">
                <X class="w-4 h-4" />
              </button>
            </div>
          </div>

          <!-- Policies Stats -->
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
            <div class="bg-white rounded-lg shadow-sm border border-slate-200 p-4">
              <div class="flex items-center">
                <div class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                  <Shield class="w-4 h-4 text-blue-600" />
                </div>
                <div class="ml-4">
                  <p class="text-sm font-medium text-slate-600">Total Policies</p>
                  <p class="text-2xl font-bold text-slate-900">{{ policies.length }}</p>
                </div>
              </div>
            </div>
            <div class="bg-white rounded-lg shadow-sm border border-slate-200 p-4">
              <div class="flex items-center">
                <div class="w-8 h-8 bg-green-100 rounded-lg flex items-center justify-center">
                  <TrendingUp class="w-4 h-4 text-green-600" />
                </div>
                <div class="ml-4">
                  <p class="text-sm font-medium text-slate-600">Active Policies</p>
                  <p class="text-2xl font-bold text-slate-900">{{ filteredPolicies.length }}</p>
                </div>
              </div>
            </div>
            <div class="bg-white rounded-lg shadow-sm border border-slate-200 p-4">
              <div class="flex items-center">
                <div class="w-8 h-8 bg-yellow-100 rounded-lg flex items-center justify-center">
                  <IndianRupee class="w-4 h-4 text-yellow-600" />
                </div>
                <div class="ml-4">
                  <p class="text-sm font-medium text-slate-600">Total Premium</p>
                  <p class="text-lg font-bold text-slate-900">{{ formatINR(totalPremium) }}</p>
                </div>
              </div>
            </div>
            <div class="bg-white rounded-lg shadow-sm border border-slate-200 p-4">
              <div class="flex items-center">
                <div class="w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center">
                  <Target class="w-4 h-4 text-purple-600" />
                </div>
                <div class="ml-4">
                  <p class="text-sm font-medium text-slate-600">Total Coverage</p>
                  <p class="text-lg font-bold text-slate-900">{{ formatINR(totalCoverage) }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Policies Table -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div class="p-6 border-b border-slate-200">
              <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-slate-900">
                  All Policies ({{ filteredPolicies.length }})
                </h3>
                <div class="flex items-center space-x-4">
                  <!-- Search Bar -->
                  <div class="relative">
                    <Search
                      class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-400"
                    />
                    <input
                      v-model="searchQuery"
                      type="text"
                      placeholder="Search policies..."
                      class="pl-10 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all duration-200"
                    />
                  </div>

                  <!-- Filter by Type -->
                  <select
                    v-model="selectedType"
                    class="px-3 py-2 border border-slate-200 rounded-lg text-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                  >
                    <option value="">All Types</option>
                    <option value="Health">Health</option>
                    <option value="Life">Life</option>
                    <option value="Auto">Auto</option>
                    <option value="Home">Home</option>
                    <option value="Travel">Travel</option>
                  </select>

                  <!-- Refresh Button -->
                  <AppButton
                    variant="ghost"
                    @click="refreshPolicies"
                    :disabled="isLoading"
                    class="flex items-center space-x-2"
                  >
                    <RefreshCw :class="{ 'animate-spin': isLoading }" class="w-4 h-4" />
                    <span>Refresh</span>
                  </AppButton>
                </div>
              </div>
            </div>

            <!-- Loading State -->
            <div v-if="isLoading" class="flex items-center justify-center py-12">
              <div class="flex flex-col items-center">
                <div
                  class="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"
                ></div>
                <p class="text-slate-600">Loading policies...</p>
              </div>
            </div>

            <!-- Empty State -->
            <div v-else-if="filteredPolicies.length === 0" class="text-center py-12">
              <Shield class="w-16 h-16 text-slate-400 mx-auto mb-4" />
              <h3 class="text-lg font-semibold text-slate-900 mb-2">
                {{ searchQuery || selectedType ? 'No matching policies' : 'No policies found' }}
              </h3>
              <p class="text-slate-600 mb-4">
                {{
                  searchQuery || selectedType
                    ? 'Try adjusting your search criteria'
                    : 'Get started by creating your first insurance policy'
                }}
              </p>
              <div class="flex justify-center space-x-3">
                <AppButton
                  v-if="!searchQuery && !selectedType"
                  variant="primary"
                  @click="showAddPolicyModal = true"
                >
                  Create First Policy
                </AppButton>
                <AppButton v-else variant="ghost" @click="clearFilters"> Clear Filters </AppButton>
                <AppButton variant="secondary" @click="refreshPolicies"> Refresh Data </AppButton>
              </div>
            </div>

            <!-- Policies Table -->
            <div v-else class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-slate-50">
                  <tr>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      <button
                        @click="sortBy('name')"
                        class="flex items-center hover:text-slate-700"
                      >
                        Policy
                        <ArrowUpDown class="w-3 h-3 ml-1" />
                      </button>
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      <button
                        @click="sortBy('type')"
                        class="flex items-center hover:text-slate-700"
                      >
                        Type
                        <ArrowUpDown class="w-3 h-3 ml-1" />
                      </button>
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      <button
                        @click="sortBy('premiumAmount')"
                        class="flex items-center hover:text-slate-700"
                      >
                        Premium (₹)
                        <ArrowUpDown class="w-3 h-3 ml-1" />
                      </button>
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      <button
                        @click="sortBy('coverageAmount')"
                        class="flex items-center hover:text-slate-700"
                      >
                        Coverage (₹)
                        <ArrowUpDown class="w-3 h-3 ml-1" />
                      </button>
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Duration
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Created
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-200">
                  <tr
                    v-for="policy in paginatedPolicies"
                    :key="policy.id"
                    class="hover:bg-slate-50 transition-colors duration-150"
                  >
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center">
                        <div class="flex-shrink-0">
                          <div
                            class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center"
                          >
                            <Shield class="w-4 h-4 text-blue-600" />
                          </div>
                        </div>
                        <div class="ml-4">
                          <div class="text-sm font-medium text-slate-900">{{ policy.name }}</div>
                          <div class="text-sm text-slate-500 max-w-xs truncate">
                            {{ policy.description }}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        :class="getTypeColor(policy.type)"
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      >
                        {{ policy.type }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="text-sm font-medium text-slate-900">
                        {{ formatINR(policy.premiumAmount) }}
                      </div>
                      <div class="text-sm text-slate-500">Annual</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="text-sm font-medium text-slate-900">
                        {{ formatINR(policy.coverageAmount) }}
                      </div>
                      <div class="text-sm text-slate-500">Maximum</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ policy.durationMonths }} months
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-500">
                      {{ formatDate(policy.createdAt) }}
                    </td>
                    <td class="px-8 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex items-center space-x-2">
                        <button
                          @click="viewPolicy(policy)"
                          class="text-blue-600 hover:text-blue-900 transition-colors"
                          title="View policy details"
                        >
                          <Eye class="w-4 h-4" />
                        </button>
                        <button
                          @click="editPolicy(policy)"
                          class="text-green-600 hover:text-green-900 transition-colors"
                          title="Edit policy"
                        >
                          <Edit class="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Pagination -->
            <div v-if="totalPages > 1" class="px-6 py-3 border-t border-slate-200 bg-slate-50">
              <div class="flex items-center justify-between">
                <div class="text-sm text-slate-700">
                  Showing {{ startIndex + 1 }} to {{ endIndex }} of
                  {{ filteredPolicies.length }} policies
                </div>
                <div class="flex items-center space-x-2">
                  <button
                    @click="goToPage(currentPage - 1)"
                    :disabled="currentPage === 1"
                    class="px-3 py-1 text-sm border border-slate-300 rounded-md hover:bg-slate-100 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Previous
                  </button>
                  <span class="px-3 py-1 text-sm">
                    Page {{ currentPage }} of {{ totalPages }}
                  </span>
                  <button
                    @click="goToPage(currentPage + 1)"
                    :disabled="currentPage === totalPages"
                    class="px-3 py-1 text-sm border border-slate-300 rounded-md hover:bg-slate-100 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Next
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Add/Edit Policy Modal -->
    <AddPolicyModal
      v-if="showAddPolicyModal"
      :policy="selectedPolicy"
      @close="closeModal"
      @save="handleSavePolicy"
    />

    <!-- View Policy Modal -->
    <ViewPolicyModal
      v-if="showViewModal && selectedPolicy"
      :policy="selectedPolicy"
      @close="showViewModal = false"
      @edit="editFromView"
    />

    <!-- Toast Container -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAdminPolicyStore } from '@/stores/adminPolicy'
import { useToast } from '@/composables/useToast'
import AdminNavbar from '@/components/admin/AdminNavbar.vue'
import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AddPolicyModal from '@/components/admin/AddPolicyModal.vue'
import ViewPolicyModal from '@/components/admin/ViewPolicyModal.vue'
import InlineError from '@/components/common/InlineError.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import AppButton from '@/components/common/AppButton.vue'
import {
  Plus,
  Search,
  Edit,
  Trash2,
  CheckCircle,
  X,
  Shield,
  Eye,
  ArrowUpDown,
  RefreshCw,
  TrendingUp,
  IndianRupee,
  Target,
} from 'lucide-vue-next'
import type { Policy } from '@/types/policy'

const adminPolicyStore = useAdminPolicyStore()
const toast = useToast()

// Component state
const showAddPolicyModal = ref(false)
const showViewModal = ref(false)
const selectedPolicy = ref<Policy | null>(null)
const searchQuery = ref('')
const selectedType = ref('')
const errorMessage = ref('')
const successMessage = ref('')

// Sorting and pagination
const sortField = ref<keyof Policy | ''>('')
const sortDirection = ref<'asc' | 'desc'>('asc')
const currentPage = ref(1)
const itemsPerPage = 10

// Computed properties
const policies = computed(() => adminPolicyStore.policies)
const isLoading = computed(() => adminPolicyStore.isLoading)

const totalPremium = computed(() => {
  return policies.value.reduce((sum, policy) => sum + policy.premiumAmount, 0)
})

const totalCoverage = computed(() => {
  return policies.value.reduce((sum, policy) => sum + policy.coverageAmount, 0)
})

const filteredPolicies = computed(() => {
  let filtered = [...policies.value]

  // Search filter
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (policy) =>
        policy.name.toLowerCase().includes(query) ||
        policy.type.toLowerCase().includes(query) ||
        policy.description.toLowerCase().includes(query),
    )
  }

  // Type filter
  if (selectedType.value) {
    filtered = filtered.filter(
      (policy) => policy.type.toLowerCase() === selectedType.value.toLowerCase(),
    )
  }

  // Sorting
  if (sortField.value) {
    filtered.sort((a, b) => {
      const aVal = a[sortField.value as keyof Policy]
      const bVal = b[sortField.value as keyof Policy]

      if (typeof aVal === 'string' && typeof bVal === 'string') {
        return sortDirection.value === 'asc' ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal)
      }

      if (typeof aVal === 'number' && typeof bVal === 'number') {
        return sortDirection.value === 'asc' ? aVal - bVal : bVal - aVal
      }

      return 0
    })
  }

  return filtered
})

const viewPolicy = (policy: Policy) => {
  selectedPolicy.value = policy
  showViewModal.value = true
}

const totalPages = computed(() => Math.ceil(filteredPolicies.value.length / itemsPerPage))
const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage)
const endIndex = computed(() =>
  Math.min(startIndex.value + itemsPerPage, filteredPolicies.value.length),
)

const paginatedPolicies = computed(() =>
  filteredPolicies.value.slice(startIndex.value, endIndex.value),
)

// Methods
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

const getTypeColor = (type: string): string => {
  const colors = {
    Health: 'bg-green-100 text-green-800',
    Life: 'bg-blue-100 text-blue-800',
    Auto: 'bg-yellow-100 text-yellow-800',
    Home: 'bg-purple-100 text-purple-800',
    Travel: 'bg-pink-100 text-pink-800',
  }
  return colors[type as keyof typeof colors] || 'bg-gray-100 text-gray-800'
}

const sortBy = (field: keyof Policy) => {
  if (sortField.value === field) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortField.value = field
    sortDirection.value = 'asc'
  }
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const clearFilters = () => {
  searchQuery.value = ''
  selectedType.value = ''
  currentPage.value = 1
}

const refreshPolicies = async () => {
  try {
    await adminPolicyStore.fetchAdminPolicies(1000) // Fetch all policies
    toast.success('Policies Refreshed', 'Policy data has been updated successfully.')
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : 'Failed to refresh policies'
    toast.error('Refresh Failed', message)
  }
}

const editPolicy = (policy: Policy) => {
  selectedPolicy.value = policy
  showAddPolicyModal.value = true
}

const editFromView = (policy: Policy) => {
  showViewModal.value = false
  setTimeout(() => {
    selectedPolicy.value = policy
    showAddPolicyModal.value = true
  }, 300)
}

const handleSavePolicy = async (policyData: any) => {
  try {
    if (selectedPolicy.value) {
      await adminPolicyStore.updatePolicy(selectedPolicy.value.id, policyData)
      toast.success('Policy Updated', `"${policyData.name}" has been successfully updated.`)
    } else {
      await adminPolicyStore.createPolicy(policyData)
      toast.success('Policy Created', `"${policyData.name}" has been successfully created.`)
    }
    closeModal()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : 'Failed to save policy'
    toast.error('Save Failed', message)
  }
}

const closeModal = () => {
  showAddPolicyModal.value = false
  selectedPolicy.value = null
}

const closeViewModal = () => {
  showViewModal.value = false
  selectedPolicy.value = null
}

const clearError = () => {
  errorMessage.value = ''
}

const clearSuccess = () => {
  successMessage.value = ''
}

// Load policies on component mount
onMounted(async () => {
  try {
    await adminPolicyStore.fetchAdminPolicies(1000) // Fetch all policies with size=1000
  } catch (error: unknown) {
    if (typeof error === 'object' && error !== null) {
      const err = error as any
      errorMessage.value =
        err.response?.data?.errorMessage ||
        err.message ||
        'Failed to load policies'
    }
    toast.error('Loading Failed', errorMessage.value)
  }
})
</script>

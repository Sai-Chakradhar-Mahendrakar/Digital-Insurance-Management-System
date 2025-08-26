<template>
  <div class="fixed inset-0 z-50 overflow-y-auto">
    <div
      class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0"
    >
      <!-- Background overlay -->
      <div
        class="fixed inset-0 transition-opacity bg-slate-500 bg-opacity-75"
        @click="$emit('close')"
      ></div>

      <!-- Modal -->
      <div
        class="inline-block w-full max-w-6xl p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-xl"
      >
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center space-x-3">
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
              <Shield class="w-6 h-6 text-blue-700" />
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-slate-900 font-poppins">{{ policy.name }}</h3>
              <span
                class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-emerald-100 text-emerald-800"
              >
                {{ policy.type }} Insurance
              </span>
            </div>
          </div>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Tabs -->
        <div class="border-b border-slate-200 mb-6">
          <nav class="-mb-px flex space-x-8">
            <button
              @click="switchToTab('details')"
              :class="[
                'py-2 px-1 border-b-2 font-medium text-sm',
                activeTab === 'details'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300',
              ]"
            >
              Policy Details
            </button>
            <button
              @click="switchToTab('users')"
              :class="[
                'py-2 px-1 border-b-2 font-medium text-sm flex items-center',
                activeTab === 'users'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300',
              ]"
            >
              Enrolled Users
              <span
                v-if="enrolledUsers.length > 0"
                class="ml-2 bg-blue-100 text-blue-600 px-2 py-1 rounded-full text-xs"
              >
                {{ enrolledUsers.length }}
              </span>
            </button>
          </nav>
        </div>

        <!-- Policy Details Tab -->
        <div v-if="activeTab === 'details'" class="space-y-6">
          <!-- ... existing policy details content ... -->

          <!-- Description -->
          <div>
            <h4 class="font-medium text-slate-900 mb-2 font-poppins">Description</h4>
            <p class="text-slate-600 font-inter">{{ policy.description }}</p>
          </div>

          <!-- Financial Details -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-4">
              <h4 class="font-medium text-slate-900 font-poppins">Financial Details</h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-slate-500">Premium Amount</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy.premiumAmount)
                  }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Coverage Amount</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy.coverageAmount)
                  }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Renewal Premium</span>
                  <span class="font-semibold text-slate-900">{{
                    formatINR(policy.renewalPremiumRate)
                  }}</span>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <h4 class="font-medium text-slate-900 font-poppins">Policy Information</h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-slate-500">Policy ID</span>
                  <span class="font-semibold text-slate-900">#{{ policy.id }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Duration</span>
                  <span class="font-semibold text-slate-900"
                    >{{ policy.durationMonths }} months</span
                  >
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-500">Created Date</span>
                  <span class="font-semibold text-slate-900">{{
                    formatDate(policy.createdAt)
                  }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Coverage Summary -->
          <div class="bg-blue-50 rounded-lg p-4">
            <h4 class="font-medium text-blue-900 mb-2 font-poppins">Coverage Summary</h4>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
              <div class="text-center">
                <div class="font-semibold text-blue-900">
                  {{ formatINR(policy.coverageAmount) }}
                </div>
                <div class="text-blue-600">Total Coverage</div>
              </div>
              <div class="text-center">
                <div class="font-semibold text-blue-900">{{ formatINR(policy.premiumAmount) }}</div>
                <div class="text-blue-600">Annual Premium</div>
              </div>
              <div class="text-center">
                <div class="font-semibold text-blue-900">
                  {{ Math.round((policy.coverageAmount / policy.premiumAmount) * 100) / 100 }}x
                </div>
                <div class="text-blue-600">Coverage Ratio</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Enrolled Users Tab -->
        <div v-if="activeTab === 'users'">
          <!-- Loading State -->
          <div v-if="isLoadingUsers" class="flex items-center justify-center py-12">
            <div class="flex flex-col items-center">
              <div
                class="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"
              ></div>
              <p class="text-slate-600">Loading enrolled users...</p>
            </div>
          </div>

          <!-- Error State -->
          <div v-else-if="errorMessage" class="text-center py-12">
            <div
              class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4"
            >
              <AlertCircle class="w-8 h-8 text-red-600" />
            </div>
            <h3 class="text-lg font-semibold text-slate-900 mb-2">Failed to Load Users</h3>
            <p class="text-slate-600 mb-4">{{ errorMessage }}</p>
            <AppButton variant="primary" @click="loadEnrolledUsers"> Try Again </AppButton>
          </div>

          <!-- Empty State -->
          <div v-else-if="enrolledUsers.length === 0" class="text-center py-12">
            <Users class="w-16 h-16 text-slate-300 mx-auto mb-4" />
            <h3 class="text-lg font-semibold text-slate-900 mb-2">No Users Enrolled</h3>
            <p class="text-slate-600">No users have enrolled in this policy yet.</p>
          </div>

          <!-- Users List -->
          <div v-else>
            <!-- Summary Stats -->
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
              <div class="bg-blue-50 rounded-lg p-4 text-center">
                <div class="font-semibold text-blue-900 text-2xl">{{ enrolledUsers.length }}</div>
                <div class="text-blue-600 text-sm">Total Enrolled</div>
              </div>
              <div class="bg-green-50 rounded-lg p-4 text-center">
                <div class="font-semibold text-green-900 text-2xl">{{ activeUsersCount }}</div>
                <div class="text-green-600 text-sm">Active Policies</div>
              </div>
              <div class="bg-yellow-50 rounded-lg p-4 text-center">
                <div class="font-semibold text-yellow-900 text-2xl">{{ pendingUsersCount }}</div>
                <div class="text-yellow-600 text-sm">Pending Approval</div>
              </div>
              <div class="bg-purple-50 rounded-lg p-4 text-center">
                <div class="font-semibold text-purple-900">
                  {{ formatINR(totalPremiumCollected) }}
                </div>
                <div class="text-purple-600 text-sm">Total Premium</div>
              </div>
            </div>

            <!-- Users Table -->
            <div class="bg-white border border-slate-200 rounded-lg overflow-hidden">
              <div class="px-6 py-4 border-b border-slate-200 bg-slate-50">
                <h4 class="font-medium text-slate-900">Enrolled Users</h4>
              </div>

              <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-slate-200">
                  <thead class="bg-slate-50">
                    <tr>
                      <th
                        class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                      >
                        User
                      </th>
                      <th
                        class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                      >
                        Contact
                      </th>
                      <th
                        class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                      >
                        Policy Period
                      </th>
                      <th
                        class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                      >
                        Premium
                      </th>
                      <th
                        class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                      >
                        Status
                      </th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-slate-200">
                    <tr v-for="user in enrolledUsers" :key="user.id" class="hover:bg-slate-50">
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="flex items-center">
                          <div
                            class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center"
                          >
                            <User class="w-5 h-5 text-blue-600" />
                          </div>
                          <div class="ml-4">
                            <div class="text-sm font-medium text-slate-900">
                              {{ user.userName }}
                            </div>
                            <div class="text-sm text-slate-500">User ID: {{ user.userId }}</div>
                          </div>
                        </div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm text-slate-900">{{ user.userEmail }}</div>
                        <div class="text-sm text-slate-500">{{ user.userPhone }}</div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm text-slate-900">{{ formatDate(user.startDate) }}</div>
                        <div class="text-sm text-slate-500">to {{ formatDate(user.endDate) }}</div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm font-medium text-slate-900">
                          {{ formatINR(user.premiumPaid) }}
                        </div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap">
                        <span
                          :class="getStatusStyle(user.status)"
                          class="px-2 py-1 rounded-full text-xs font-medium"
                        >
                          {{ getStatusText(user.status) }}
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="flex justify-end space-x-3 mt-8 pt-6 border-t border-slate-200">
          <AppButton variant="ghost" @click="$emit('close')"> Close </AppButton>
          <AppButton
            v-if="activeTab === 'users' && enrolledUsers.length > 0"
            variant="secondary"
            @click="exportUsers"
          >
            <Download class="w-4 h-4 mr-2" />
            Export Users
          </AppButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useAdminPolicyStore } from '@/stores/adminPolicy'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import { Shield, X, Users, User, Download, AlertCircle } from 'lucide-vue-next'
import AppButton from '@/components/common/AppButton.vue'
import type { Policy } from '@/types/policy'
import type { PolicyUser } from '@/types/admin'

interface Props {
  policy: Policy
}

const props = defineProps<Props>()

defineEmits<{
  (e: 'close'): void
}>()

const adminPolicyStore = useAdminPolicyStore()
const authStore = useAuthStore()
const toast = useToast()

const activeTab = ref('details')
const enrolledUsers = ref<PolicyUser[]>([])
const isLoadingUsers = ref(false)
const errorMessage = ref('')

// Computed properties
const activeUsersCount = computed(
  () => enrolledUsers.value.filter((user) => user.status === 'ACTIVE').length,
)

const pendingUsersCount = computed(
  () => enrolledUsers.value.filter((user) => user.status === 'PENDING').length,
)

const totalPremiumCollected = computed(() =>
  enrolledUsers.value.reduce((sum, user) => sum + user.premiumPaid, 0),
)

// Methods
const switchToTab = (tab: string) => {
  console.log('Switching to tab:', tab)
  activeTab.value = tab
}

const loadEnrolledUsers = async () => {
  console.log('Loading enrolled users for policy ID:', props.policy.id)
  isLoadingUsers.value = true
  errorMessage.value = ''

  try {
    const response = await fetch(`https://digital-insurance-management-system.onrender.com/admin/policies/${props.policy.id}/users`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${authStore.token}`,
        Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
      },
      credentials: 'include',
    })

    if (!response.ok) {
      throw new Error(`Failed to fetch policy users: ${response.status} ${response.statusText}`)
    }

    const data = await response.json()
    console.log('API Response:', data)

    // Fix: Handle both possible response structures
    if (data.userPolicies && Array.isArray(data.userPolicies)) {
      enrolledUsers.value = data.userPolicies
    } else if (data.content && Array.isArray(data.content)) {
      enrolledUsers.value = data.content
    } else if (Array.isArray(data)) {
      enrolledUsers.value = data
    } else {
      enrolledUsers.value = []
    }

    console.log('Enrolled users loaded:', enrolledUsers.value.length)

    if (enrolledUsers.value.length === 0) {
      console.log('No users found for this policy')
    }
  } catch (err) {
    console.error('Error loading enrolled users:', err)
    errorMessage.value = err instanceof Error ? err.message : 'Failed to load enrolled users'
    toast.error('Loading Failed', 'Failed to load enrolled users')
  } finally {
    isLoadingUsers.value = false
  }
}

// Watch for tab changes
watch(activeTab, (newTab) => {
  console.log('Tab changed to:', newTab)
  if (newTab === 'users' && enrolledUsers.value.length === 0 && !isLoadingUsers.value) {
    loadEnrolledUsers()
  }
})

const getStatusStyle = (status: string) => {
  const styles = {
    PENDING: 'bg-yellow-100 text-yellow-800',
    APPROVED: 'bg-green-100 text-green-800',
    REJECTED: 'bg-red-100 text-red-800',
    ACTIVE: 'bg-blue-100 text-blue-800',
    EXPIRED: 'bg-gray-100 text-gray-800',
  }
  return styles[status as keyof typeof styles] || styles.PENDING
}

const getStatusText = (status: string) => {
  const texts = {
    PENDING: 'Pending',
    APPROVED: 'Approved',
    REJECTED: 'Rejected',
    ACTIVE: 'Active',
    EXPIRED: 'Expired',
  }
  return texts[status as keyof typeof texts] || status
}

const exportUsers = () => {
  const csvContent =
    'data:text/csv;charset=utf-8,' +
    'Name,Email,Phone,Status,Premium,Start Date,End Date\n' +
    enrolledUsers.value
      .map(
        (user) =>
          `"${user.userName}","${user.userEmail}","${user.userPhone}","${user.status}","${user.premiumPaid}","${user.startDate}","${user.endDate}"`,
      )
      .join('\n')

  const encodedUri = encodeURI(csvContent)
  const link = document.createElement('a')
  link.setAttribute('href', encodedUri)
  link.setAttribute('download', `policy-${props.policy.id}-users.csv`)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)

  toast.success('Export Complete', 'User data has been exported to CSV')
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

// Initialize component
onMounted(() => {
  // console.log('ViewPolicyModal mounted with policy:', props.policy)
  if (activeTab.value === 'users') {
    loadEnrolledUsers()
  }
})
</script>

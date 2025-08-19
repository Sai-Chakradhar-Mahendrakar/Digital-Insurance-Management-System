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

          <!-- Policies Table -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div class="p-6 border-b border-slate-200">
              <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-slate-900">All Policies</h3>
                <div class="flex items-center space-x-4">
                  <div class="relative">
                    <Search
                      class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-400"
                    />
                    <input
                      v-model="searchQuery"
                      type="text"
                      placeholder="Search policies..."
                      class="pl-10 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-slate-50">
                  <tr>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Policy
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Type
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Premium (₹)
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Coverage (₹)
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Duration
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-200">
                  <tr v-for="policy in filteredPolicies" :key="policy.id" class="hover:bg-slate-50">
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div class="text-sm font-medium text-slate-900">{{ policy.name }}</div>
                        <div class="text-sm text-slate-500 truncate max-w-xs">
                          {{ policy.description }}
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                      >
                        {{ policy.type }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ formatINR(policy.premiumAmount) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ formatINR(policy.coverageAmount) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ policy.durationMonths }} months
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex items-center space-x-3">
                        <button
                          @click="editPolicy(policy)"
                          class="text-blue-600 hover:text-blue-900 transition-colors"
                          title="Edit policy"
                        >
                          <Edit class="w-4 h-4" />
                        </button>
                        <button
                          @click="confirmDeletePolicy(policy)"
                          class="text-red-600 hover:text-red-900 transition-colors"
                          title="Delete policy"
                        >
                          <Trash2 class="w-4 h-4" />
                        </button>
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

    <!-- Add/Edit Policy Modal -->
    <AddPolicyModal
      v-if="showAddPolicyModal"
      :policy="selectedPolicy"
      @close="closeModal"
      @save="handleSavePolicy"
    />

    <!-- Delete Confirmation Modal -->
    <ConfirmDialog
      v-if="showDeleteConfirm"
      :title="`Delete ${policyToDelete?.name}`"
      :message="`Are you sure you want to delete this policy? This action cannot be undone.`"
      confirm-text="Delete"
      cancel-text="Cancel"
      variant="danger"
      @confirm="handleDeletePolicy"
      @cancel="cancelDelete"
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
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import InlineError from '@/components/common/InlineError.vue'
import ToastContainer from '@/components/common/ToastContainer.vue'
import AppButton from '@/components/common/AppButton.vue'
import { Plus, Search, Edit, Trash2, CheckCircle, X } from 'lucide-vue-next'
import type { Policy } from '@/types/policy'

const adminPolicyStore = useAdminPolicyStore()
const toast = useToast()

const showAddPolicyModal = ref(false)
const selectedPolicy = ref<Policy | null>(null)
const searchQuery = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const showDeleteConfirm = ref(false)
const policyToDelete = ref<Policy | null>(null)

const policies = computed(() => adminPolicyStore.policies)

const filteredPolicies = computed(() => {
  if (!searchQuery.value.trim()) return policies.value

  const query = searchQuery.value.toLowerCase()
  return policies.value.filter(
    (policy) =>
      policy.name.toLowerCase().includes(query) ||
      policy.type.toLowerCase().includes(query) ||
      policy.description.toLowerCase().includes(query),
  )
})

const formatINR = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount)
}

const editPolicy = (policy: Policy) => {
  selectedPolicy.value = policy
  showAddPolicyModal.value = true
}

const confirmDeletePolicy = (policy: Policy) => {
  policyToDelete.value = policy
  showDeleteConfirm.value = true
}

const handleDeletePolicy = async () => {
  if (!policyToDelete.value) return

  try {
    await adminPolicyStore.deletePolicy(policyToDelete.value.id)
    toast.success('Policy Deleted', `"${policyToDelete.value.name}" has been successfully deleted.`)
    cancelDelete()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : 'Failed to delete policy'
    toast.error('Delete Failed', message)
    cancelDelete()
  }
}

const cancelDelete = () => {
  showDeleteConfirm.value = false
  policyToDelete.value = null
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

const clearError = () => {
  errorMessage.value = ''
}

const clearSuccess = () => {
  successMessage.value = ''
}

onMounted(async () => {
  try {
    await adminPolicyStore.fetchAdminPolicies()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : 'Failed to load policies'
    errorMessage.value = message
    toast.error('Loading Failed', message)
  }
})
</script>

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
        class="inline-block w-full max-w-2xl p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-xl"
      >
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-2xl font-bold text-slate-900 font-poppins">
            {{ isEditing ? 'Edit Policy' : 'Add New Policy' }}
          </h3>
          <button
            @click="$emit('close')"
            class="text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Policy Name -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Policy Name</label>
            <input
              v-model="form.name"
              type="text"
              required
              class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
              placeholder="e.g., Comprehensive Health Insurance"
            />
          </div>

          <!-- Policy Type -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Policy Type</label>
            <select
              v-model="form.type"
              required
              class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            >
              <option value="">Select Policy Type</option>
              <option value="Health">Health Insurance</option>
              <option value="Life">Life Insurance</option>
              <option value="Auto">Motor Insurance</option>
              <option value="Home">Home Insurance</option>
              <option value="Travel">Travel Insurance</option>
            </select>
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Description</label>
            <textarea
              v-model="form.description"
              required
              rows="3"
              class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
              placeholder="Describe the policy coverage and benefits"
            ></textarea>
          </div>

          <!-- Financial Details Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Premium Amount -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-2"
                >Premium Amount (₹)</label
              >
              <input
                v-model.number="form.premiumAmount"
                type="number"
                required
                min="1"
                step="0.01"
                class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                placeholder="50000"
              />
            </div>

            <!-- Coverage Amount -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-2"
                >Coverage Amount (₹)</label
              >
              <input
                v-model.number="form.coverageAmount"
                type="number"
                required
                min="1"
                step="0.01"
                class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                placeholder="1000000"
              />
            </div>

            <!-- Duration -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-2">Duration (Months)</label>
              <input
                v-model.number="form.durationMonths"
                type="number"
                required
                min="1"
                max="240"
                class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                placeholder="12"
              />
            </div>

            <!-- Renewal Premium -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-2"
                >Renewal Premium (₹)</label
              >
              <input
                v-model.number="form.renewalPremiumRate"
                type="number"
                required
                min="1"
                step="0.01"
                class="w-full px-4 py-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                placeholder="48000"
              />
            </div>
          </div>

          <!-- Error Message -->
          <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-lg">
            <p class="text-red-600 text-sm">{{ errorMessage }}</p>
          </div>

          <!-- Form Actions -->
          <div class="flex justify-end space-x-4 pt-6 border-t border-slate-200">
            <AppButton type="button" variant="ghost" @click="$emit('close')"> Cancel </AppButton>
            <AppButton
              type="submit"
              variant="primary"
              :disabled="isLoading"
              class="flex items-center space-x-2"
            >
              <span v-if="!isLoading">
                {{ isEditing ? 'Update Policy' : 'Create Policy' }}
              </span>
              <span v-else class="flex items-center space-x-2">
                <div
                  class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
                ></div>
                <span>{{ isEditing ? 'Updating...' : 'Creating...' }}</span>
              </span>
            </AppButton>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import AppButton from '@/components/common/AppButton.vue'
import { X } from 'lucide-vue-next'
import type { Policy } from '@/types/policy'

interface Props {
  policy?: Policy | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', policyData: any): void
}>()

const isEditing = computed(() => !!props.policy)
const isLoading = ref(false)
const errorMessage = ref('')

const form = reactive({
  name: '',
  type: '',
  description: '',
  premiumAmount: 0,
  coverageAmount: 0,
  durationMonths: 12,
  renewalPremiumRate: 0,
})

// Watch for policy prop changes to populate form
watch(
  () => props.policy,
  (policy) => {
    if (policy) {
      Object.assign(form, {
        name: policy.name,
        type: policy.type,
        description: policy.description,
        premiumAmount: policy.premiumAmount,
        coverageAmount: policy.coverageAmount,
        durationMonths: policy.durationMonths,
        renewalPremiumRate: policy.renewalPremiumRate,
      })
    }
  },
  { immediate: true },
)

const handleSubmit = async () => {
  try {
    isLoading.value = true
    errorMessage.value = ''

    emit('save', { ...form })
  } catch (error: any) {
    errorMessage.value = error.message || 'Failed to save policy'
  } finally {
    isLoading.value = false
  }
}
</script>

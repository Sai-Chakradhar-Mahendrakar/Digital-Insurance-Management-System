// src/stores/adminPolicy.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import type { Policy } from '@/types/policy'

export interface CreatePolicyRequest {
  name: string
  type: string
  description: string
  premiumAmount: number
  coverageAmount: number
  durationMonths: number
  renewalPremiumRate: number
}

export const useAdminPolicyStore = defineStore('adminPolicy', () => {
  const authStore = useAuthStore()
  const policies = ref<Policy[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const createPolicy = async (policyData: CreatePolicyRequest) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/admin/policies', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        body: JSON.stringify(policyData),
      })

      if (!response.ok) {
        throw new Error('Failed to create policy')
      }

      const newPolicy: Policy = await response.json()
      policies.value.push(newPolicy)
      return newPolicy
    } catch (err) {
      error.value = 'Failed to create policy'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchAdminPolicies = async () => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/admin/policies', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
      })

      if (!response.ok) {
        throw new Error('Failed to fetch admin policies')
      }

      const data = await response.json()
      policies.value = data.policies || data
    } catch (err) {
      error.value = 'Failed to load admin policies'
    } finally {
      isLoading.value = false
    }
  }

  const updatePolicy = async (id: number, policyData: Partial<CreatePolicyRequest>) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/admin/policies/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        body: JSON.stringify(policyData),
      })

      if (!response.ok) {
        throw new Error('Failed to update policy')
      }

      const updatedPolicy: Policy = await response.json()
      const index = policies.value.findIndex((p) => p.id === id)
      if (index !== -1) {
        policies.value[index] = updatedPolicy
      }
      return updatedPolicy
    } catch (err) {
      error.value = 'Failed to update policy'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const deletePolicy = async (id: number) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/admin/policies/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
      })

      if (!response.ok) {
        throw new Error('Failed to delete policy')
      }

      policies.value = policies.value.filter((p) => p.id !== id)
    } catch (err) {
      error.value = 'Failed to delete policy'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    policies,
    isLoading,
    error,
    createPolicy,
    fetchAdminPolicies,
    updatePolicy,
    deletePolicy,
  }
})

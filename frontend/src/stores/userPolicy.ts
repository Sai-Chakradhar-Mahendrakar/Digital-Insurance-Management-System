import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useAuthStore } from './auth'

export interface PolicyPurchase {
  policyId: number
  premiumPaid: number
}

export interface UserPolicy {
  id: number
  policyId: number
  policyName: string
  policyType: 'Health' | 'Auto' | 'Life' | 'Home'
  startDate: string
  endDate: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ACTIVE' | 'EXPIRED'
  premiumPaid: number
  totalAmountClaimed: number | null
  userId: number
  userName: string
  userEmail: string
  userPhone: string
  userAddress: string
}

// API Response structure matching your curl response
export interface UserPoliciesApiResponse {
  userPolicies: UserPolicy[]
  totalElements: number
  totalPages: number
  size: number
  page: number
}

// Legacy response structure (keeping for backward compatibility)
export interface UserPoliciesResponse {
  content: UserPolicy[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      empty: boolean
      sorted: boolean
      unsorted: boolean
    }
    offset: number
    paged: boolean
    unpaged: boolean
  }
  last: boolean
  totalPages: number
  totalElements: number
  first: boolean
  size: number
  number: number
  numberOfElements: number
  sort: {
    empty: boolean
    sorted: boolean
    unsorted: boolean
  }
  empty: boolean
}

export const useUserPolicyStore = defineStore('userPolicy', () => {
  const authStore = useAuthStore()

  // State
  const userPolicies = ref<UserPolicy[]>([])
  const purchasedPolicyIds = ref<Set<number>>(new Set())
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const totalElements = ref(0)
  const totalPages = ref(0)
  const currentPage = ref(0)
  const pageSize = ref(1000)

  // Computed getters (matching the design system patterns)
  const activePolicies = computed(() =>
    userPolicies.value.filter((policy) => policy.status === 'ACTIVE'),
  )

  const expiredPolicies = computed(() =>
    userPolicies.value.filter((policy) => policy.status === 'EXPIRED'),
  )

  const pendingPolicies = computed(() =>
    userPolicies.value.filter((policy) => policy.status === 'PENDING'),
  )

  const totalPremiumPaid = computed(() =>
    userPolicies.value.reduce((sum, policy) => sum + policy.premiumPaid, 0),
  )

  const totalClaimsAmount = computed(() =>
    userPolicies.value.reduce((sum, policy) => sum + (policy.totalAmountClaimed || 0), 0),
  )

  const policiesByType = computed(() => {
    return userPolicies.value.reduce(
      (acc, policy) => {
        if (!acc[policy.policyType]) {
          acc[policy.policyType] = []
        }
        acc[policy.policyType].push(policy)
        return acc
      },
      {} as Record<string, UserPolicy[]>,
    )
  })

  const policyTypeStats = computed(() => {
    const stats = {
      Health: { count: 0, totalPremium: 0 },
      Auto: { count: 0, totalPremium: 0 },
      Life: { count: 0, totalPremium: 0 },
      Home: { count: 0, totalPremium: 0 },
    }

    userPolicies.value.forEach((policy) => {
      if (stats[policy.policyType as keyof typeof stats]) {
        stats[policy.policyType as keyof typeof stats].count++
        stats[policy.policyType as keyof typeof stats].totalPremium += policy.premiumPaid
      }
    })

    return stats
  })

  // Actions
  const purchasePolicy = async (purchaseData: PolicyPurchase) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/user/policy/purchase', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
        body: JSON.stringify(purchaseData),
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Failed to purchase policy: ${response.status} ${errorText}`)
      }

      const result = await response.json()

      // Add the policy ID to purchased set
      purchasedPolicyIds.value.add(purchaseData.policyId)

      // Refresh user policies after purchase
      await fetchUserPolicies()

      return result
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to purchase policy'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchUserPolicies = async (size = 1000, page = 0) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(
        `http://localhost:8080/user/policies?size=${size}&page=${page}`,
        {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${authStore.token}`,
            Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
          },
          credentials: 'include',
        },
      )

      if (!response.ok) {
        throw new Error(`Failed to fetch user policies: ${response.status}`)
      }

      const data: UserPoliciesApiResponse = await response.json()

      // Handle the new API structure
      userPolicies.value = data.userPolicies || []
      totalElements.value = data.totalElements || 0
      totalPages.value = data.totalPages || 0
      currentPage.value = data.page || 0
      pageSize.value = data.size || 1000

      // Update purchased policy IDs set
      purchasedPolicyIds.value = new Set(userPolicies.value.map((policy) => policy.policyId))
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load user policies'
    } finally {
      isLoading.value = false
    }
  }

  const refreshPolicies = () => fetchUserPolicies(pageSize.value, currentPage.value)

  const isPolicyPurchased = (policyId: number): boolean => {
    return purchasedPolicyIds.value.has(policyId)
  }

  const getPolicyById = (policyId: number): UserPolicy | undefined => {
    return userPolicies.value.find((policy) => policy.policyId === policyId)
  }

  const getPoliciesByStatus = (status: UserPolicy['status']): UserPolicy[] => {
    return userPolicies.value.filter((policy) => policy.status === status)
  }

  const getPoliciesByType = (type: UserPolicy['policyType']): UserPolicy[] => {
    return userPolicies.value.filter((policy) => policy.policyType === type)
  }

  // Utility methods
  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
    }).format(amount)
  }

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })
  }

  const isExpiringSoon = (policy: UserPolicy, days = 30): boolean => {
    const endDate = new Date(policy.endDate)
    const now = new Date()
    const timeDiff = endDate.getTime() - now.getTime()
    const daysDiff = timeDiff / (1000 * 3600 * 24)
    return daysDiff <= days && daysDiff > 0
  }

  return {
    // State
    userPolicies,
    purchasedPolicyIds,
    isLoading,
    error,
    totalElements,
    totalPages,
    currentPage,
    pageSize,

    // Computed getters
    activePolicies,
    expiredPolicies,
    pendingPolicies,
    totalPremiumPaid,
    totalClaimsAmount,
    policiesByType,
    policyTypeStats,

    // Actions
    purchasePolicy,
    fetchUserPolicies,
    refreshPolicies,
    isPolicyPurchased,
    getPolicyById,
    getPoliciesByStatus,
    getPoliciesByType,

    // Utilities
    formatCurrency,
    formatDate,
    isExpiringSoon,
  }
})

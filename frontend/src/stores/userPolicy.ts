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
  policyType: 'Health' | 'Auto' | 'Life' | 'Home' | 'Travel' | 'Motor' 
  startDate: string
  endDate: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ACTIVE' | 'EXPIRED'
  premiumPaid: number
  totalAmountClaimed: number | null
  coverageAmount: number 
  userId: number
  userName: string
  userEmail: string
  userPhone: string
  userAddress: string
}

// API Response structure matching your latest curl response
export interface UserPoliciesApiResponse {
  userPolicies: UserPolicy[]
  totalElements: number
  totalPages: number
  size: number
  page: number
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

  // Computed getters
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

  const totalCoverageAmount = computed(() =>
    userPolicies.value.reduce((sum, policy) => sum + policy.coverageAmount, 0),
  )

  const totalClaimsAmount = computed(() =>
    userPolicies.value.reduce((sum, policy) => sum + (policy.totalAmountClaimed || 0), 0),
  )

  const availableCoverageAmount = computed(() =>
    userPolicies.value.reduce((sum, policy) => {
      const claimed = policy.totalAmountClaimed || 0
      return sum + (policy.coverageAmount - claimed)
    }, 0),
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
      Health: { count: 0, totalPremium: 0, totalCoverage: 0 },
      Auto: { count: 0, totalPremium: 0, totalCoverage: 0 },
      Life: { count: 0, totalPremium: 0, totalCoverage: 0 },
      Home: { count: 0, totalPremium: 0, totalCoverage: 0 },
    }

    userPolicies.value.forEach((policy) => {
      if (stats[policy.policyType as keyof typeof stats]) {
        const typeStats = stats[policy.policyType as keyof typeof stats]
        typeStats.count++
        typeStats.totalPremium += policy.premiumPaid
        typeStats.totalCoverage += policy.coverageAmount
      }
    })

    return stats
  })

  const averageClaimsUtilization = computed(() => {
    if (userPolicies.value.length === 0) return 0

    const totalUtilization = userPolicies.value.reduce((sum, policy) => {
      const coverage = policy.coverageAmount
      const claimed = policy.totalAmountClaimed || 0
      return sum + (coverage > 0 ? (claimed / coverage) * 100 : 0)
    }, 0)

    return Math.round(totalUtilization / userPolicies.value.length)
  })

  const highUtilizationPolicies = computed(() =>
    userPolicies.value.filter((policy) => {
      const claimed = policy.totalAmountClaimed || 0
      const utilizationPercentage =
        policy.coverageAmount > 0 ? (claimed / policy.coverageAmount) * 100 : 0
      return utilizationPercentage > 75
    }),
  )

  const expiringSoonPolicies = computed(() => {
    const thirtyDaysFromNow = new Date()
    thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() + 30)

    return userPolicies.value.filter((policy) => {
      const endDate = new Date(policy.endDate)
      const now = new Date()
      return policy.status === 'ACTIVE' && endDate <= thirtyDaysFromNow && endDate > now
    })
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

      // Handle the API structure
      userPolicies.value = data.userPolicies || []
      totalElements.value = data.totalElements || 0
      totalPages.value = data.totalPages || 0
      currentPage.value = data.page || 0
      pageSize.value = data.size || 1000

      // Update purchased policy IDs set
      purchasedPolicyIds.value = new Set(userPolicies.value.map((policy) => policy.policyId))
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load user policies'
      console.error('User policies fetch error:', err)
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
    // Handle large amounts properly
    if (amount >= 10000000000) {
      // 1000 crores
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
        notation: 'compact',
        compactDisplay: 'short',
      }).format(amount)
    }

    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(amount)
  }

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-IN', {
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

  const getClaimsUtilizationPercentage = (policy: UserPolicy): number => {
    const claimed = policy.totalAmountClaimed || 0
    if (policy.coverageAmount === 0) return 0
    return Math.round((claimed / policy.coverageAmount) * 100)
  }

  const getAvailableCoverage = (policy: UserPolicy): number => {
    const claimed = policy.totalAmountClaimed || 0
    return Math.max(0, policy.coverageAmount - claimed)
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
    totalCoverageAmount,
    totalClaimsAmount,
    availableCoverageAmount,
    policiesByType,
    policyTypeStats,
    averageClaimsUtilization,
    highUtilizationPolicies,
    expiringSoonPolicies,

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
    getClaimsUtilizationPercentage,
    getAvailableCoverage,
  }
})

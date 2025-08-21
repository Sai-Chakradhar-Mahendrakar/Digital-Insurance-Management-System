export interface Policy {
  id: number
  name: string
  type: string
  description: string
  premiumAmount: number
  coverageAmount: number
  createdAt: string
  renewalPremiumRate: number
  durationMonths: number
}

export interface PolicyResponse {
  policies: Policy[]
  totalElements: number
  totalPages: number
  size: number
  page: number
}

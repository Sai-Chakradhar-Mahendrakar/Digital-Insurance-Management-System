// src/types/admin.ts
export interface PolicyUser {
  id: number
  policyId: number
  policyName: string
  policyType: string
  startDate: string
  endDate: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ACTIVE' | 'EXPIRED'
  premiumPaid: number
  userId: number
  userName: string
  userEmail: string
  userPhone: string
  userAddress: string
}

export interface PolicyUsersResponse {
  content: PolicyUser[]
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

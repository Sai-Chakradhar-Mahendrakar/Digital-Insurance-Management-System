<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-slate-100">
    <!-- Navigation Header -->
    <AppNavbar />

    <!-- Hero Section -->
    <main class="pt-16 pb-8">
      <div class="max-w-7xl mx-auto px-4 lg:px-8">
        <div class="text-center">
          <h1 class="text-4xl lg:text-6xl font-bold text-slate-900 font-poppins mb-6">
            Digital Insurance
            <span class="block text-blue-700">Management System</span>
          </h1>
          <p class="text-xl text-slate-600 mb-12 max-w-3xl mx-auto font-inter">
            Streamline your insurance operations with our comprehensive digital platform. Manage
            policies, track claims, and serve customers efficiently.
          </p>

          <!-- Search Bar -->
          <div class="max-w-2xl mx-auto mb-12">
            <div class="relative">
              <Search
                class="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-slate-400"
              />
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Search policies, claims, or help topics..."
                class="w-full pl-12 pr-4 py-4 border border-slate-200 rounded-xl text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-4 focus:ring-blue-100 focus:outline-none transition-all duration-200 ease-in-out font-inter shadow-sm"
                @keyup.enter="handleSearch"
              />
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex flex-col sm:flex-row gap-4 justify-center mb-12">
            <AppButton
              variant="primary"
              size="large"
              @click="navigateTo('/policies')"
              class="flex items-center space-x-2"
            >
              <Shield class="w-5 h-5" />
              <span>View My Policies</span>
            </AppButton>
          </div>

          <!-- Feature Cards -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mt-12">
            <FeatureCard
              title="Policy Management"
              description="Comprehensive policy lifecycle management from creation to renewal"
              icon="shield"
            />
            <FeatureCard
              title="Secure Access"
              description="Multi-layered security with role-based access control"
              icon="lock"
            />
            <FeatureCard
              title="Digital First"
              description="Paperless operations with automated workflows"
              icon="smartphone"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppButton from '@/components/common/AppButton.vue'
import FeatureCard from '@/components/common/FeatureCard.vue'
import { Shield, Search, Plus } from 'lucide-vue-next'

const router = useRouter()
const searchQuery = ref('')

const navigateTo = (path: string) => {
  router.push(path)
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push(`/policies?search=${encodeURIComponent(searchQuery.value)}`)
  }
}
</script>

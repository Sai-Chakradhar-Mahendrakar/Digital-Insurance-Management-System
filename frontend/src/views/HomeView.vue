<!-- src/views/HomeView.vue -->
<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-slate-100">
    <!-- Navigation Header -->
    <header class="bg-white/80 backdrop-blur-md border-b border-slate-200 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 lg:px-8 py-4">
        <nav class="flex items-center justify-between">
          <div class="flex items-center space-x-2">
            <div class="w-8 h-8 bg-blue-700 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-sm">DI</span>
            </div>
            <h1 class="text-xl font-semibold text-slate-900 font-poppins">Digital Insurance</h1>
          </div>
          <div class="flex items-center space-x-4">
            <AppButton variant="ghost" @click="navigateTo('/login')" class="hidden md:inline-flex">
              Login
            </AppButton>
            <AppButton variant="primary" @click="navigateTo('/register')"> Get Started </AppButton>
          </div>
        </nav>
      </div>
    </header>

    <!-- Hero Section -->
    <main class="pt-16 pb-24">
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

          <!-- Action Buttons -->
          <div class="flex flex-col sm:flex-row gap-4 justify-center mb-16">
            <AppButton
              variant="primary"
              size="large"
              @click="navigateTo('/policies')"
              class="flex items-center space-x-2"
            >
              <Shield class="w-5 h-5" />
              <span>View Policies</span>
            </AppButton>
            <AppButton
              variant="secondary"
              size="large"
              @click="navigateTo('/login')"
              class="flex items-center space-x-2"
            >
              <LogIn class="w-5 h-5" />
              <span>Login</span>
            </AppButton>
          </div>

          <!-- Feature Cards -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mt-16">
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
import { useRouter } from 'vue-router'
import AppButton from '@/components/common/AppButton.vue'
import FeatureCard from '@/components/common/FeatureCard.vue'
import { Shield, LogIn } from 'lucide-vue-next'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

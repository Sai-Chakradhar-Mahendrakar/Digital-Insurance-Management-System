<!-- src/views/NotFoundView.vue -->
<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center px-4">
    <div class="max-w-md w-full text-center">
      <!-- 404 Number -->
      <h1 class="text-9xl font-bold text-gray-200 mb-4">404</h1>

      <!-- Error Message -->
      <h2 class="text-2xl font-semibold text-gray-900 mb-2">Page Not Found</h2>
      <p class="text-gray-600 mb-8">Sorry, the page you are looking for doesn't exist.</p>

      <!-- Action Buttons -->
      <div class="space-y-3">
        <button
          @click="goHome"
          class="w-full bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors"
        >
          Go Home
        </button>

        <button
          @click="goBack"
          class="w-full bg-gray-200 text-gray-800 px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors"
        >
          Go Back
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<!-- src/views/HomeView.vue -->
<template>
  <main>
    <TheWelcome />
  </main>
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

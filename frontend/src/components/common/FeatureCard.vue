<!-- src/components/common/FeatureCard.vue -->
<template>
  <div
    class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 hover:shadow-md transition-shadow duration-200 ease-in-out"
  >
    <div class="flex flex-col items-center text-center">
      <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center mb-4">
        <component :is="iconComponent" class="w-6 h-6 text-blue-700" />
      </div>
      <h3 class="text-xl font-semibold text-slate-900 mb-2 font-poppins">{{ title }}</h3>
      <p class="text-slate-600 font-inter">{{ description }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Shield, Lock, Smartphone } from 'lucide-vue-next'

interface Props {
  title: string
  description: string
  icon: 'shield' | 'lock' | 'smartphone'
}

const props = defineProps<Props>()

const iconComponent = computed(() => {
  const icons = {
    shield: Shield,
    lock: Lock,
    smartphone: Smartphone,
  }
  return icons[props.icon]
})
</script>

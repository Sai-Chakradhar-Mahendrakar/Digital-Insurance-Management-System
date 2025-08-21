<!-- src/components/admin/StatsCard.vue -->
<template>
  <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
    <div class="flex items-center justify-between">
      <div>
        <p class="text-sm font-medium text-slate-600">{{ title }}</p>
        <p class="text-2xl font-bold text-slate-900 mt-1">{{ value }}</p>
        <p class="text-sm mt-2" :class="trendClass">
          {{ trend }}
        </p>
      </div>
      <div :class="iconBgClass" class="w-12 h-12 rounded-lg flex items-center justify-center">
        <component :is="iconComponent" :class="iconClass" class="w-6 h-6" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Shield, Users, FileText, TrendingUp } from 'lucide-vue-next'

interface Props {
  title: string
  value: string
  trend?: string
  icon: string
  color: 'blue' | 'green' | 'yellow' | 'purple'
}

const props = defineProps<Props>()
const safeTrend = computed(() => props.trend ?? '')

const iconComponent = computed(() => {
  const icons: Record<string, any> = {
    shield: Shield,
    users: Users,
    'file-text': FileText,
    'trending-up': TrendingUp,
  }
  return icons[props.icon as keyof typeof icons] || Shield
})

const trendClass = computed(() => {
  if (safeTrend.value.includes('+')) return 'text-green-600'
  if (safeTrend.value.includes('-')) return 'text-red-600'
  return 'text-slate-500'
})

const iconBgClass = computed(() => {
  const colors = {
    blue: 'bg-blue-100',
    green: 'bg-green-100',
    yellow: 'bg-yellow-100',
    purple: 'bg-purple-100',
  }
  return colors[props.color]
})

const iconClass = computed(() => {
  const colors = {
    blue: 'text-blue-600',
    green: 'text-green-600',
    yellow: 'text-yellow-600',
    purple: 'text-purple-600',
  }
  return colors[props.color]
})
</script>

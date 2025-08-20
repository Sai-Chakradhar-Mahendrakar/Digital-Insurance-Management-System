<template>
    <div class="relative">
      <input
        v-model="query"
        @input="onSearch"
        type="text"
        :placeholder="placeholder"
        class="pl-10 pr-4 py-2 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-200 font-medium rounded-lg border border-slate-300 dark:border-slate-600 focus:outline-none focus:ring-2 focus:ring-blue-500 transition w-full"
      />
      <svg
        class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-400 dark:text-slate-500"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
        />
      </svg>
    </div>
  </template>
    
  <script setup>
  import { ref } from 'vue'
  
  const props = defineProps({
    placeholder: { type: String, default: 'Search...' }
  })
  const emits = defineEmits(['search'])
  
  const query = ref('')
  
  function onSearch() {
    emits('search', query.value)
  }
  </script>
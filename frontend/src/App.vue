<template>
  <div class="min-h-screen bg-slate-50">
    <router-view />
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import ToastContainer from '@/components/common/ToastContainer.vue'
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&display=swap');
</style>

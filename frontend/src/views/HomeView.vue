<template>
  <div>
    <AppHeader />

    <!-- Hero Section -->
    <section class="bg-gradient-to-br from-blue-50 to-slate-100 dark:from-slate-800 dark:to-slate-900 py-16 lg:py-24">
      <div class="max-w-7xl mx-auto px-4 lg:px-8 text-center">
        <h1 class="font-poppins text-4xl lg:text-6xl font-bold text-slate-900 dark:text-slate-100">
          Build Modern UIs with <span class="text-blue-700">Vue & Tailwind</span>
        </h1>
        <p class="mt-6 text-lg text-slate-600 dark:text-slate-300 max-w-2xl mx-auto">
          A clean, professional design system inspired by Deel.com, optimized for enterprise-grade Vue applications.
        </p>
        <div class="mt-8 flex justify-center space-x-4">
          <RouterLink
            to="/signup"
            class="bg-blue-700 hover:bg-blue-800 text-white font-medium px-6 py-3 rounded-lg shadow-sm hover:shadow-md transition-all duration-200"
          >
            Get Started
          </RouterLink>
          <RouterLink
            to="/docs"
            class="border-2 border-blue-700 text-blue-700 hover:bg-blue-700 hover:text-white font-medium px-6 py-3 rounded-lg transition-all"
          >
            Learn More
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-12 lg:py-16 px-4 lg:px-8 max-w-7xl mx-auto">
      <h2 class="text-2xl lg:text-3xl font-semibold text-center font-poppins text-slate-900 dark:text-slate-100 mb-12">
        Why Choose Our Platform?
      </h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div class="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 p-6 hover:shadow-md hover:-translate-y-1 transition-all">
          <div class="w-12 h-12 flex items-center justify-center bg-blue-100 text-blue-700 rounded-lg mb-4">
            <SparklesIcon class="w-6 h-6" />
          </div>
          <h3 class="text-xl font-medium text-slate-800 dark:text-slate-100 mb-2">Modern UI</h3>
          <p class="text-slate-600 dark:text-slate-300">Clean, minimal, and professional design inspired by Deel.</p>
        </div>

        <div class="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 p-6 hover:shadow-md hover:-translate-y-1 transition-all">
          <div class="w-12 h-12 flex items-center justify-center bg-emerald-100 text-emerald-600 rounded-lg mb-4">
            <BoltIcon class="w-6 h-6" />
          </div>
          <h3 class="text-xl font-medium text-slate-800 dark:text-slate-100 mb-2">Fast Performance</h3>
          <p class="text-slate-600 dark:text-slate-300">Optimized for speed with Vue 3 Composition API and Tailwind.</p>
        </div>

        <div class="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 p-6 hover:shadow-md hover:-translate-y-1 transition-all">
          <div class="w-12 h-12 flex items-center justify-center bg-amber-100 text-amber-600 rounded-lg mb-4">
            <ShieldIcon class="w-6 h-6" />
          </div>
          <h3 class="text-xl font-medium text-slate-800 dark:text-slate-100 mb-2">Secure & Reliable</h3>
          <p class="text-slate-600 dark:text-slate-300">Enterprise-grade security and accessibility by design.</p>
        </div>
      </div>
    </section>
  </div>
</template>
npm install lucide-vue-next
<script setup>
import AppHeader from "@/components/layouts/AppHeader.vue"
import { SparklesIcon, BoltIcon, ShieldIcon } from "lucide-vue-next"
</script>

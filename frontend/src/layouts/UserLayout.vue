<script setup>
import Navbar from '../components/Navbar.vue'
import AIChatButton from '../components/AIChatButton.vue'
</script>

<template>
  <div class="min-h-screen bg-[#f7f8fc] flex flex-col items-center">
    <!-- Navbar -->
    <Navbar />

    <!-- Main Content -->
    <main class="w-full px-8 md:px-12 lg:px-20 py-8 pb-24 flex flex-col gap-6 max-w-[1200px] flex-1">
      <router-view />
    </main>

    <!-- AI Chat Floating Button -->
    <AIChatButton />
  </div>
</template>

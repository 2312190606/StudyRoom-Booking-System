<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const isAdmin = computed(() => localStorage.getItem('userRole') === 'admin')
const userName = computed(() => localStorage.getItem('userName') || '用户')

// 当前时间
const currentTime = ref('')
const currentDate = ref('')

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  currentDate.value = now.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric', weekday: 'long' })
}

let timer = null
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  localStorage.removeItem('userName')
  router.push('/login')
}
</script>

<template>
  <header class="w-full bg-white flex justify-center sticky top-0 z-50 shadow-[0_2px_10px_rgba(0,0,0,0.03)] border-b border-gray-100">
    <div class="w-full px-8 md:px-12 lg:px-20 h-16 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-2.5 text-[#5A52FF] font-black text-xl cursor-pointer" @click="router.push('/home')">
        <div class="w-8 h-8 bg-[#5A52FF] rounded-lg flex items-center justify-center shadow-md shadow-indigo-200">
          <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
        </div>
        <span class="text-gray-900 tracking-wide cursor-pointer">静学自习室</span>
      </div>

      <!-- Nav Links -->
      <nav class="hidden md:flex items-center gap-10 text-[14px] font-bold text-gray-400">
        <router-link to="/home" class="flex items-center gap-1.5 transition-colors" :class="route.path === '/home' ? 'text-[#5A52FF]' : 'hover:text-[#5A52FF]'">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path></svg>首页
        </router-link>
        <router-link to="/reservations" class="flex items-center gap-1.5 transition-colors" :class="route.path === '/reservations' ? 'text-[#5A52FF]' : 'hover:text-[#5A52FF]'">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>我的预约
        </router-link>
        <router-link to="/profile" class="flex items-center gap-1.5 transition-colors" :class="route.path === '/profile' ? 'text-[#5A52FF]' : 'hover:text-[#5A52FF]'">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>个人中心
        </router-link>
        <router-link v-if="isAdmin" to="/admin" class="flex items-center gap-1.5 transition-colors" :class="route.path.startsWith('/admin') ? 'text-[#5A52FF]' : 'hover:text-[#5A52FF]'">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>管理后台
        </router-link>
      </nav>

      <!-- User profile -->
      <div class="flex items-center gap-4 text-sm text-gray-600">
        <div class="text-right">
          <div class="font-bold text-gray-900 text-[13px]">{{ userName }}</div>
          <div class="text-[11px] text-gray-400 font-medium">{{ currentTime }}</div>
        </div>
        <button @click="handleLogout" class="text-gray-400 hover:text-[#5A52FF] transition-colors">
          <svg class="w-[22px] h-[22px]" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path></svg>
        </button>
      </div>
    </div>
  </header>
</template>

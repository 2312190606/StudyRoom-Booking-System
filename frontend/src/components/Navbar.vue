<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const showMobileMenu = ref(false)

const isAdmin = computed(() => localStorage.getItem('userRole') === 'admin')
const userName = computed(() => localStorage.getItem('userName') || '用户')

const currentTime = ref('')

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
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

const closeMobileMenu = () => {
  showMobileMenu.value = false
}
</script>

<template>
  <header class="w-full bg-white sticky top-0 z-50 shadow-sm border-b border-gray-100">
    <div class="w-full px-4 sm:px-6 md:px-8 h-14 md:h-16 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-2 text-[#5A52FF] font-black text-lg md:text-xl cursor-pointer" @click="router.push('/')">
        <div class="w-7 h-7 md:w-8 md:h-8 bg-[#5A52FF] rounded-lg flex items-center justify-center">
          <svg class="w-4 h-4 md:w-5 md:h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
        </div>
        <span class="hidden sm:inline text-gray-900">静学自习室</span>
      </div>

      <!-- Desktop Nav Links -->
      <nav class="hidden md:flex items-center gap-6 lg:gap-10 text-sm font-bold text-gray-400">
        <router-link to="/" class="flex items-center gap-1.5 transition-colors" :class="route.path === '/' ? 'text-[#5A52FF]' : 'hover:text-[#5A52FF]'">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path>
          </svg>
          首页
        </router-link>
        <router-link to="/reservations" class="flex items-center gap-1.5 transition-colors" :class="route.path === '/reservations' ? 'text-[#5A52FF]' : 'hover:text-[#5A52FF]'">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
          我的预约
        </router-link>
        <router-link to="/profile" class="flex items-center gap-1.5 transition-colors" :class="route.path === '/profile' ? 'text-[#5A52FF]' : 'hover:text-[#5A52FF]'">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
          </svg>
          个人中心
        </router-link>
        <router-link v-if="isAdmin" to="/admin" class="flex items-center gap-1.5 transition-colors" :class="route.path.startsWith('/admin') ? 'text-[#5A52FF]' : 'hover:text-[#5A52FF]'">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path>
          </svg>
          管理后台
        </router-link>
      </nav>

      <!-- Desktop User -->
      <div class="hidden md:flex items-center gap-3 text-sm text-gray-600">
        <div class="text-right">
          <div class="font-bold text-gray-900 text-[13px]">{{ userName }}</div>
          <div class="text-[11px] text-gray-400">{{ currentTime }}</div>
        </div>
        <button @click="handleLogout" class="text-gray-400 hover:text-red-500 transition">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
          </svg>
        </button>
      </div>

      <!-- Mobile Menu Button -->
      <button @click="showMobileMenu = !showMobileMenu" class="md:hidden p-2 text-gray-600 hover:text-[#5A52FF]">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16"></path>
        </svg>
      </button>
    </div>

    <!-- Mobile Menu -->
    <div v-if="showMobileMenu" class="md:hidden absolute top-full left-0 right-0 bg-white shadow-lg border-t border-gray-100 py-4 px-4">
      <nav class="flex flex-col gap-1 text-sm font-bold text-gray-600">
        <router-link @click="closeMobileMenu" to="/" class="flex items-center gap-2 p-3 rounded-lg transition-colors" :class="route.path === '/' ? 'bg-indigo-50 text-[#5A52FF]' : 'hover:bg-gray-50'">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path>
          </svg>
          首页
        </router-link>
        <router-link @click="closeMobileMenu" to="/reservations" class="flex items-center gap-2 p-3 rounded-lg transition-colors" :class="route.path === '/reservations' ? 'bg-indigo-50 text-[#5A52FF]' : 'hover:bg-gray-50'">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
          我的预约
        </router-link>
        <router-link @click="closeMobileMenu" to="/profile" class="flex items-center gap-2 p-3 rounded-lg transition-colors" :class="route.path === '/profile' ? 'bg-indigo-50 text-[#5A52FF]' : 'hover:bg-gray-50'">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
          </svg>
          个人中心
        </router-link>
        <router-link v-if="isAdmin" @click="closeMobileMenu" to="/admin" class="flex items-center gap-2 p-3 rounded-lg transition-colors" :class="route.path.startsWith('/admin') ? 'bg-indigo-50 text-[#5A52FF]' : 'hover:bg-gray-50'">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path>
          </svg>
          管理后台
        </router-link>
      </nav>
      <!-- Mobile User Info -->
      <div class="mt-4 pt-4 border-t border-gray-100 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div>
            <div class="font-bold text-gray-900 text-sm">{{ userName }}</div>
            <div class="text-xs text-gray-400">{{ currentTime }}</div>
          </div>
        </div>
        <button @click="handleLogout" class="flex items-center gap-2 px-4 py-2 text-sm text-gray-600 hover:text-red-500">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
          </svg>
          退出
        </button>
      </div>
    </div>
  </header>
</template>

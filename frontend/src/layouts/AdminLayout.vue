<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const menuItems = [
  { name: '自习室管理', path: '/admin/rooms', exact: false, icon: 'M17.657 16.657L13.414 20.9l-4.244-4.243a8 8 0 1111.314 0zM15 11a3 3 0 11-6 0 3 3 0 016 0z' },
  { name: '预约记录', path: '/admin/bookings', exact: false, icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z' },
  { name: '用户管理', path: '/admin/users', exact: false, icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' },
  { name: '公告管理', path: '/admin/announcements', exact: false, icon: 'M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9' },
  { name: '系统设置', path: '/admin/settings', exact: false, icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z' }
]

const isActive = (item) => {
  return item.exact ? route.path === item.path : route.path.startsWith(item.path)
}
</script>

<template>
  <div class="min-h-screen bg-[#0E121E] text-gray-200 flex overflow-hidden">
    <!-- Sidebar -->
    <aside class="w-[260px] bg-[#121624] border-r border-[#1C2136] flex flex-col justify-between shrink-0 h-screen sticky top-0 shadow-[4px_0_24px_rgba(0,0,0,0.2)] z-50">
      <div>
        <div class="h-[80px] flex items-center px-8 gap-3 border-b border-[#1C2136]">
          <div class="w-[32px] h-[32px] bg-[#5A52FF] rounded-lg flex items-center justify-center shadow-[0_0_12px_rgba(90,82,255,0.4)]">
             <svg class="w-[18px] h-[18px] text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
          </div>
          <div>
            <h1 class="text-[16px] font-black text-white leading-tight tracking-wide">管理系统</h1>
            <p class="text-[10px] text-[#5A52FF] font-black tracking-widest leading-none mt-1 uppercase">studyroom admin</p>
          </div>
        </div>
        
        <nav class="p-6 flex flex-col gap-2">
          <router-link v-for="item in menuItems" :key="item.path" :to="item.path"
            class="flex items-center gap-4 px-4 py-3.5 rounded-[12px] text-[14px] font-bold transition-all relative group overflow-hidden"
            :class="isActive(item) ? 'bg-[#21263c] text-white shadow-inner border border-[#303753]/60' : 'text-gray-400 hover:text-white hover:bg-[#1A1F30]'"
          >
            <div v-show="isActive(item)" class="absolute inset-0 bg-gradient-to-r from-[#5A52FF]/20 to-transparent"></div>
            <svg class="w-5 h-5 flex-shrink-0 relative z-10" :class="isActive(item) ? 'text-[#5A52FF]' : ''" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" :d="item.icon"></path></svg>
            <span class="relative z-10">{{ item.name }}</span>
            <div v-if="isActive(item)" class="ml-auto w-1.5 h-1.5 rounded-full bg-[#5A52FF] shadow-[0_0_6px_rgba(90,82,255,0.8)] relative z-10"></div>
          </router-link>
        </nav>
      </div>

      <div class="p-6 border-t border-[#1C2136] flex flex-col gap-2">
        <router-link to="/" class="flex items-center gap-4 px-4 py-3 rounded-[12px] text-[13.5px] font-bold text-gray-400 hover:text-white hover:bg-[#1A1F30] transition-all">
          <svg class="w-[18px] h-[18px]" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"></path></svg>
          返回前台
        </router-link>
        <button @click="router.push('/login')" class="flex items-center gap-4 px-4 py-3 rounded-[12px] text-[13.5px] font-bold text-red-500 hover:bg-red-500/10 transition-all w-full text-left">
          <svg class="w-[18px] h-[18px]" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path></svg>
          退出登录
        </button>
      </div>
    </aside>

    <!-- Main Content wrapper -->
    <div class="flex-1 flex flex-col h-screen overflow-hidden">
      <!-- Header -->
      <header class="h-[80px] bg-[#0E121E] border-b border-[#1C2136] flex items-center justify-between px-10 shrink-0">
        <h2 class="text-[1.35rem] font-black text-white tracking-wide">
          {{ menuItems.find(m => isActive(m))?.name || '管理系统' }}
        </h2>
        <div class="flex items-center gap-6">
          
          <div class="h-6 w-px bg-[#1C2136]"></div>

          <div class="flex items-center gap-3 cursor-pointer group">
            <div class="text-right">
              <div class="text-[13px] font-bold text-white group-hover:text-[#5A52FF] transition tracking-wide">Admin User</div>
              <div class="text-[11px] text-gray-500 font-bold tracking-widest mt-0.5">超级管理员</div>
            </div>
            <div class="w-10 h-10 rounded-full border-[2.5px] border-[#5A52FF] flex items-center justify-center bg-[#1A1F30] text-[#5A52FF] text-[13px] font-black tracking-tighter">
              AU
            </div>
          </div>
        </div>
      </header>
      
      <!-- Scrollable body -->
      <main class="flex-1 overflow-y-auto w-full custom-scrollbar">
        <div class="p-8 lg:p-10 w-full max-w-[1600px] mx-auto min-h-full">
          <router-view></router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 8px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #0E121E; 
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #1C2136; 
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #2a314d; 
}
</style>

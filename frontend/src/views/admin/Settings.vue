<script setup>
import { ref } from 'vue'
import { showLoadingToast, showSuccessToast, showFailToast } from 'vant'

const isChecking = ref(false)
const dbStatus = ref('online') // 'online', 'offline', 'error'
const lastCheckTime = ref(new Date().toLocaleString())

const checkDatabaseConnection = () => {
  isChecking.value = true
  showLoadingToast({
    message: '正在检测数据库连接...',
    forbidClick: true,
    duration: 1500
  })

  // Simulated connection check logic
  setTimeout(() => {
    isChecking.value = false
    const rand = Math.random()
    if (rand > 0.05) {
      dbStatus.value = 'online'
      lastCheckTime.value = new Date().toLocaleString()
      showSuccessToast('连接正常')
    } else {
      dbStatus.value = 'offline'
      showFailToast('数据库连接超时')
    }
  }, 1600)
}
</script>

<template>
  <div class="flex flex-col items-center justify-center gap-8 pb-10 min-h-[75vh]">
    <!-- Header -->
    <div class="flex flex-col items-center text-center gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">系统设置</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">监控与诊断数据库后端运行状态</p>
      </div>
    </div>

    <!-- DB Status Card -->
    <div class="max-w-3xl">
      <div class="bg-[#121624] border border-[#1C2136] rounded-[32px] p-10 flex flex-col items-center relative overflow-hidden group">
        <!-- Background Glow -->
        <div class="absolute -top-32 -left-32 w-80 h-80 bg-[#5A52FF]/5 rounded-full blur-[80px] opacity-0 group-hover:opacity-100 transition-opacity"></div>
        
        <div class="z-10 flex flex-col items-center text-center">
          <!-- Status Icon -->
          <div :class="{
            'bg-emerald-500/10 text-emerald-500 shadow-[0_0_50px_rgba(16,185,129,0.1)]': dbStatus === 'online',
            'bg-red-500/10 text-red-500 shadow-[0_0_50px_rgba(239,68,68,0.1)]': dbStatus === 'offline'
          }" class="w-32 h-32 rounded-full flex items-center justify-center mb-8 relative transition-all duration-500">
            <!-- Pulsing line for online -->
            <div v-if="dbStatus === 'online'" class="absolute inset-0 rounded-full bg-emerald-500/20 animate-ping opacity-20"></div>
            
            <svg v-if="dbStatus === 'online'" class="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
            <svg v-else class="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
          </div>

          <h3 class="text-3xl font-black text-white mb-3 tracking-wide">
            {{ dbStatus === 'online' ? '数据库连接正常' : '数据库连接异常' }}
          </h3>
          <p class="text-gray-550 font-bold mb-10 tracking-widest text-xs uppercase opacity-60">Database Connection Monitor</p>
          
          <div class="flex flex-col gap-3 w-full max-w-sm">
            <div class="bg-[#0B0F19] rounded-2xl p-5 border border-[#1C2136] flex items-center justify-between">
              <span class="text-[12px] font-black text-gray-500 uppercase tracking-widest">当前状态</span>
              <div class="flex items-center gap-2">
                <div :class="dbStatus === 'online' ? 'bg-emerald-500' : 'bg-red-500'" class="w-2 h-2 rounded-full"></div>
                <span class="text-[14px] font-black" :class="dbStatus === 'online' ? 'text-white' : 'text-red-500'">{{ dbStatus === 'online' ? 'SUCCESS' : 'FAILED' }}</span>
              </div>
            </div>
            <div class="bg-[#0B0F19] rounded-2xl p-5 border border-[#1C2136] flex items-center justify-between">
              <span class="text-[12px] font-black text-gray-500 uppercase tracking-widest">上次检测</span>
              <span class="text-[14px] font-black text-white">{{ lastCheckTime }}</span>
            </div>
          </div>

          <button @click="checkDatabaseConnection" :disabled="isChecking" class="mt-12 w-full max-w-xs bg-[#5A52FF] hover:bg-[#4a42e5] text-white py-5 rounded-[22px] text-[16px] font-black shadow-[0_10px_30px_rgba(90,82,255,0.2)] hover:shadow-[0_15px_40px_rgba(90,82,255,0.4)] transition-all flex items-center justify-center gap-4 group/btn disabled:opacity-50 border-none cursor-pointer">
            <svg :class="isChecking ? 'animate-spin' : 'group-hover/btn:rotate-180'" class="w-5 h-5 transition-transform duration-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
            立即重新检测
          </button>
        </div>
      </div>
    </div>
    
  </div>
</template>

<style scoped>
.animate-ping {
  animation: ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite;
}

@keyframes ping {
  75%, 100% {
    transform: scale(1.6);
    opacity: 0;
  }
}
</style>

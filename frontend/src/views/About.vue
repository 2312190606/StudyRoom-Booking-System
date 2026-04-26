<script setup>
</script>

<template>
  <div class="w-full flex flex-col gap-6 max-w-[1200px] items-center">
      <!-- App Identity Section -->
      <div class="w-full bg-white rounded-[2.5rem] p-12 md:p-20 shadow-[0_10px_60px_rgba(0,0,0,0.03)] flex flex-col items-center relative overflow-hidden text-center">
        <!-- Background Decorations -->
        <div class="absolute -top-40 -left-40 w-96 h-96 bg-[#f0f4ff] rounded-full blur-[80px] opacity-60"></div>
        <div class="absolute -bottom-40 -right-40 w-96 h-96 bg-purple-50 rounded-full blur-[80px] opacity-60"></div>

        <div class="z-10 flex flex-col items-center scale-up">
          <div class="w-32 h-32 rounded-[2.5rem] bg-[#5A52FF] flex items-center justify-center shadow-2xl shadow-indigo-200 mb-8 transform transition-transform hover:scale-110">
            <svg class="w-16 h-16 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path></svg>
          </div>
          <h1 class="text-4xl font-black text-gray-900 mb-2 tracking-tight">静学自习室</h1>
          <span class="text-indigo-500 font-black tracking-widest text-xs uppercase mb-10">THE QUIET LEARNING SPACE</span>
          
          <div class="w-20 h-1 bg-gradient-to-r from-transparent via-indigo-200 to-transparent mb-10"></div>
          
          <p class="max-w-2xl text-lg font-bold text-gray-400 leading-loose">
            我们致力于重塑学习环境的交互体验。通过先进的座位预约算法与实时人流监控系统，“静学”旨在为每一位学子提供极致安静、智能且充满学术氛围的学习港湾。不仅是座位预约，更是您的学习效率倍增器。
          </p>
        </div>
      </div>

      <button @click="$router.back()" class="mt-8 text-[15px] font-black text-indigo-500 bg-indigo-50 px-10 py-4 rounded-full hover:bg-indigo-100 transition border-none cursor-pointer">
        返回个人中心
      </button>
  </div>
</template>

<style scoped>
.scale-up {
  animation: scale-up 0.5s ease-out forwards;
}

@keyframes scale-up {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
</style>

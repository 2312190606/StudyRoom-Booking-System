<script setup>
import { ref, computed } from 'vue'
import Navbar from '../components/Navbar.vue'
import { showLoadingToast, showSuccessToast, showFailToast } from 'vant'

const activeTab = ref('current') // 'current' or 'history'
const isSigning = ref(false)
const currentLocation = ref('')

// Initialize Mock History Records
const historyRecords = ref(Array.from({ length: 25 }, (_, i) => ({
  id: 25 - i,
  room: i % 2 === 0 ? '晨曦自习室 - B区' : '极客自习室 - 24h',
  seat: i % 2 === 0 ? 'B-12' : 'C-05',
  time: i % 2 === 0 ? '2024-05-11 09:00 - 12:00' : '2024-05-10 19:00 - 21:00',
  status: i % 3 === 0 ? 'cancelled' : 'completed'
})))

const activeMenu = ref(null)
const currentPage = ref(1)

const toggleMenu = (id) => {
  if (activeMenu.value === id) activeMenu.value = null
  else activeMenu.value = id
}

const deleteRecord = (id) => {
  historyRecords.value = historyRecords.value.filter(r => r.id !== id)
  activeMenu.value = null
  showSuccessToast('已删除记录')
  if (paginatedRecords.value.length === 0 && currentPage.value > 1) {
    currentPage.value--
  }
}

const totalPages = computed(() => Math.max(1, Math.ceil(historyRecords.value.length / 10)))

const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * 10
  return historyRecords.value.slice(start, start + 10)
})

const recentRecords = computed(() => {
  return historyRecords.value.slice(0, 2)
})

const activeReservation = ref({
  id: 'BK-10294',
  room: '静雅自习室 - A区',
  location: '图书馆 3 楼',
  time: '14:00 - 15:00',
  deadline: '14:15',
  seat: 'A-08'
})

const postponeReservation = () => {
  if (!activeReservation.value) return
  let [hours, minutes] = activeReservation.value.deadline.split(':').map(Number)
  minutes += 30
  if (minutes >= 60) {
    hours += Math.floor(minutes / 60)
    minutes %= 60
  }
  activeReservation.value.deadline = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`
  showSuccessToast('签到截止时间已延后 30 分钟')
}

const cancelActiveReservation = () => {
  if (!activeReservation.value) return
  
  historyRecords.value.unshift({
    id: Date.now(),
    room: activeReservation.value.room,
    seat: activeReservation.value.seat,
    time: `2024-05-12 ${activeReservation.value.time}`,
    status: 'cancelled'
  })
  
  activeReservation.value = null
  showSuccessToast('已成功取消预约')
}

const endStudy = () => {
  if (!activeReservation.value) return
  
  historyRecords.value.unshift({
    id: Date.now(),
    room: activeReservation.value.room,
    seat: activeReservation.value.seat,
    time: `2024-05-12 ${activeReservation.value.time}`,
    status: 'completed'
  })
  
  activeReservation.value = null
  showSuccessToast('自习已结束，释放座位成功')
}

const handleSign = () => {
  if (isSigning.value) return
  isSigning.value = true
  
  const toast = showLoadingToast({
    message: '正在调用地图定位...',
    forbidClick: true,
    duration: 0,
  })

  // 模拟调用浏览器/地图API进行定位验证
  setTimeout(() => {
    // 模拟80%的成功率
    const isSuccess = Math.random() > 0.2
    
    toast.close()
    isSigning.value = false
    
    if (isSuccess) {
      currentLocation.value = '图书馆 3 楼自习区范围内'
      if (activeReservation.value) activeReservation.value.status = 'checked_in'
      showSuccessToast('定位验证成功，已签到！')
    } else {
      currentLocation.value = '距静态目标 1.2km'
      showFailToast('定位失败，请确认您已到达自习室')
    }
  }, 1500)
}
</script>

<template>
  <div class="min-h-screen bg-[#f7f8fc] flex flex-col items-center pb-12">
    <!-- Navbar -->
    <Navbar />

    <main class="w-full px-8 md:px-12 lg:px-20 py-8 flex flex-col gap-8 max-w-[1200px]">
      
      <!-- Page Header -->
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mt-2">
        <div>
          <h1 class="text-[2rem] font-black text-gray-900 tracking-wide mb-1">我的预约</h1>
          <p class="text-[14px] font-medium text-gray-400 tracking-wide">管理您的学习计划与座位预约记录</p>
        </div>
        
        <!-- Toggle Switch -->
        <div class="bg-white p-1.5 rounded-full flex items-center shadow-sm border border-gray-100/50">
          <button 
            @click="activeTab = 'current'" 
            :class="activeTab === 'current' ? 'bg-[#5A52FF] shadow-md' : 'bg-transparent hover:bg-gray-50'"
            class="px-8 py-2.5 rounded-full text-[14px] font-bold transition-all duration-300 border-none cursor-pointer">
            <span :style="activeTab === 'current' ? 'color: white !important;' : 'color: black !important;'">当前预约</span>
          </button>
          <button 
            @click="activeTab = 'history'" 
            :class="activeTab === 'history' ? 'bg-[#5A52FF] shadow-md' : 'bg-transparent hover:bg-gray-50'"
            class="px-8 py-2.5 rounded-full text-[14px] font-bold transition-all duration-300 border-none cursor-pointer">
            <span :style="activeTab === 'history' ? 'color: white !important;' : 'color: black !important;'">历史记录</span>
          </button>
        </div>
      </div>

      <div v-if="activeTab === 'current'" class="flex flex-col gap-8 animate-in fade-in zoom-in-95 duration-500">
        <!-- Current Effective Section -->
        <div>
          <div class="flex items-center gap-3 mb-6">
            <div class="bg-indigo-50 text-[#5A52FF] p-2 rounded-xl flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
            </div>
            <h2 class="text-[1.35rem] font-black text-gray-900 tracking-wide">当前生效中</h2>
          </div>

          <!-- Active Reservation Card -->
          <div v-if="activeReservation" class="bg-white rounded-[2rem] p-8 lg:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col lg:flex-row justify-between gap-10 border border-gray-50">
            
            <!-- Left Info -->
            <div class="flex-1 flex flex-col justify-between">
              <div>
                <!-- Status Badges -->
                <div class="flex items-center gap-3 mb-5">
                  <span class="bg-[#dcfce7] text-[#16a34a] text-[11px] font-black px-3.5 py-1.5 rounded-full uppercase tracking-wider">预约成功</span>
                  <span class="text-[11px] font-bold text-gray-400 tracking-wider">编号：{{ activeReservation.id }}</span>
                </div>
                
                <!-- Title & Location -->
                <h3 class="text-[1.75rem] font-black text-gray-900 mb-2.5 tracking-wide">{{ activeReservation.room }}</h3>
                <div class="flex items-center gap-1.5 text-[14px] font-bold text-gray-400">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                  {{ activeReservation.location }}
                </div>

                <!-- Time Info -->
                <div class="flex gap-16 mt-12 w-full max-w-sm">
                  <div class="flex flex-col gap-2">
                    <div class="flex items-center gap-1.5 text-gray-400 text-xs font-bold">
                      <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                      签到截止
                    </div>
                    <div class="text-[1.15rem] font-black text-[#f59e0b] tracking-wide">{{ activeReservation.deadline }} 前</div>
                  </div>
                </div>
              </div>

              <!-- Actions -->
              <div class="flex items-center gap-4 mt-12">
                <template v-if="activeReservation.status !== 'checked_in'">
                  <button @click="handleSign" :disabled="isSigning" class="bg-[#5A52FF] text-white px-7 py-3.5 rounded-[1.25rem] flex items-center justify-center gap-2 text-[14px] font-bold hover:bg-[#4a42e5] transition-all shadow-[0_4px_14px_rgba(90,82,255,0.3)] hover:shadow-[0_6px_20px_rgba(90,82,255,0.4)] hover:-translate-y-0.5 disabled:opacity-70 disabled:cursor-not-allowed border-none cursor-pointer" style="color: white !important;">
                    <svg v-if="isSigning" class="animate-spin w-5 h-5 text-white" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                    <svg v-else class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5" style="color: white !important;"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                    <span class="text-white" style="color: white !important;">立即定位签到</span>
                  </button>
                  <button @click="postponeReservation" class="bg-white border-2 border-gray-100 text-gray-900 px-7 py-[12px] rounded-[1.25rem] text-[14px] font-bold hover:bg-gray-50 hover:border-gray-200 transition-all cursor-pointer" style="color: black !important;">
                    预约延后
                  </button>
                  <button @click="cancelActiveReservation" class="bg-white border-2 border-gray-100 text-black px-7 py-[12px] rounded-[1.25rem] text-[14px] font-bold hover:bg-red-50 transition-all cursor-pointer" style="color: black !important;">
                    取消预约
                  </button>
                </template>
                <template v-else>
                  <div class="flex items-center gap-2 px-3 py-2 bg-green-50 rounded-xl border border-green-100 mr-4">
                    <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                    <span class="text-green-600 text-[13px] font-bold tracking-wide">学习中，已锁定当前座位</span>
                  </div>
                  <button @click="endStudy" class="bg-white border-2 border-gray-100 text-black px-7 py-[12px] rounded-[1.25rem] text-[14px] font-bold hover:bg-green-50 border-green-100 transition-all cursor-pointer" style="color: black !important;">
                    自习结束
                  </button>
                </template>
              </div>
            </div>

            <!-- Right Location & Seat -->
            <div class="flex flex-col items-center justify-center lg:pl-12 lg:border-l border-gray-100 lg:w-[340px]">
              <div class="flex flex-col items-center mb-6">
                <span class="text-gray-400 font-bold text-[13px] mb-1">您的座位</span>
                <span class="text-[3.25rem] font-black text-[#5A52FF] leading-none tracking-tight">{{ activeReservation.seat }}</span>
              </div>
              
              <div class="bg-[#f8f9fc] p-6 rounded-[1.5rem] flex flex-col items-center w-full min-h-[12rem] justify-center">
                <div class="w-16 h-16 bg-indigo-50 text-[#5A52FF] p-3.5 rounded-full shadow-sm border border-indigo-100 flex items-center justify-center mb-4 transition-transform hover:scale-105">
                  <svg class="w-full h-full" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                </div>
                
                <div class="text-xs font-bold text-gray-400 mb-1">当前位置</div>
                <div class="text-sm font-black text-emerald-600 text-center mb-4" v-if="currentLocation && currentLocation.includes('内')">{{ currentLocation }}</div>
                <div class="text-sm font-black text-red-500 text-center mb-4" v-else-if="currentLocation">{{ currentLocation }}</div>
                <div class="text-xs font-black text-gray-400 text-center mb-4" v-else>尚未触发定位</div>
                
                <p class="text-[11.5px] font-bold text-gray-400 text-center tracking-wider px-2 leading-[1.6]">
                  请到达自习室范围内<br>点击签到按钮完成定位验证
                </p>
              </div>
            </div>
          </div>

          <!-- Empty Active Status -->
          <div v-else class="bg-white rounded-[2rem] p-10 flex flex-col items-center justify-center border border-gray-50 shadow-sm min-h-[16rem]">
            <div class="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center text-gray-300 mb-5">
               <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
            </div>
            <p class="text-gray-900 font-black text-lg tracking-wide mb-1">当前暂无进行中的预约</p>
            <p class="text-[13px] text-gray-400 font-bold tracking-wide">去首页看看有什么空闲的自习室吧</p>
          </div>
        </div>

        <!-- Recent Records Section -->
        <div>
          <div class="flex items-center gap-3 mb-5 mt-2">
            <div class="bg-gray-200 text-gray-500 p-2 rounded-xl flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            </div>
            <h2 class="text-[1.35rem] font-black text-gray-900 tracking-wide">最近记录</h2>
          </div>

          <div class="flex flex-col gap-4">
            
            <div v-for="record in recentRecords" :key="'recent-' + record.id" class="bg-white rounded-[1.5rem] p-6 shadow-sm border border-gray-50 flex flex-col sm:flex-row sm:items-center justify-between group hover:shadow-md transition-all cursor-pointer relative">
              <div class="flex items-center gap-5">
                <div v-if="record.status === 'completed'" class="w-12 h-12 bg-[#ecfdf3] text-[#10b981] rounded-full flex items-center justify-center flex-shrink-0">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"></path></svg>
                </div>
                <div v-else class="w-12 h-12 bg-gray-100 text-gray-400 rounded-full flex items-center justify-center flex-shrink-0">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
                </div>
                <!-- Texts -->
                <div class="flex flex-col">
                  <span class="text-[1.1rem] font-black text-gray-900 mb-1 tracking-wide">{{ record.room }}</span>
                  <span class="text-[12px] font-bold text-gray-400 tracking-wide">座位 {{ record.seat }} · {{ record.time }}</span>
                </div>
              </div>
              
              <div class="flex items-center gap-6 mt-4 sm:mt-0 relative">
                <span v-if="record.status === 'completed'" class="text-[#10b981] text-[13px] font-black tracking-wide">已完成</span>
                <span v-else class="text-gray-400 text-[13px] font-black tracking-wide">已取消</span>
                
                <button @click.stop="toggleMenu(record.id)" class="text-gray-900 border-none bg-transparent cursor-pointer hover:bg-gray-100 w-8 h-8 rounded-full transition-colors font-bold tracking-widest leading-none flex items-center justify-center pb-2.5" style="letter-spacing: 0.1em;">
                  ...
                </button>

                <!-- Popover -->
                <div v-if="activeMenu === record.id" class="absolute right-0 top-10 bg-white rounded-xl shadow-lg border border-gray-100 py-1.5 w-28 z-20 animate-in fade-in zoom-in-95 duration-200">
                  <button @click.stop="deleteRecord(record.id)" class="w-full text-left px-4 py-2.5 text-[13px] text-red-500 hover:bg-red-50 font-bold border-none bg-transparent cursor-pointer">删除记录</button>
                </div>
              </div>
            </div>

          </div>
        </div>

      </div>

      <!-- History Tab Content -->
      <div v-else class="flex flex-col gap-6 w-full animate-in fade-in duration-500 mt-2">
        <div class="flex items-center gap-3 mb-2">
          <div class="bg-gray-200 text-gray-500 p-2 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          </div>
          <h2 class="text-[1.35rem] font-black text-gray-900 tracking-wide">完整历史记录</h2>
        </div>

        <div v-if="historyRecords.length === 0" class="flex flex-col items-center justify-center py-20">
          <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center text-gray-300 mb-6">
            <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          </div>
          <h3 class="text-xl font-bold text-gray-900 mb-2">暂无历史记录</h3>
        </div>

        <div v-else class="flex flex-col gap-4">
          <div v-for="record in paginatedRecords" :key="'hist-' + record.id" class="bg-white rounded-[1.5rem] p-6 shadow-sm border border-gray-50 flex flex-col sm:flex-row sm:items-center justify-between group hover:shadow-md transition-all cursor-pointer relative">
            <div class="flex items-center gap-5">
              <div v-if="record.status === 'completed'" class="w-12 h-12 bg-[#ecfdf3] text-[#10b981] rounded-full flex items-center justify-center flex-shrink-0">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"></path></svg>
              </div>
              <div v-else class="w-12 h-12 bg-gray-100 text-gray-400 rounded-full flex items-center justify-center flex-shrink-0">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-[1.1rem] font-black text-gray-900 mb-1 tracking-wide">{{ record.room }}</span>
                <span class="text-[12px] font-bold text-gray-400 tracking-wide">座位 {{ record.seat }} · {{ record.time }}</span>
              </div>
            </div>
            
            <div class="flex items-center gap-6 mt-4 sm:mt-0 relative">
              <span v-if="record.status === 'completed'" class="text-[#10b981] text-[13px] font-black tracking-wide">已完成</span>
              <span v-else class="text-gray-400 text-[13px] font-black tracking-wide">已取消</span>
              
              <button @click.stop="toggleMenu(record.id)" class="text-gray-900 border-none bg-transparent cursor-pointer hover:bg-gray-100 w-8 h-8 rounded-full transition-colors font-bold tracking-widest leading-none flex items-center justify-center pb-2.5" style="letter-spacing: 0.1em;">
                ...
              </button>

              <div v-if="activeMenu === record.id" class="absolute right-0 top-10 bg-white rounded-xl shadow-lg border border-gray-100 py-1.5 w-28 z-20 animate-in fade-in zoom-in-95 duration-200">
                <button @click.stop="deleteRecord(record.id)" class="w-full text-left px-4 py-2.5 text-[13px] text-red-500 hover:bg-red-50 font-bold border-none bg-transparent cursor-pointer">删除记录</button>
              </div>
            </div>
          </div>
          
          <!-- Pagination -->
          <div v-if="totalPages > 1" class="flex justify-center items-center gap-4 mt-6">
            <button 
              @click="currentPage > 1 && currentPage--" 
              :disabled="currentPage === 1"
              class="px-5 py-2.5 rounded-full bg-white text-[13px] font-bold text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition border border-gray-200 cursor-pointer shadow-sm">
              上一页
            </button>
            <span class="text-[13px] font-bold text-gray-500">{{ currentPage }} / {{ totalPages }}</span>
            <button 
              @click="currentPage < totalPages && currentPage++" 
              :disabled="currentPage === totalPages"
              class="px-5 py-2.5 rounded-full bg-white text-[13px] font-bold text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition border border-gray-200 cursor-pointer shadow-sm">
              下一页
            </button>
          </div>
        </div>
      </div>

    </main>
  </div>
</template>

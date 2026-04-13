<script setup>
import { ref, computed, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import { showLoadingToast, showSuccessToast, showFailToast } from 'vant'
import { getMyReservations, cancelReservation, extendReservation, checkIn, endStudy, deleteReservation } from '../api/reservations'

const activeTab = ref('current')
const isSigning = ref(false)
const currentLocation = ref('')
const loading = ref(false)

// 真实预约数据
const reservations = ref([])
const activeMenu = ref(null)
const currentPage = ref(1)
const pageSize = 10

const toggleMenu = (id) => {
  if (activeMenu.value === id) activeMenu.value = null
  else activeMenu.value = id
}

// 状态: 1=待使用, 2=使用中, 0=已取消
const getStatusText = (status) => {
  const map = { 1: '待使用', 2: '使用中', 0: '已取消' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    1: 'bg-[#fef3c7] text-[#d97706]',
    2: 'bg-[#dcfce7] text-[#16a34a]',
    0: 'bg-gray-100 text-gray-400'
  }
  return map[status] || 'bg-gray-100 text-gray-400'
}

// 当前生效的预约 (status = 1 待使用 或 status = 2 使用中)
const activeReservations = computed(() => {
  return reservations.value.filter(r => r.status === 1 || r.status === 2)
})

// 当前选中的活跃预约（优先显示待使用的，其次显示使用中的）
const currentReservation = computed(() => {
  return activeReservations.value.find(r => r.status === 1) || activeReservations.value.find(r => r.status === 2) || null
})

// 正在学习中的预约
const studyingReservation = computed(() => {
  return activeReservations.value.find(r => r.status === 2) || null
})

// 历史记录 (已取消或已完成的) - 按 id 倒序，新的在前
const historyRecords = computed(() => {
  return reservations.value
    .filter(r => r.status === 0 || r.status === 3)
    .sort((a, b) => b.id - a.id)
})

const totalPages = computed(() => Math.max(1, Math.ceil(historyRecords.value.length / pageSize)))

const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return historyRecords.value.slice(start, start + pageSize)
})

// 加载预约数据
const loadReservations = async () => {
  loading.value = true
  try {
    const data = await getMyReservations({ page: 1, size: 100 })
    reservations.value = data.records || []
  } catch (error) {
    console.error('加载预约失败', error)
  } finally {
    loading.value = false
  }
}

// 取消预约
const handleCancel = async (id) => {
  try {
    await cancelReservation(id)
    showSuccessToast('已取消预约')
    await loadReservations()
  } catch (error) {
    showFailToast('取消失败')
  }
  activeMenu.value = null
}

// 延后预约
const handleExtend = async (id) => {
  try {
    await extendReservation(id)
    showSuccessToast('已延后30分钟')
    await loadReservations()
  } catch (error) {
    showFailToast('延后失败')
  }
}

// 签到
const handleSign = async () => {
  if (!currentReservation.value || isSigning.value) return

  isSigning.value = true
  const toast = showLoadingToast({
    message: '正在签到...',
    forbidClick: true,
    duration: 0,
  })

  try {
    await checkIn(currentReservation.value.id)
    toast.close()
    showSuccessToast('签到成功')
    await loadReservations()
  } catch (error) {
    toast.close()
    showFailToast('签到失败')
  } finally {
    isSigning.value = false
  }
}

// 结束学习
const handleEndStudy = async () => {
  if (!studyingReservation.value) return
  try {
    await endStudy(studyingReservation.value.id)
    showSuccessToast('学习结束，座位已释放')
    await loadReservations()
  } catch (error) {
    showFailToast('操作失败')
  }
}

// 删除预约记录
const handleDelete = async (id) => {
  try {
    await deleteReservation(id)
    showSuccessToast('记录已删除')
    await loadReservations()
  } catch (error) {
    showFailToast('删除失败')
  }
  activeMenu.value = null
}

onMounted(() => {
  loadReservations()
})
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

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center py-20">
        <span class="text-gray-400">加载中...</span>
      </div>

      <div v-else-if="activeTab === 'current'" class="flex flex-col gap-8 animate-in fade-in zoom-in-95 duration-500">
        <!-- Current Effective Section -->
        <div>
          <div class="flex items-center gap-3 mb-6">
            <div class="bg-indigo-50 text-[#5A52FF] p-2 rounded-xl flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
            </div>
            <h2 class="text-2xl font-black text-gray-900 tracking-wide">当前生效中</h2>
          </div>

          <!-- Active Reservation Card -->
          <div v-if="currentReservation || studyingReservation" class="bg-white rounded-[2rem] p-8 lg:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col lg:flex-row justify-between gap-10 border border-gray-50">

            <!-- Left Info -->
            <div class="flex-1 flex flex-col justify-between">
              <div>
                <!-- Status Badges -->
                <div class="flex items-center gap-3 mb-5">
                  <span :class="['text-[11px] font-black px-3.5 py-1.5 rounded-full uppercase tracking-wider', getStatusClass(currentReservation.status)]">
                    {{ getStatusText(currentReservation.status) }}
                  </span>
                  <span class="text-[11px] font-bold text-gray-400 tracking-wider">编号：{{ currentReservation.id }}</span>
                </div>

                <!-- Title & Location -->
                <h3 class="text-[1.75rem] font-black text-gray-900 mb-2.5 tracking-wide">{{ currentReservation.seat?.roomName || '自习室' }}</h3>
                <div class="flex items-center gap-1.5 text-[14px] font-bold text-gray-400">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                  {{ currentReservation.seat?.roomName || '' }}
                </div>

                <!-- Time Info -->
                <div class="flex gap-16 mt-12 w-full max-w-sm">
                  <div class="flex flex-col gap-2">
                    <div class="flex items-center gap-1.5 text-gray-400 text-xs font-bold">
                      <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                      预约时间
                    </div>
                    <div class="text-[1.15rem] font-black text-gray-900 tracking-wide">
                      {{ currentReservation.startTime?.slice(-5) }} - {{ currentReservation.endTime?.slice(-5) }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- Actions -->
              <div class="flex items-center gap-4 mt-12">
                <template v-if="currentReservation.status === 1">
                  <button @click="handleSign" :disabled="isSigning" class="bg-[#5A52FF] text-white px-8 py-4 rounded-[1.25rem] flex items-center justify-center gap-2 text-[15px] font-bold hover:bg-[#4a42e5] transition-all shadow-[0_4px_14px_rgba(90,82,255,0.3)] hover:shadow-[0_6px_20px_rgba(90,82,255,0.4)] hover:-translate-y-0.5 disabled:opacity-70 disabled:cursor-not-allowed border-none cursor-pointer" style="color: white !important;">
                    <svg v-if="isSigning" class="animate-spin w-5 h-5 text-white" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                    <span class="text-white" style="color: white !important;">立即签到</span>
                  </button>
                  <button v-if="currentReservation.extended !== 1" @click="handleExtend(currentReservation.id)" class="bg-white border-2 border-gray-100 text-gray-900 px-8 py-4 rounded-[1.25rem] text-[15px] font-bold hover:bg-gray-50 hover:border-gray-200 transition-all cursor-pointer" style="color: black !important;">
                    预约延后
                  </button>
                  <button @click="handleCancel(currentReservation.id)" class="bg-white border-2 border-gray-100 text-black px-8 py-4 rounded-[1.25rem] text-[15px] font-bold hover:bg-red-50 transition-all cursor-pointer" style="color: black !important;">
                    取消预约
                  </button>
                </template>
                <template v-else-if="currentReservation.status === 2">
                  <div class="flex items-center gap-2 px-3 py-2 bg-green-50 rounded-xl border border-green-100 mr-4">
                    <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                    <span class="text-green-600 text-[13px] font-bold tracking-wide">学习中，已锁定当前座位</span>
                  </div>
                  <button @click="handleEndStudy" class="bg-white border-2 border-gray-100 text-black px-8 py-4 rounded-[1.25rem] text-[15px] font-bold hover:bg-green-50 border-green-100 transition-all cursor-pointer" style="color: black !important;">
                    自习结束
                  </button>
                </template>
              </div>
            </div>

            <!-- Right Location & Seat -->
            <div class="flex flex-col items-center justify-center lg:pl-12 lg:border-l border-gray-100 lg:w-[340px]">
              <div class="flex flex-col items-center mb-6">
                <span class="text-gray-400 font-bold text-[13px] mb-1">您的座位</span>
                <span class="text-[3.25rem] font-black text-[#5A52FF] leading-none tracking-tight">{{ currentReservation.seat?.seatNumber || '-' }}</span>
              </div>

              <div class="bg-[#f8-9fc] p-6 rounded-[1.5rem] flex flex-col items-center w-full min-h-[12rem] justify-center">
                <div class="w-16 h-16 bg-indigo-50 text-[#5A52FF] p-3.5 rounded-full shadow-sm border border-indigo-100 flex items-center justify-center mb-4 transition-transform hover:scale-105">
                  <svg class="w-full h-full" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                </div>
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
      </div>

      <!-- History Tab Content -->
      <div v-else class="flex flex-col gap-6 w-full animate-in fade-in duration-500 mt-2">
        <div class="flex items-center gap-3 mb-2">
          <div class="bg-gray-200 text-gray-500 p-2 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          </div>
          <h2 class="text-2xl font-black text-gray-900 tracking-wide">完整历史记录</h2>
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
              <div v-if="record.status === 0" class="w-12 h-12 bg-gray-100 text-gray-400 rounded-full flex items-center justify-center flex-shrink-0">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
              </div>
              <div v-else-if="record.status === 3" class="w-12 h-12 bg-[#ecfdf3] text-[#10b981] rounded-full flex items-center justify-center flex-shrink-0">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"></path></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-lg font-black text-gray-900 mb-1 tracking-wide">{{ record.seat?.roomName || '自习室' }}</span>
                <span class="text-[12px] font-bold text-gray-400 tracking-wide">座位 {{ record.seat?.seatNumber || '-' }} · {{ record.startTime }}</span>
              </div>
            </div>

            <div class="flex items-center gap-6 mt-4 sm:mt-0 relative">
              <span class="text-gray-400 text-[13px] font-black tracking-wide">
                {{ record.status === 0 ? '已取消' : '已完成' }}
              </span>

              <button @click.stop="toggleMenu(record.id)" class="text-gray-900 border-none bg-transparent cursor-pointer hover:bg-gray-100 w-8 h-8 rounded-full transition-colors font-bold tracking-widest leading-none flex items-center justify-center pb-2.5" style="letter-spacing: 0.1em;">
                ...
              </button>

              <div v-if="activeMenu === record.id" class="absolute right-0 top-10 bg-white rounded-xl shadow-lg border border-gray-100 py-1.5 w-28 z-20 animate-in fade-in zoom-in-95 duration-200">
                <button @click.stop="handleDelete(record.id)" class="w-full text-left px-4 py-2.5 text-[13px] text-red-500 hover:bg-red-50 font-bold border-none bg-transparent cursor-pointer">删除记录</button>
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

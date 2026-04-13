<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import Navbar from '../components/Navbar.vue'
import { getRooms, getRoomSeats } from '../api/rooms'
import { createReservation } from '../api/reservations'

const router = useRouter()

const showAllRooms = ref(false)
const showSeatModal = ref(false)
const showAnnouncementModal = ref(false)
const currentRoom = ref(null)
const seats = ref([])
const bookingSuccessMsg = ref(false)
const showFailToastMsg = ref(false)

// 公告相关
const announcements = ref([])
const currentNoticeIdx = ref(0)

setInterval(() => {
  if (announcements.value.length > 0) {
    currentNoticeIdx.value = (currentNoticeIdx.value + 1) % announcements.value.length
  }
}, 4000)

// 加载公告
const loadAnnouncements = async () => {
  try {
    const res = await fetch('/api/public/announcements')
    const data = await res.json()
    if (data.code === 200) {
      announcements.value = data.data || []
    }
  } catch (error) {
    console.error('加载公告失败', error)
  }
}

// 常用座位 - 从 localStorage 读取
const frequentSeat = ref(JSON.parse(localStorage.getItem('frequentSeat') || 'null'))
const frequentSeatStr = computed(() => {
  if (!frequentSeat.value) return ""
  return `${frequentSeat.value.roomName || 'S'}-${frequentSeat.value.seatNumber}`
})
const frequentSeatStatus = ref(localStorage.getItem('frequentSeatStatus') || "available")

const modalMode = ref('book') // 'book' or 'setFrequent'
const showConfirmModal = ref(false)
const selectedSeatForFrequent = ref(null)

// 真实数据
const studyRooms = ref([])

// 后端座位状态: 0=维修, 1=可用, 2=占用
// 前端显示状态: 'maintenance', 'available', 'occupied'
// 前端预约状态: 'booked' (本地状态，表示已预约)
const seatStatusMap = {
  0: 'maintenance',
  1: 'available',
  2: 'occupied'
}

// 将后端座位数据转为前端格式
const transformSeats = (backendSeats, maintenanceSeats) => {
  const maintenanceSet = new Set(maintenanceSeats || [])
  return backendSeats.map(seat => {
    const posKey = `${seat.positionX}-${seat.positionY}`
    // 如果座位位置在维修列表中，强制标记为维修
    const isMaintenance = maintenanceSet.has(posKey) || seat.status === 0
    // 座位编号使用 row-col 格式（如 "A1", "B2"）
    const rowLetter = String.fromCharCode(64 + seat.positionX)
    return {
      id: seat.id,
      seatNumber: `${rowLetter}${seat.positionY}`,
      status: isMaintenance ? 'maintenance' : (seatStatusMap[seat.status] || 'available'),
      hasPower: seat.hasPower,
      isWindow: seat.isWindow,
      positionX: seat.positionX,
      positionY: seat.positionY
    }
  })
}

// 计算指定自习室的可用座位数
const getAvailableSeatsCount = (roomId) => {
  const room = studyRooms.value.find(r => r.id === roomId)
  if (!room) return 0

  const rows = room.seatRows || 5
  const cols = room.cols || 8
  const totalSeats = rows * cols

  // 计算维修中的座位数
  let maintenanceCount = 0
  if (room.maintenanceSeats) {
    try {
      const list = JSON.parse(room.maintenanceSeats)
      if (Array.isArray(list)) {
        maintenanceCount = list.length
      }
    } catch (e) {
      console.error('解析维修座位失败', e, room.maintenanceSeats)
    }
  }

  console.log('剩余座位计算:', { roomId, rows, cols, totalSeats, maintenanceSeats: room.maintenanceSeats, maintenanceCount, result: totalSeats - maintenanceCount })
  return totalSeats - maintenanceCount
}

const totalSeatsLeft = computed(() => {
  return studyRooms.value.reduce((total, room) => total + getAvailableSeatsCount(room.id), 0)
})

const allRoomsSeats = ref({})

// 加载自习室数据
const loadRooms = async () => {
  try {
    const data = await getRooms({ page: 1, size: 100 })
    studyRooms.value = data.records || []
  } catch (error) {
    console.error('加载自习室失败', error)
  }
}

// 加载座位数据
const loadSeats = async (roomId, maintenanceSeats) => {
  try {
    const data = await getRoomSeats(roomId)
    allRoomsSeats.value[roomId] = transformSeats(data || [], maintenanceSeats || [])
  } catch (error) {
    console.error('加载座位失败', error)
  }
}

const initRooms = async () => {
  await loadRooms()
  // 预加载所有自习室的座位数据
  for (const room of studyRooms.value) {
    let maintenanceSeats = []
    if (room.maintenanceSeats) {
      try {
        maintenanceSeats = JSON.parse(room.maintenanceSeats)
      } catch (e) {}
    }
    await loadSeats(room.id, maintenanceSeats)
  }
  // 加载公告
  await loadAnnouncements()
}

onMounted(() => {
  initRooms()
})

const generateSeats = () => {
  if (currentRoom.value) {
    const rows = currentRoom.value.seatRows || currentRoom.value.rows || 5
    const cols = currentRoom.value.cols || 8
    const expectedCount = rows * cols
    const cachedSeats = allRoomsSeats.value[currentRoom.value.id] || []

    // 获取维修座位列表
    let maintenanceSeats = []
    if (currentRoom.value.maintenanceSeats) {
      try {
        maintenanceSeats = JSON.parse(currentRoom.value.maintenanceSeats)
      } catch (e) {}
    }
    const maintenanceSet = new Set(maintenanceSeats)

    // 如果缓存座位数量与期望数量不匹配，重新生成
    if (cachedSeats.length !== expectedCount) {
      const generatedSeats = []
      for (let r = 1; r <= rows; r++) {
        for (let c = 1; c <= cols; c++) {
          const rowLetter = String.fromCharCode(64 + r)
          const posKey = `${r}-${c}`
          generatedSeats.push({
            id: `${r}-${c}`,
            seatNumber: `${rowLetter}${c}`,
            status: maintenanceSet.has(posKey) ? 'maintenance' : 'available',
            positionX: r,
            positionY: c
          })
        }
      }
      seats.value = generatedSeats
    } else {
      seats.value = cachedSeats
    }
  }
}

const openRoomLayout = async (roomId, forceMode) => {
  if (forceMode) modalMode.value = forceMode
  showAllRooms.value = false
  // 重新加载自习室数据，确保 maintenanceSeats 是最新的
  await loadRooms()
  currentRoom.value = studyRooms.value.find(r => r.id === roomId)
  // 获取维修座位列表
  let maintenanceSeats = []
  if (currentRoom.value && currentRoom.value.maintenanceSeats) {
    try {
      maintenanceSeats = JSON.parse(currentRoom.value.maintenanceSeats)
    } catch (e) {}
  }
  // 每次都强制重新加载座位数据，确保显示最新状态
  await loadSeats(roomId, maintenanceSeats)
  generateSeats()
  showSeatModal.value = true
}

const handleSetFrequent = () => {
  modalMode.value = 'setFrequent'
  showAllRooms.value = true
}

const selectSeat = async (seat) => {
  if (modalMode.value === 'setFrequent') {
    selectedSeatForFrequent.value = seat
    showConfirmModal.value = true
  } else {
    if (seat.status !== 'available') return;

    // 检查是否为真实座位ID（数据库ID是数字，"1-1"这种格式是动态生成的无真实记录）
    const seatId = parseInt(seat.id)
    if (isNaN(seatId) || !String(seat.id).match(/^\d+$/)) {
      // 动态生成的座位（row-col格式如"1-1"），提示用户重新创建自习室
      alert('座位数据异常，请联系管理员在后台重新创建该自习室')
      return
    }

    try {
      // 默认预约时长2小时
      const now = new Date()
      const startTime = new Date(now.getTime() + 30 * 60 * 1000) // 30分钟后开始
      const endTime = new Date(startTime.getTime() + 2 * 60 * 60 * 1000) // 2小时后结束

      // 格式化时间为 yyyy-MM-ddTHH:mm:ss（本地时间）
      const formatTime = (date) => {
        const pad = (n) => n.toString().padStart(2, '0')
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
      }

      await createReservation({
        seatId: seatId,
        startTime: formatTime(startTime),
        endTime: formatTime(endTime)
      })

      bookingSuccessMsg.value = true;
      setTimeout(() => {
        bookingSuccessMsg.value = false;
        showSeatModal.value = false;
        router.push({ name: 'reservations' });
      }, 1200);
    } catch (error) {
      console.error('预约失败', error)
    }
  }
}

const confirmSetFrequent = () => {
  const roomPrefix = currentRoom.value.name.includes('-') ? currentRoom.value.name.split('-')[1].trim()[0] : 'S'
  const seatNb = selectedSeatForFrequent.value.seatNumber || selectedSeatForFrequent.value.id
  // 保存完整信息到 localStorage
  const seatInfo = {
    roomId: currentRoom.value.id,
    roomName: roomPrefix,
    seatId: selectedSeatForFrequent.value.id,
    seatNumber: seatNb,
    status: selectedSeatForFrequent.value.status
  }
  frequentSeat.value = seatInfo
  localStorage.setItem('frequentSeat', JSON.stringify(seatInfo))
  frequentSeatStatus.value = selectedSeatForFrequent.value.status
  localStorage.setItem('frequentSeatStatus', frequentSeatStatus.value)
  showConfirmModal.value = false
  showSeatModal.value = false
}

const cancelSetFrequent = () => {
  showConfirmModal.value = false
}

const handleQuickBook = async () => {
  if (!frequentSeat.value) {
    showFailToastMsg.value = true
    setTimeout(() => { showFailToastMsg.value = false }, 1500)
    return
  }

  if (frequentSeatStatus.value !== 'available') {
    showFailToastMsg.value = true
    setTimeout(() => { showFailToastMsg.value = false }, 1500)
    return
  }

  try {
    // 默认预约时长2小时
    const now = new Date()
    const startTime = new Date(now.getTime() + 30 * 60 * 1000) // 30分钟后开始
    const endTime = new Date(startTime.getTime() + 2 * 60 * 60 * 1000) // 2小时后结束

    // 格式化时间为 yyyy-MM-ddTHH:mm:ss（本地时间）
    const formatTime = (date) => {
      const pad = (n) => n.toString().padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
    }

    await createReservation({
      seatId: parseInt(frequentSeat.value.seatId),
      startTime: formatTime(startTime),
      endTime: formatTime(endTime)
    })

    bookingSuccessMsg.value = true
    setTimeout(() => {
      bookingSuccessMsg.value = false
      router.push({ name: 'reservations' })
    }, 1200)
  } catch (error) {
    console.error('快速预约失败', error)
    showFailToastMsg.value = true
    setTimeout(() => { showFailToastMsg.value = false }, 1500)
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col items-center">
    <!-- Navbar -->
    <Navbar />

    <main class="w-full px-8 md:px-12 lg:px-20 py-8 pb-20 flex flex-col gap-8">
      <!-- Hero Banner -->
      <div class="bg-[#3B34D1] rounded-[2rem] p-10 text-white relative overflow-hidden shadow-lg h-[22rem] flex flex-col justify-center">
        <!-- Background Pattern (Calendar icon outline) -->
        <svg class="absolute right-10 top-1/2 transform -translate-y-1/2 w-96 h-96 text-white opacity-[0.15] pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="1.5">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
        </svg>
        
        <div class="relative z-10 w-full md:w-2/3">
          <h1 class="text-[2.75rem] font-bold mb-3 tracking-wide leading-tight text-white/95">静心专注，</h1>
          <h1 class="text-[2.75rem] font-bold mb-10 tracking-wide leading-tight text-white/95">在这里开启高效学习</h1>
        </div>
      </div>

      <!-- Dynamic Notice Bar -->
      <div v-if="announcements.length > 0" class="bg-[#fef4e8] border border-orange-100 rounded-2xl px-5 py-3.5 flex items-center justify-between text-orange-600 shadow-sm mt-2">
        <div class="flex items-center gap-3 text-sm font-black flex-1 overflow-hidden">
          <div class="bg-[#f28e2b] text-white p-1 rounded-lg shadow-sm">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z"></path></svg>
          </div>
          <div class="flex-1 h-6 relative overflow-hidden">
            <div v-for="(anno, idx) in announcements" :key="anno.id" 
                 class="absolute inset-0 flex items-center transition-all duration-700 pointer-events-none"
                 :class="{
                   'translate-y-0 opacity-100': currentNoticeIdx === idx,
                   '-translate-y-full opacity-0': currentNoticeIdx !== idx
                 }">
              <span class="text-[#e27318] truncate">{{ anno.title }}</span>
            </div>
          </div>
        </div>
        <button @click="showAnnouncementModal = true" class="text-xs font-black text-[#e27318] hover:text-orange-800 ml-4 whitespace-nowrap bg-transparent border-none cursor-pointer tracking-wider">查看更多</button>
      </div>

      <!-- Overview Header -->
      <div class="flex items-end justify-between mt-6">
        <div>
          <h2 class="text-[1.35rem] font-bold text-gray-900 tracking-wide">自习室概览</h2>
          <div class="text-[13px] text-gray-500 mt-1.5 font-medium">共 {{ studyRooms.length }} 个自习室，剩余 {{ totalSeatsLeft }} 个座位</div>
        </div>
        <button @click="showAllRooms = true; modalMode = 'book'" class="text-[13px] font-bold text-[#3B34D1] hover:text-indigo-800 flex items-center gap-0.5 border-none bg-transparent cursor-pointer p-0">查看全部 <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg></button>
      </div>

      <!-- Room Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-2">
        <div v-for="room in studyRooms" :key="room.id" class="bg-white rounded-[1.25rem] overflow-hidden shadow-sm border border-gray-100 flex flex-col hover:shadow-md transition duration-300 group">
          <!-- Image -->
          <div class="h-48 relative bg-gray-200 overflow-hidden">
            <img :src="room.image" class="w-full h-full object-cover group-hover:scale-105 transition duration-500" />
            <!-- Tag removed -->
          </div>
          <!-- Body -->
          <div class="p-6 flex flex-col gap-5 flex-1">
            <div>
              <h3 class="font-bold text-xl text-gray-900">{{ room.name }}</h3>
              <div class="flex items-center gap-1.5 text-[14px] text-gray-500 mt-1.5 font-medium">
                <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                {{ room.location }}
              </div>
            </div>
            
            <div class="flex items-center gap-8 mt-1">
              <div class="flex items-center gap-3">
                <div class="w-[2.25rem] h-[2.25rem] rounded-full bg-[#f0fdf4] text-[#22c55e] flex items-center justify-center">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                </div>
                <div>
                  <div class="text-[11px] text-gray-400 font-medium">剩余座位</div>
                  <div class="text-sm font-black text-gray-900 mt-0.5">{{ getAvailableSeatsCount(room.id) }}</div>
                </div>
              </div>
              
              <div class="flex items-center gap-3">
                <div class="w-[2.25rem] h-[2.25rem] rounded-full bg-[#eff6ff] text-[#3b82f6] flex items-center justify-center">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </div>
                <div>
                  <div class="text-[11px] text-gray-400 font-medium">开放时间</div>
                  <div class="text-sm font-black text-gray-900 mt-0.5">{{ room.openTime }}</div>
                </div>
              </div>
            </div>

            <button @click="openRoomLayout(room.id, 'book')" class="mt-4 w-full bg-[#0f172a] text-white py-4 rounded-xl text-[15px] font-bold hover:bg-gray-800 transition shadow-md" style="color: white !important;">
              <span class="text-white" style="color: white !important;">去预选座位</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Quick Book Section (常用座位) -->
      <div class="bg-white border border-gray-100 rounded-[1.25rem] p-6 flex items-center justify-between shadow-sm mt-4">
        <div class="flex items-center gap-5">
          <div class="w-[3.5rem] h-[3.5rem] rounded-2xl bg-[#5A52FF] text-white flex items-center justify-center shadow-lg shadow-indigo-200">
            <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
          </div>
          <div>
            <h3 class="text-lg font-bold text-gray-900">常用座位</h3>
            <div class="text-[13px] text-gray-500 mt-1 font-medium">快速预约您常坐的位置，开启专注模式</div>
          </div>
        </div>
        <div class="flex items-center gap-4">
          <button @click="handleSetFrequent" class="px-6 py-3.5 rounded-xl border border-gray-200 text-gray-700 text-[15px] font-bold hover:bg-gray-50 transition cursor-pointer bg-white">设置常用</button>
          <button @click="handleQuickBook" class="px-6 py-3.5 rounded-xl bg-[#5A52FF] text-white text-[15px] font-bold hover:bg-[#4a42e5] transition shadow-lg shadow-indigo-200 border-none cursor-pointer flex items-center justify-center" style="color: white !important;">
            <span class="text-white" style="color: white !important;">一键预约 {{ frequentSeatStr }}</span>
          </button>
        </div>
      </div>
    </main>
    
    <!-- All Rooms Modal -->
    <div v-if="showAllRooms" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6 md:p-12">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showAllRooms = false"></div>
      
      <!-- Modal Content -->
      <div class="bg-gray-50 w-full max-w-6xl h-full md:max-h-full rounded-[2rem] shadow-2xl relative flex flex-col overflow-hidden animate-fade-in-up">
        <!-- Header -->
        <div class="bg-white px-8 py-6 border-b border-gray-100 flex items-center justify-between shrink-0">
          <div>
            <h2 class="text-2xl font-bold text-gray-900 tracking-wide">全部自习室</h2>
            <p class="text-[13px] text-gray-500 mt-1 font-medium">浏览并选择适合您的学习空间，共找到 {{ studyRooms.length }} 个结果</p>
          </div>
          <button @click="showAllRooms = false" class="w-10 h-10 bg-gray-50 hover:bg-gray-100 text-gray-500 rounded-full flex items-center justify-center transition cursor-pointer border-none">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>
        
        <!-- Room List -->
        <div class="p-8 overflow-y-auto flex-1">
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div v-for="room in studyRooms" :key="'modal-'+room.id" class="bg-white rounded-[1.25rem] overflow-hidden shadow-sm border border-gray-100 flex flex-col hover:shadow-md transition duration-300 group">
              <!-- Image -->
              <div class="h-48 relative bg-gray-200 overflow-hidden">
                <img :src="room.image" class="w-full h-full object-cover group-hover:scale-105 transition duration-500" />
                <!-- Tag removed -->
              </div>
              <!-- Body -->
              <div class="p-6 flex flex-col gap-5 flex-1">
                <div>
                  <h3 class="font-bold text-lg text-gray-900">{{ room.name }}</h3>
                  <div class="flex items-center gap-1.5 text-[13px] text-gray-500 mt-1.5 font-medium">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                    {{ room.location }}
                  </div>
                </div>
                
                <div class="flex items-center gap-8 mt-1">
                  <div class="flex items-center gap-3">
                    <div class="w-[2.25rem] h-[2.25rem] rounded-full bg-[#f0fdf4] text-[#22c55e] flex items-center justify-center">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                    </div>
                    <div>
                      <div class="text-[11px] text-gray-400 font-medium">剩余座位</div>
                      <div class="text-sm font-black text-gray-900 mt-0.5">{{ getAvailableSeatsCount(room.id) }}</div>
                    </div>
                  </div>
                  
                  <div class="flex items-center gap-3">
                    <div class="w-[2.25rem] h-[2.25rem] rounded-full bg-[#eff6ff] text-[#3b82f6] flex items-center justify-center">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    </div>
                    <div>
                      <div class="text-[11px] text-gray-400 font-medium">开放时间</div>
                      <div class="text-sm font-black text-gray-900 mt-0.5">{{ room.openTime }}</div>
                    </div>
                  </div>
                </div>

                <button @click="openRoomLayout(room.id, modalMode)" class="mt-4 w-full bg-[#0f172a] text-white py-3 rounded-xl text-sm font-bold hover:bg-gray-800 transition shadow-md border-none cursor-pointer flex items-center justify-center">
                  <span class="text-white">{{ modalMode === 'setFrequent' ? '选择该自习室' : '去预选座位' }}</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Seat Selection Modal -->
    <div v-if="showSeatModal" class="fixed inset-0 z-[110] flex items-center justify-center p-4 sm:p-6 md:p-12">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="showSeatModal = false"></div>
      
      <!-- Modal Content -->
      <div class="bg-gray-50 w-full max-w-4xl h-full md:max-h-full rounded-[2rem] shadow-2xl relative flex flex-col overflow-hidden animate-fade-in-up">
        <!-- Header -->
        <div class="bg-white px-8 py-6 border-b border-gray-100 flex items-center justify-between shrink-0">
          <div>
            <h2 class="text-2xl font-bold text-gray-900 tracking-wide">{{ currentRoom?.name }} - {{ modalMode === 'book' ? '座位预选' : '设置常用' }}</h2>
            <p class="text-[13px] text-gray-500 mt-1 font-medium">点击绿色可用座位即可{{ modalMode === 'book' ? '快速预约' : '设为常用' }}</p>
          </div>
          <button @click="showSeatModal = false" class="w-10 h-10 bg-gray-50 hover:bg-gray-100 text-gray-500 rounded-full flex items-center justify-center transition cursor-pointer border-none p-0">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>
        
        <!-- Legend -->
        <div class="flex flex-wrap items-center justify-center gap-4 sm:gap-6 py-4 bg-white border-b border-gray-100 shrink-0">
            <div class="flex items-center gap-2">
                <div class="w-4 h-4 rounded bg-green-500 shadow-sm border border-green-600"></div><span class="text-sm text-gray-600 font-bold">可用</span>
            </div>
            <div class="flex items-center gap-2">
                <div class="w-4 h-4 rounded bg-red-500 shadow-sm border border-red-600"></div><span class="text-sm text-gray-600 font-bold">已占用</span>
            </div>
            <div class="flex items-center gap-2">
                <div class="w-4 h-4 rounded bg-gray-400 shadow-sm border border-gray-500"></div><span class="text-sm text-gray-600 font-bold">维修中</span>
            </div>
            <div class="flex items-center gap-2">
                <div class="w-4 h-4 rounded bg-yellow-400 shadow-sm border border-yellow-500"></div><span class="text-sm text-gray-600 font-bold">我的预约</span>
            </div>
        </div>

        <!-- Seat Grid -->
        <div class="p-8 overflow-y-auto flex-1 flex justify-center items-start bg-gray-100/50">
            <!-- Simulated desk/room area -->
            <div class="grid gap-3 sm:gap-4 lg:gap-5 bg-white p-6 md:p-8 rounded-[1.5rem] shadow-sm border border-gray-200"
                 :style="{ gridTemplateColumns: `repeat(${currentRoom?.cols || 8}, minmax(0, 1fr))` }">
                <button v-for="seat in seats" :key="seat.id" @click="selectSeat(seat)"
                    :class="{
                        'bg-green-500 hover:bg-green-600 hover:scale-[1.15] active:scale-95 cursor-pointer shadow-[0_4px_12px_rgba(34,197,94,0.3)] z-10': seat.status === 'available',
                        'bg-red-500 opacity-80': seat.status === 'occupied',
                        'bg-gray-400 opacity-60': seat.status === 'maintenance',
                        'bg-yellow-400 shadow-[0_4px_12px_rgba(250,204,21,0.3)]': seat.status === 'booked',
                        'cursor-not-allowed': modalMode === 'book' && seat.status !== 'available',
                        'hover:scale-[1.15] active:scale-95 cursor-pointer z-10 hover:shadow-lg': modalMode === 'setFrequent'
                    }"
                    class="w-[2.5rem] h-[2.5rem] sm:w-[3rem] sm:h-[3rem] md:w-[3.5rem] md:h-[3.5rem] rounded-[10px] sm:rounded-xl transition-all duration-300 flex items-center justify-center border border-black/10 group relative border-none p-0 outline-none">
                    <span class="text-[11px] md:text-[13px] font-black text-white/95 drop-shadow-sm">{{ seat.seatNumber }}</span>
                </button>
            </div>
        </div>
      </div>
    </div>

    <!-- Global Success Toast -->
    <div v-if="bookingSuccessMsg" class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-[200] bg-[#121624]/95 text-white px-10 py-8 rounded-[2rem] shadow-2xl flex flex-col items-center gap-4 animate-fade-in-up backdrop-blur-xl border border-gray-800">
      <div class="w-16 h-16 rounded-full bg-green-500 flex items-center justify-center mb-2 shadow-[0_4px_16px_rgba(34,197,94,0.4)]">
        <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"></path></svg>
      </div>
      <div class="text-xl font-black tracking-wide text-white">座位预约成功</div>
      <div class="text-sm font-bold text-gray-400 tracking-wider">正在前往我的预约...</div>
    </div>

    <!-- Global Fail Toast -->
    <div v-if="showFailToastMsg" class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-[200] bg-[#121624]/95 text-white px-10 py-8 rounded-[2rem] shadow-2xl flex flex-col items-center gap-4 animate-fade-in-up backdrop-blur-xl border border-gray-800">
      <div class="w-16 h-16 rounded-full bg-red-500/10 flex items-center justify-center mb-2 shadow-[0_4px_16px_rgba(239,68,68,0.2)]">
        <svg class="w-8 h-8 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M6 18L18 6M6 6l12 12"></path></svg>
      </div>
      <div class="text-xl font-black tracking-wide text-white">该座位目前不可用</div>
      <div class="text-sm font-bold text-gray-400 tracking-wider">该常用座位可能被占用或在维护中</div>
    </div>

    <!-- Confirm Frequent Seat Modal -->
    <div v-if="showConfirmModal" class="fixed inset-0 z-[120] flex items-center justify-center p-4 sm:p-6">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="cancelSetFrequent"></div>
      <div class="bg-white w-full max-w-sm rounded-[1.5rem] shadow-2xl relative p-6 sm:p-8 flex flex-col items-center animate-fade-in-up text-center">
        <div class="w-14 h-14 rounded-full bg-indigo-50 text-[#5A52FF] flex items-center justify-center mb-5">
          <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"></path></svg>
        </div>
        <h3 class="text-xl font-bold text-gray-900 mb-2">设置常用座位</h3>
        <p class="text-[14px] text-gray-500 mb-6 font-medium px-4 leading-relaxed">确认将“<span class="text-gray-800 font-bold mx-1">{{ selectedSeatForFrequent?.id }} 号座位</span>”设置为常用座位吗？</p>
        <div class="flex gap-3 w-full">
          <button @click="cancelSetFrequent" class="flex-1 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-bold rounded-xl transition cursor-pointer border-none">取消</button>
          <button @click="confirmSetFrequent" class="flex-1 py-3 bg-[#5A52FF] hover:bg-[#4a42e5] text-white font-bold rounded-xl transition shadow-md shadow-indigo-200 cursor-pointer border-none">确认</button>
        </div>
      </div>
    </div>
    <!-- Announcements List Modal -->
    <div v-if="showAnnouncementModal" class="fixed inset-0 z-[150] flex items-center justify-center p-4 sm:p-6 md:p-12">
      <div class="absolute inset-0 bg-[#0A0D18]/80 backdrop-blur-md" @click="showAnnouncementModal = false"></div>
      <div class="bg-white w-full max-w-2xl h-full md:max-h-[80vh] rounded-[2.5rem] shadow-2xl relative flex flex-col overflow-hidden animate-fade-in-up">
        <div class="px-8 py-8 border-b border-gray-50 flex items-center justify-between shrink-0 bg-white z-10">
          <div>
            <h2 class="text-2xl font-black text-gray-900 tracking-tight">所有公告</h2>
            <p class="text-[13px] text-gray-400 mt-1 font-bold">获取自习室最新动态与规则说明</p>
          </div>
          <button @click="showAnnouncementModal = false" class="w-10 h-10 bg-gray-50 hover:bg-gray-100 text-gray-400 rounded-full flex items-center justify-center transition cursor-pointer border-none">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>
        
        <div class="p-8 overflow-y-auto flex-1 space-y-6 bg-gray-50/50">
          <div v-for="anno in announcements" :key="'modal-anno-'+anno.id" class="bg-white p-7 rounded-[2rem] border border-gray-100 shadow-sm transition hover:shadow-md group">
            <div class="flex items-center gap-3 mb-4">
              <span class="px-3 py-1 rounded-lg bg-orange-50 text-orange-500 text-[10px] font-black uppercase tracking-widest">重要通知</span>
              <span class="text-[11px] font-bold text-gray-300">{{ anno.date }}</span>
            </div>
            <h3 class="text-lg font-black text-gray-900 mb-3 group-hover:text-[#5A52FF] transition">{{ anno.title }}</h3>
            <p class="text-[14px] font-bold text-gray-500 leading-relaxed">{{ anno.content }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

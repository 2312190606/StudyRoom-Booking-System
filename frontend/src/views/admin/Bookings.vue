<script setup>
import { ref, computed, onMounted } from 'vue'
import { showConfirmDialog, showNotify, showLoadingToast } from 'vant'
import { getAdminReservations, adminCancelReservation } from '../../api/admin'

const loading = ref(false)
const bookingsList = ref([])

const activeFilter = ref('全部状态')
const filters = ['全部状态', '进行中', '已完成', '已取消', '违约记录']
const searchText = ref('')

// Pagination State
const currentPage = ref(1)
const pageSize = 10

// Load data
const loadBookings = async () => {
  loading.value = true
  try {
    const data = await getAdminReservations({ page: 1, size: 100 })
    const records = Array.isArray(data) ? data : (data.records || [])
    bookingsList.value = records.map(b => {
      // 状态: 0=已取消, 1=待使用, 2=使用中, 3=已完成, 4=违约
      let status = '预约中'
      let statusClass = 'bg-blue-500/10 text-blue-500'
      if (b.status === 2) {
        status = '已签到'
        statusClass = 'bg-emerald-500/10 text-emerald-500'
      } else if (b.status === 3) {
        status = '已完成'
        statusClass = 'bg-emerald-500/10 text-emerald-500'
      } else if (b.status === 0) {
        status = '已取消'
        statusClass = 'bg-red-500/10 text-red-500'
      } else if (b.status === 4) {
        status = '违约'
        statusClass = 'bg-red-500/10 text-red-500'
      }

      // 座位编号
      const seatNumber = b.seat?.seatNumber || (b.seat?.positionX && b.seat?.positionY
        ? String.fromCharCode(64 + b.seat.positionX) + b.seat.positionY
        : '--')

      return {
        id: b.id,
        type: 'STANDARD TYPE',
        user: b.nickName || b.userName || '未知用户',
        userId: b.userId,
        room: b.roomName || '--',
        seat: seatNumber,
        time: b.startTime ? b.startTime.substring(0, 11) : '--', // MM-dd HH:mm 取前面部分
        duration: (() => {
          if (b.startTime && b.endTime) {
            // 解析 MM-dd HH:mm 格式
            const startParts = b.startTime.substring(11).split(':').map(Number)
            const endParts = b.endTime.substring(11).split(':').map(Number)
            const startMins = startParts[0] * 60 + startParts[1]
            const endMins = endParts[0] * 60 + endParts[1]
            const hours = (endMins - startMins) / 60
            return `${hours.toFixed(1)}H`
          }
          return '--'
        })(),
        status,
        statusClass,
        initial: (b.nickName || b.userName || '未').charAt(0)
      }
    })
  } catch (error) {
    console.error('加载预约记录失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadBookings()
})

// Computed Logic
const filteredBookings = computed(() => {
  let list = bookingsList.value

  if (activeFilter.value !== '全部状态') {
    if (activeFilter.value === '进行中') {
      list = list.filter(b => b.status === '预约中' || b.status === '已签到')
    } else if (activeFilter.value === '违约记录') {
      list = list.filter(b => b.status === '已过期' || b.status === '违约')
    } else {
      list = list.filter(b => b.status === activeFilter.value)
    }
  }

  const query = searchText.value.trim().toLowerCase()
  if (query) {
    list = list.filter(b =>
      b.id.toString().toLowerCase().includes(query) ||
      b.user.toLowerCase().includes(query) ||
      b.userId.toString().toLowerCase().includes(query)
    )
  }

  return list
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredBookings.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredBookings.value.length / pageSize))

// Actions
const setPage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
}

const handleDelete = (id) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除这条预约记录吗？此操作不可撤销。',
    theme: 'round-button'
  }).then(async () => {
    showLoadingToast({ message: '删除中...', forbidClick: true })
    try {
      await adminCancelReservation(id)
      showNotify({ type: 'success', message: '记录已成功删除' })
      await loadBookings()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}
</script>

<template>
  <div class="flex flex-col gap-8 pb-10 min-h-[85vh]">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">预约记录</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">查看用户预约详情、处理签到及违约状态</p>
      </div>
    </div>

    <!-- Search & Filters -->
    <div class="bg-[#121624] border border-[#1C2136] rounded-[32px] p-8 flex flex-col gap-6 shadow-xl">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
        <div>
          <h3 class="text-lg font-black text-white tracking-wide mb-1">预约记录检索</h3>
          <p class="text-[11px] font-bold text-gray-550 opacity-40 uppercase tracking-widest">Advanced Audit & Tracking</p>
        </div>
        <div class="flex items-center gap-4">
           <div class="relative group w-full md:w-[350px]">
            <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 group-focus-within:text-[#5A52FF] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
            <input v-model="searchText" type="text" placeholder="搜索姓名/订单号/用户ID..." class="w-full bg-[#0B0F19] border border-[#1C2136] text-white pl-11 pr-5 py-4 rounded-2xl text-sm focus:border-[#5A52FF]/50 outline-none transition-all font-bold tracking-tight">
          </div>
        </div>
      </div>

      <div class="flex flex-wrap gap-3">
        <button v-for="f in filters" :key="f" @click="activeFilter = f; currentPage = 1"
          :class="activeFilter === f ? 'bg-[#5A52FF] text-white shadow-[0_4px_12px_rgba(90,82,255,0.3)]' : 'bg-[#1A1F30] text-gray-400 hover:text-white border border-white/5'"
          class="px-6 py-3 rounded-2xl text-[13px] font-black tracking-wide transition-all active:scale-95 border-none cursor-pointer">
          {{ f }}
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="text-gray-500 font-bold">加载中...</div>
    </div>

    <!-- Table -->
    <div v-else class="bg-[#121624] border border-[#1C2136] rounded-[32px] overflow-hidden shadow-2xl relative">
      <table class="w-full text-left border-collapse">
        <thead class="bg-[#1A1F30]/50 border-b border-[#1C2136]">
          <tr>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] border-r border-white/5">订单信息</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] border-r border-white/5">用户详情</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] border-r border-white/5">自习室/座位</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] border-r border-white/5">预约时间</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] text-center border-r border-white/5">状态</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] text-right">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#1C2136]">
          <tr v-for="b in paginatedData" :key="b.id" class="hover:bg-white/[0.03] transition-colors group">
            <td class="px-8 py-7 border-r border-white/5">
              <div class="text-[14px] font-black text-white tracking-widest mb-1.5 font-mono">{{ b.id }}</div>
              <div class="text-[10px] text-gray-550 font-black tracking-widest opacity-40 uppercase">{{ b.type }}</div>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-xl bg-[#1A1F30] border border-[#2A314D] flex items-center justify-center text-[#5A52FF] text-[14px] font-black group-hover:border-[#5A52FF]/50 transition-colors">
                  {{ b.initial }}
                </div>
                <div>
                  <div class="text-[15px] font-black text-white leading-none mb-1.5 tracking-tight">{{ b.user }}</div>
                  <div class="text-[11px] text-gray-550 font-bold tracking-widest opacity-60 font-mono">{{ b.userId }}</div>
                </div>
              </div>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="text-[14px] font-black text-white tracking-wide mb-1.5">{{ b.room || '--' }}</div>
              <div class="text-[11px] text-[#5A52FF] font-black tracking-[0.15em] uppercase ring-1 ring-[#5A52FF]/30 px-2 py-0.5 rounded-md inline-block">{{ b.seat || '--' }}</div>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="text-[14px] font-black text-white tracking-widest mb-1.5">{{ b.time }}</div>
              <div class="flex items-center gap-2 text-[10px] text-gray-550 font-black uppercase opacity-50">
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                FOR {{ b.duration }}
              </div>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="flex justify-center">
                <div :class="b.statusClass" class="text-[10px] font-black px-4 py-2 rounded-xl tracking-[0.15em] uppercase text-center min-w-[80px] ring-1 ring-inset ring-white/5 shadow-sm">
                  {{ b.status }}
                </div>
              </div>
            </td>
            <td class="px-8 py-7 text-right">
              <button @click="handleDelete(b.id)" class="w-10 h-10 rounded-xl bg-[#1A1F30] border border-[#2A314D] text-gray-500 hover:text-red-500 hover:border-red-500/50 hover:bg-red-500/10 transition-all flex items-center justify-center m-auto mr-0 group/btn">
                <svg class="w-5 h-5 transition-transform group-hover/btn:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Empty State -->
      <div v-if="filteredBookings.length === 0" class="py-24 flex flex-col items-center justify-center bg-[#121624]">
        <div class="w-20 h-20 rounded-[2rem] bg-[#1A1F30] flex items-center justify-center mb-6 text-gray-600">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
        </div>
        <p class="text-gray-500 font-black tracking-widest uppercase text-xs">No records found</p>
      </div>

      <!-- Consistent Info Bar -->
      <div class="px-8 py-5 bg-[#1A1F30]/30 border-t border-[#1C2136] text-[11px] font-bold text-gray-600 tracking-wide">
        Showing {{ filteredBookings.length }} records total
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="mt-4 flex items-center justify-center gap-2">
      <button @click="setPage(currentPage - 1)" :disabled="currentPage === 1" class="w-12 h-12 rounded-2xl bg-[#121624] border border-[#1C2136] text-white flex items-center justify-center disabled:opacity-30 disabled:cursor-not-allowed hover:border-[#5A52FF] transition-all">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"></path></svg>
      </button>

      <div class="flex items-center gap-2 px-6 py-3 bg-[#121624] border border-[#1C2136] rounded-2xl">
        <span class="text-[14px] font-black text-[#5A52FF]">{{ currentPage }}</span>
        <span class="text-[12px] font-black text-gray-550 opacity-40">/</span>
        <span class="text-[14px] font-black text-gray-550">{{ totalPages }}</span>
      </div>

      <button @click="setPage(currentPage + 1)" :disabled="currentPage === totalPages" class="w-12 h-12 rounded-2xl bg-[#121624] border border-[#1C2136] text-white flex items-center justify-center disabled:opacity-30 disabled:cursor-not-allowed hover:border-[#5A52FF] transition-all">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
      </button>
    </div>
  </div>
</template>

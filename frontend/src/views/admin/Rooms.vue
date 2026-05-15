<script setup>
import { ref, computed, onMounted } from 'vue'
import { showConfirmDialog, showNotify, showLoadingToast, closeToast } from 'vant'
import { getAdminRooms, createRoom, updateRoom, deleteRoom } from '../../api/admin'

const loading = ref(false)
const roomsList = ref([])

const searchText = ref('')
const showModal = ref(false)
const modalMode = ref('add')
const form = ref({
  id: null,
  name: '',
  location: '',
  totalSeats: 40,
  availableSeats: 40,
  seatRows: 5,
  cols: 8,
  status: 1,
  openingTime: '08:00:00',
  closingTime: '22:00:00',
  image: 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=800'
})

// Layout Setting State
const showLayoutModal = ref(false)
const currentLayoutRoom = ref(null)
const layoutTempData = ref({
  rows: 0,
  cols: 0,
  seats: {}
})

// Load data
const loadRooms = async () => {
  loading.value = true
  try {
    const data = await getAdminRooms()
    // 拦截器返回的是 res.data，可能是数组或分页对象
    const rooms = Array.isArray(data) ? data : (data.records || [])

    roomsList.value = rooms.map(r => {
      // 从maintenanceSeats字段计算维修数量
      let maintenanceCount = 0
      if (r.maintenanceSeats) {
        try {
          const list = JSON.parse(r.maintenanceSeats)
          maintenanceCount = Array.isArray(list) ? list.length : 0
        } catch (e) {
          maintenanceCount = 0
        }
      }
      return {
        ...r,
        maintenanceCount,
        statusClass: r.status === 1 ? 'text-emerald-500 bg-emerald-500/10' :
                     r.status === 2 ? 'text-amber-500 bg-amber-500/10' :
                     'text-red-500 bg-red-500/10',
        displayStatus: r.status === 1 ? '开放中' :
                       r.status === 2 ? '维护中' : '已关闭'
      }
    })
  } catch (error) {
    console.error('加载自习室失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRooms()
})

// Computed Logic
const filteredRooms = computed(() => {
  const query = searchText.value.trim().toLowerCase()
  if (!query) return roomsList.value
  return roomsList.value.filter(r =>
    r.name.toLowerCase().includes(query) ||
    r.location.toLowerCase().includes(query)
  )
})

// Actions
const openAddModal = () => {
  modalMode.value = 'add'
  form.value = {
    id: null,
    name: '',
    location: '',
    totalSeats: 40,
    availableSeats: 40,
    seatRows: 5,
    cols: 8,
    status: 1,
    openingTime: '08:00:00',
    closingTime: '22:00:00',
    image: 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=800'
  }
  showModal.value = true
}

const openEditModal = (room) => {
  modalMode.value = 'edit'
  form.value = { ...room }
  showModal.value = true
}

const handleSaveRoom = async () => {
  if (!form.value.name || !form.value.location) {
    showNotify({ type: 'warning', message: '请完善自习室基本信息' })
    return
  }

  showLoadingToast({ message: '保存中...', forbidClick: true })
  try {
    // 补全秒数以符合后端LocalTime格式
    const ensureFullTime = (t) => {
      if (!t) return '08:00:00'
      if (t.split(':').length === 2) return t + ':00'
      return t
    }

    const payload = {
      name: form.value.name,
      location: form.value.location,
      seatRows: form.value.seatRows,
      cols: form.value.cols,
      status: form.value.status,
      openingTime: ensureFullTime(form.value.openingTime),
      closingTime: ensureFullTime(form.value.closingTime),
      image: form.value.image
    }

    if (modalMode.value === 'add') {
      await createRoom(payload)
      showNotify({ type: 'success', message: '自习室创建成功' })
    } else {
      await updateRoom(form.value.id, payload)
      showNotify({ type: 'success', message: '自习室配置已更新' })
    }
    showModal.value = false
    await loadRooms()
  } catch (error) {
    console.error('保存失败', error)
  }
}

const handleDeleteRoom = (id) => {
  showConfirmDialog({
    title: '删除确认',
    message: '确定要删除该自习室吗？删除后所有座位资源将一并移除。',
    theme: 'round-button'
  }).then(async () => {
    showLoadingToast({ message: '删除中...', forbidClick: true })
    try {
      await deleteRoom(id)
      showNotify({ type: 'success', message: '自习室已删除' })
      await loadRooms()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

// Layout Functions
const openLayoutModal = async (room) => {
  currentLayoutRoom.value = room
  layoutTempData.value = {
    rows: room.seatRows || 5,
    cols: room.cols || 8,
    seats: {}  // { "row-col": "maintenance" }
  }

  // 从自习室的maintenanceSeats字段加载维修位置
  if (room.maintenanceSeats) {
    try {
      const maintenanceList = JSON.parse(room.maintenanceSeats)
      for (const pos of maintenanceList) {
        layoutTempData.value.seats[pos] = 'maintenance'
      }
    } catch (e) {
      console.error('解析维修座位失败', e)
    }
  }

  showLayoutModal.value = true
}

const toggleSeatMaintenance = (row, col) => {
  const key = `${row}-${col}`
  if (layoutTempData.value.seats[key] === 'maintenance') {
    delete layoutTempData.value.seats[key]
  } else {
    layoutTempData.value.seats[key] = 'maintenance'
  }
}

const saveLayoutChanges = async () => {
  showLoadingToast({ message: '保存中...', forbidClick: true })
  try {
    const currentRoom = currentLayoutRoom.value
    const newRows = layoutTempData.value.rows
    const newCols = layoutTempData.value.cols

    // 构建维修座位列表
    const maintenanceList = Object.keys(layoutTempData.value.seats)
        .filter(k => layoutTempData.value.seats[k] === 'maintenance')

    // 更新自习室的行列数和维修座位（后台自动计算可用座位数）
    await updateRoom(currentRoom.id, {
      seatRows: newRows,
      cols: newCols,
      maintenanceSeats: JSON.stringify(maintenanceList)
    })

    closeToast()
    showNotify({ type: 'success', message: '布局配置已激活' })
    showLayoutModal.value = false

    await loadRooms()
  } catch (error) {
    closeToast()
    console.error('保存失败', error)
  }
}
// Time formatting helper
const formatTimeInput = (field, event) => {
  let val = event.target.value.replace(/\D/g, '') // 只保留数字
  if (val.length > 4) val = val.slice(0, 4) // 最多4位
  
  let formatted = val
  if (val.length >= 3) {
    formatted = val.slice(0, 2) + ':' + val.slice(2)
  }
  
  form.value[field] = formatted
}
</script>

<template>
  <div class="flex flex-col gap-8 pb-10 min-h-[85vh]">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">自习室管理</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">进行空间行列布局配置、维护特定故障座位</p>
      </div>
      <div class="flex items-center gap-4">
        <div class="relative group">
          <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 group-focus-within:text-[#5A52FF] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
          <input v-model="searchText" type="text" placeholder="搜索自习室名称或地点..." class="bg-[#121624] border border-[#1C2136] text-white pl-11 pr-5 py-3 rounded-2xl text-sm focus:border-[#5A52FF]/50 outline-none transition-all w-72 font-bold tracking-tight shadow-lg">
        </div>
        <button @click="openAddModal" class="bg-[#5A52FF] hover:bg-[#4a42e5] text-white px-7 py-3 rounded-2xl flex items-center gap-2 text-sm font-black shadow-[0_8px_20px_rgba(90,82,255,0.3)] transition-all active:scale-95">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path></svg>
          新增自习室
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="text-gray-500 font-bold">加载中...</div>
    </div>

    <!-- Empty State -->
    <div v-else-if="roomsList.length === 0" class="flex flex-col items-center justify-center py-20 bg-[#121624] border border-dashed border-[#1C2136] rounded-[32px]">
      <div class="w-20 h-20 rounded-[2rem] bg-[#1A1F30] flex items-center justify-center mb-6 text-gray-600">
        <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path></svg>
      </div>
      <p class="text-gray-500 font-bold tracking-widest uppercase text-xs">暂无自习室数据</p>
    </div>

    <!-- Room List -->
    <div v-else class="grid grid-cols-1 gap-8">
      <div v-for="room in filteredRooms" :key="room.id" class="bg-[#121624] border border-[#1C2136] rounded-[32px] overflow-hidden flex flex-col lg:flex-row group hover:border-[#303753] transition-all shadow-xl hover:shadow-[0_20px_40px_rgba(0,0,0,0.4)]">
        <div class="lg:w-[400px] h-[300px] lg:h-auto relative overflow-hidden">
          <img :src="room.image || 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=800'" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700 ease-out" alt="Room image">
          <div class="absolute inset-0 bg-gradient-to-r from-[#121624] via-[#121624]/40 to-transparent"></div>
          <div class="absolute bottom-6 left-6">
            <span :class="room.statusClass" class="px-4 py-1.5 rounded-xl text-[10px] font-black tracking-[0.2em] uppercase shadow-lg ring-1 ring-inset ring-white/5">
              {{ room.displayStatus || room.status }}
            </span>
          </div>
        </div>

        <div class="flex-1 p-10 flex flex-col justify-between relative">
          <div class="absolute top-0 right-0 w-64 h-64 bg-[#5A52FF]/5 rounded-full blur-[80px] pointer-events-none"></div>

          <div>
            <div class="flex justify-between items-start mb-6">
              <div>
                <h3 class="text-2xl font-black text-white mb-3 tracking-wide group-hover:text-[#5A52FF] transition-colors">{{ room.name }}</h3>
                <div class="flex items-center gap-2.5 text-gray-550 text-[14px] font-bold">
                  <div class="p-1.5 rounded-lg bg-[#1A1F30] text-[#5A52FF]">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M17.657 16.657L13.414 20.9l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                  </div>
                  {{ room.location }}
                </div>
              </div>
            </div>

            <div class="grid grid-cols-2 lg:grid-cols-3 gap-8 py-8 border-y border-dashed border-[#1C2136] my-6">
              <div class="flex flex-col gap-1.5">
                <div class="text-[10px] font-black text-gray-550 tracking-[0.2em] uppercase opacity-60">座位总数</div>
                <div class="text-2xl font-black text-white tracking-widest">{{ room.totalSeats || (room.seatRows * room.cols) }}</div>
              </div>
              <div class="flex flex-col gap-1.5">
                <div class="text-[10px] font-black text-gray-550 tracking-[0.2em] uppercase opacity-60">维修数量</div>
                <div class="text-2xl font-black text-red-500 tracking-widest">{{ room.maintenanceCount || 0 }}</div>
              </div>
              <div class="flex flex-col gap-1.5">
                <div class="text-[10px] font-black text-gray-550 tracking-[0.2em] uppercase opacity-60">行列布局</div>
                <div class="text-lg font-black text-white tracking-widest">{{ room.seatRows || 0 }} × {{ room.cols || 0 }}</div>
              </div>
            </div>
          </div>

          <div class="flex items-center gap-4 mt-6">
            <button @click="openLayoutModal(room)" class="flex-1 lg:flex-none lg:min-w-[180px] bg-[#1A1F30] hover:bg-[#5A52FF] hover:shadow-[0_8px_20px_rgba(90,82,255,0.2)] text-white py-4 rounded-2xl text-[13px] font-black transition-all border border-white/5 flex items-center justify-center gap-3 active:scale-95 cursor-pointer">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
              布局设置
            </button>
            <button @click="openEditModal(room)" class="w-14 h-14 bg-[#1A1F30] hover:bg-white/5 border border-white/5 rounded-2xl flex items-center justify-center text-gray-400 hover:text-white transition-all cursor-pointer">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
            </button>
            <button @click="handleDeleteRoom(room.id)" class="w-14 h-14 bg-red-500/10 hover:bg-red-500 text-red-500 hover:text-white border border-red-500/20 rounded-2xl flex items-center justify-center transition-all cursor-pointer">
              <svg class="w-5 h-5 font-black" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Add New Room Card -->
      <button @click="openAddModal" class="bg-[#121624] border-2 border-[#1C2136] border-dashed rounded-[32px] p-16 flex flex-col items-center justify-center group hover:border-[#5A52FF] hover:bg-[#5A52FF]/5 transition-all cursor-pointer shadow-sm">
        <div class="w-20 h-20 rounded-[2.5rem] bg-[#1A1F30] border-2 border-[#1C2136] flex items-center justify-center text-gray-600 group-hover:text-[#5A52FF] group-hover:border-[#5A52FF]/50 transition-all mb-6">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path></svg>
        </div>
        <h3 class="text-2xl font-black text-gray-500 group-hover:text-white transition-colors mb-2 tracking-wide">添加新自习室</h3>
        <p class="text-[14px] font-bold text-gray-600 tracking-widest uppercase opacity-40">Create New Environment</p>
      </button>
    </div>

    <!-- Basic Config Modal -->
    <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div @click="showModal = false" class="absolute inset-0 bg-[#0A0D18]/85 backdrop-blur-md transition-opacity"></div>

      <div class="bg-[#121624] border border-[#1C2136] rounded-[48px] w-full max-w-2xl shadow-2xl relative z-10 overflow-hidden animate-modal">
        <div class="px-10 py-10">
          <div class="flex items-center justify-between mb-10">
            <div>
              <h2 class="text-2xl font-black text-white tracking-wide">{{ modalMode === 'add' ? '新增自习室' : '编辑描述信息' }}</h2>
              <p class="text-gray-550 text-xs font-bold uppercase tracking-widest mt-1 opacity-60">Identity & Location Setup</p>
            </div>
            <button @click="showModal = false" class="w-12 h-12 rounded-full bg-[#1A1F30] text-gray-500 hover:text-white transition flex items-center justify-center border-none cursor-pointer group">
              <svg class="w-5 h-5 group-hover:rotate-90 transition-transform duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
            </button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">自习室名称</label>
              <input v-model="form.name" type="text" placeholder="例: 极客工坊 - 3楼" class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] px-6 py-4 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none" />
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">地理位置</label>
              <input v-model="form.location" type="text" placeholder="例: 图书馆三层东侧" class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] px-6 py-4 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none" />
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">默认行列数</label>
              <div class="flex items-center gap-3">
                <div class="flex-1 flex items-center bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] overflow-hidden focus-within:border-[#5A52FF] transition-all">
                  <input v-model.number="form.seatRows" type="number" class="w-full bg-transparent px-6 py-4 text-[14px] font-bold text-white outline-none" />
                </div>
                <span class="text-gray-700 font-black">×</span>
                <div class="flex-1 flex items-center bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] overflow-hidden focus-within:border-[#5A52FF] transition-all">
                  <input v-model.number="form.cols" type="number" class="w-full bg-transparent px-6 py-4 text-[14px] font-bold text-white outline-none" />
                </div>
              </div>
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">运营状态</label>
              <div class="flex gap-2.5">
                <button v-for="s in [{v:1,l:'开放中'}, {v:2,l:'维护中'}, {v:0,l:'已关闭'}]" :key="s.v" @click="form.status = s.v"
                  :class="form.status === s.v ? 'bg-[#5A52FF] text-white border-[#5A52FF] shadow-[0_4px_12px_rgba(90,82,255,0.2)]' : 'bg-[#0A0D18] text-gray-550 border-[#1C2136]'"
                  class="flex-1 py-4 rounded-2xl text-[12px] font-black transition-all border-2 cursor-pointer">
                  {{ s.l }}
                </button>
              </div>
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">开放时间</label>
              <input :value="form.openingTime" @input="formatTimeInput('openingTime', $event)" type="tel" maxlength="5" placeholder="例: 0800" class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] px-6 py-4 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none" />
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">关闭时间</label>
              <input :value="form.closingTime" @input="formatTimeInput('closingTime', $event)" type="tel" maxlength="5" placeholder="例: 2200" class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] px-6 py-4 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none" />
            </div>

            <div class="flex flex-col gap-3 md:col-span-2">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">封面预览 URL</label>
              <input v-model="form.image" type="text" placeholder="https://unsplash.com/..." class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] px-6 py-4 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none" />
            </div>
          </div>

          <div class="mt-12 flex gap-4">
            <button @click="showModal = false" class="flex-1 bg-[#1A1F30] text-gray-400 py-5 rounded-[22px] text-[15px] font-black hover:bg-[#20263d] hover:text-white transition-all border-none cursor-pointer">取消</button>
            <button @click="handleSaveRoom" class="flex-1 bg-[#5A52FF] text-white py-5 rounded-[22px] text-[15px] font-black shadow-[0_8px_25px_rgba(90,82,255,0.3)] hover:bg-[#4a42e5] transition-all border-none cursor-pointer">
              {{ modalMode === 'add' ? '立即创建' : '保存修改' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Layout Settings Modal -->
    <div v-if="showLayoutModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div @click="showLayoutModal = false" class="absolute inset-0 bg-[#0A0D18]/90 backdrop-blur-xl transition-opacity"></div>

      <div class="bg-[#121624] border border-white/5 rounded-[56px] w-full max-w-4xl shadow-2xl relative z-10 overflow-hidden animate-modal">
        <div class="flex flex-col h-[85vh]">
          <!-- Modal Header -->
          <div class="px-12 pt-12 pb-8 border-b border-white/5 flex items-center justify-between bg-white/[0.02]">
            <div>
              <h2 class="text-3xl font-black text-white tracking-wide mb-2">{{ currentLayoutRoom?.name }} <span class="text-[#5A52FF] mx-2">/</span> 空间布局</h2>
              <p class="text-[12px] font-bold text-gray-500 uppercase tracking-[0.3em]">Grid System & Maintenance Panel</p>
            </div>
            <div class="flex items-center gap-6">
              <div class="flex items-center gap-4 bg-[#0A0D18] px-6 py-3 rounded-2xl border border-white/5">
                <div class="flex flex-col items-center">
                  <span class="text-[9px] font-black text-gray-600 uppercase">Rows</span>
                  <input v-model.number="layoutTempData.rows" type="number" class="w-12 bg-transparent text-center text-white font-black text-lg outline-none" />
                </div>
                <div class="w-px h-8 bg-white/5"></div>
                <div class="flex flex-col items-center">
                  <span class="text-[9px] font-black text-gray-600 uppercase">Cols</span>
                  <input v-model.number="layoutTempData.cols" type="number" class="w-12 bg-transparent text-center text-white font-black text-lg outline-none" />
                </div>
              </div>
              <button @click="showLayoutModal = false" class="w-14 h-14 rounded-full bg-[#1A1F30] text-gray-400 hover:text-white transition flex items-center justify-center border-none cursor-pointer group shadow-xl">
                 <svg class="w-6 h-6 group-hover:rotate-90 transition-transform duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
              </button>
            </div>
          </div>

          <!-- Main Layout Area -->
          <div class="flex-1 overflow-auto p-12 custom-scrollbar">
            <div class="inline-block min-w-full">
              <div class="flex flex-col gap-3 items-center">
                <div v-for="r in layoutTempData.rows" :key="r" class="flex gap-3">
                  <div v-for="c in layoutTempData.cols" :key="c"
                    @click="toggleSeatMaintenance(r, c)"
                    :class="[
                      layoutTempData.seats[`${r}-${c}`] === 'maintenance'
                        ? 'bg-red-500/20 border-red-500/50 text-red-500 shadow-[0_0_15px_rgba(239,68,68,0.2)]'
                        : 'bg-[#1A1F30] border-white/10 text-gray-550 hover:border-[#5A52FF]/50 hover:shadow-[0_0_15px_rgba(90,82,255,0.2)] hover:text-white'
                    ]"
                    class="w-14 h-14 rounded-xl border flex flex-col items-center justify-center cursor-pointer transition-all duration-300 group select-none">
                    <span class="text-[9px] font-black opacity-40 group-hover:opacity-100">{{ String.fromCharCode(64 + r) }}</span>
                    <span class="text-xs font-black">{{ c }}</span>
                    <div v-if="layoutTempData.seats[`${r}-${c}`] === 'maintenance'" class="mt-1">
                      <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer Legend & Actions -->
          <div class="p-10 border-t border-white/5 bg-white/[0.01] flex items-center justify-between">
            <div class="flex gap-8">
              <div class="flex items-center gap-3">
                <div class="w-4 h-4 rounded bg-[#1A1F30] border border-white/10"></div>
                <span class="text-[12px] font-bold text-gray-500 uppercase tracking-widest">正常座位</span>
              </div>
              <div class="flex items-center gap-3">
                <div class="w-4 h-4 rounded bg-red-500/30 border border-red-500/50"></div>
                <span class="text-[12px] font-bold text-gray-500 uppercase tracking-widest">维修中 (不可选)</span>
              </div>
            </div>
            <div class="flex gap-4">
              <button @click="showLayoutModal = false" class="px-8 py-4 rounded-2xl bg-[#1A1F30] text-gray-400 font-black text-sm hover:text-white transition cursor-pointer border-none shadow-sm">放弃修改</button>
              <button @click="saveLayoutChanges" class="px-10 py-4 rounded-2xl bg-[#5A52FF] text-white font-black text-sm shadow-[0_10px_30px_rgba(90,82,255,0.3)] hover:shadow-[0_15px_40px_rgba(90,82,255,0.4)] transition cursor-pointer border-none active:scale-95">更新布局并上线</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-modal {
  animation: modal-in 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes modal-in {
  from { opacity: 0; transform: scale(0.95) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(90, 82, 255, 0.2);
}
</style>

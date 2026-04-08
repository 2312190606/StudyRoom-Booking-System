<script setup>
import { ref, computed } from 'vue'
import { showConfirmDialog, showNotify } from 'vant'

// Initial Mock Data
import { useAnnouncementStore } from '../../stores/announcement'

const store = useAnnouncementStore()

// Search and Pagination State
const searchText = ref('')
const currentPage = ref(1)
const pageSize = 4

// Modal State
const showModal = ref(false)
const modalType = ref('create') // 'create' | 'edit'
const form = ref({
  id: null,
  title: '',
  content: '',
  status: '发布中'
})

// Computed Properties
const filteredAnnouncements = computed(() => {
  const query = searchText.value.trim().toLowerCase()
  if (!query) return store.announcements
  return store.announcements.filter(a => 
    a.title.toLowerCase().includes(query) || 
    a.content.toLowerCase().includes(query)
  )
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredAnnouncements.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredAnnouncements.value.length / pageSize))

// Actions
const openCreateModal = () => {
  modalType.value = 'create'
  form.value = { id: null, title: '', content: '', status: '发布中' }
  showModal.value = true
}

const openEditModal = (anno) => {
  modalType.value = 'edit'
  form.value = { ...anno }
  showModal.value = true
}

const handleSave = () => {
  if (!form.value.title || !form.value.content) {
    showNotify({ type: 'warning', message: '标题和内容不能为空' })
    return
  }

  if (modalType.value === 'create') {
    const newId = store.announcements.length > 0 ? Math.max(...store.announcements.map(a => a.id)) + 1 : 1
    store.addAnnouncement({
      ...form.value,
      id: newId,
      date: new Date().toISOString().split('T')[0],
      statusClass: form.value.status === '发布中' ? 'text-emerald-500 bg-emerald-500/10' : 'text-gray-500 bg-gray-500/10'
    })
    showNotify({ type: 'success', message: '发布成功' })
  } else {
    store.updateAnnouncement(form.value.id, {
      ...form.value,
      statusClass: form.value.status === '发布中' ? 'text-emerald-500 bg-emerald-500/10' : 'text-gray-500 bg-gray-500/10'
    })
    showNotify({ type: 'success', message: '已更新' })
  }
  showModal.value = false
}

const handleDelete = (id) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除这条公告吗？删除后不可恢复。',
    theme: 'round-button'
  }).then(() => {
    store.deleteAnnouncement(id)
    showNotify({ type: 'success', message: '已删除' })
  }).catch(() => {})
}

const setPage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
}
</script>

<template>
  <div class="flex flex-col gap-8 pb-10 min-h-[80vh]">
    <!-- Header/Search -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-6">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">公告管理</h2>
        <div class="flex items-center gap-3">
          <p class="text-[14px] font-bold text-gray-550 tracking-wider">发布系统公告、维护通知及规则说明</p>
          <div class="px-3 py-1 rounded-full bg-[#1C2136] text-[#5A52FF] text-[11px] font-black tracking-widest uppercase">
            Total: {{ store.announcements.length }}
          </div>
        </div>
      </div>
      <div class="flex items-center gap-4">
        <div class="relative group w-[300px]">
          <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 group-focus-within:text-[#5A52FF] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
          <input v-model="searchText" type="text" placeholder="搜索公告标题或内容..." class="w-full bg-[#121624] border border-[#1C2136] text-white pl-11 pr-5 py-3 rounded-2xl text-sm focus:border-[#5A52FF]/50 outline-none transition-all font-bold tracking-tight">
        </div>
        <button @click="openCreateModal" class="bg-[#5A52FF] hover:bg-[#4a42e5] text-white px-7 py-3 rounded-2xl flex items-center gap-3 text-sm font-black shadow-[0_4px_20px_rgba(90,82,255,0.2)] hover:shadow-[0_8px_25px_rgba(90,82,255,0.4)] transition-all">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path></svg>
          发布新公告
        </button>
      </div>
    </div>

    <!-- Announcement List -->
    <div class="flex flex-col gap-4">
      <transition-group name="list">
        <div v-for="anno in paginatedData" :key="anno.id" class="bg-[#121624] border border-[#1C2136] rounded-[24px] p-7 md:p-8 flex flex-col md:flex-row gap-6 hover:border-[#303753] transition-all group relative overflow-hidden">
          <div class="absolute -right-20 -top-20 w-48 h-48 rounded-full blur-[90px] bg-[#5A52FF]/5 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none"></div>
          
          <div class="flex-1">
            <div class="flex flex-wrap items-center gap-4 mb-4">
              <span :class="anno.statusClass" class="px-3.5 py-1.5 rounded-xl text-[10px] font-black tracking-widest uppercase shadow-sm">{{ anno.status }}</span>
              <div class="flex items-center gap-2 text-gray-550 text-[11px] font-bold opacity-70">
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
                {{ anno.date }}
              </div>
            </div>
            
            <h3 class="text-xl font-black text-white mb-3 tracking-wide group-hover:text-[#5A52FF] transition-colors line-clamp-1">{{ anno.title }}</h3>
            <p class="text-[14px] font-bold text-gray-500 leading-relaxed max-w-[90%] line-clamp-2">{{ anno.content }}</p>
          </div>

          <div class="flex items-center md:flex-col justify-end gap-3 border-l md:pl-10 border-[#1C2136]">
            <button @click="openEditModal(anno)" class="w-12 h-12 rounded-2xl bg-[#1A1F30] border border-[#2A314D] flex items-center justify-center text-gray-400 hover:text-white hover:border-[#5A52FF] hover:bg-[#5A52FF]/10 transition-all group/btn">
              <svg class="w-5 h-5 transition-transform group-hover/btn:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
            </button>
            <button @click="handleDelete(anno.id)" class="w-12 h-12 rounded-2xl bg-red-500/5 border border-red-500/10 flex items-center justify-center text-red-500/70 hover:bg-red-500 hover:text-white hover:border-red-500 transition-all group/btn">
              <svg class="w-5 h-5 transition-transform group-hover/btn:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
            </button>
          </div>
        </div>
      </transition-group>

      <!-- Empty State -->
      <div v-if="filteredAnnouncements.length === 0" class="py-20 flex flex-col items-center justify-center bg-[#121624] border border-dashed border-[#1C2136] rounded-[32px]">
        <div class="w-20 h-20 rounded-full bg-[#1A1F30] flex items-center justify-center mb-6">
          <svg class="w-10 h-10 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path></svg>
        </div>
        <p class="text-gray-500 font-bold">没有找到相关公告</p>
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

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div @click="showModal = false" class="absolute inset-0 bg-[#0A0D18]/80 backdrop-blur-md transition-opacity"></div>
      
      <div class="bg-[#121624] border border-[#1C2136] rounded-[32px] w-full max-w-2xl shadow-2xl relative z-10 overflow-hidden animate-modal">
        <div class="px-10 pt-10 pb-8">
          <div class="flex items-center justify-between mb-10">
            <div>
              <h2 class="text-2xl font-black text-white tracking-wide">{{ modalType === 'create' ? '发布新公告' : '编辑公告内容' }}</h2>
              <p class="text-gray-550 text-xs font-bold uppercase tracking-widest mt-1 opacity-60">System Announcement Update</p>
            </div>
            <button @click="showModal = false" class="w-12 h-12 rounded-full bg-[#1A1F30] text-gray-500 hover:text-white transition flex items-center justify-center border-none cursor-pointer group">
              <svg class="w-5 h-5 mt-0.5 group-hover:rotate-90 transition-transform duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
            </button>
          </div>

          <div class="space-y-8">
            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">公告标题</label>
              <input v-model="form.title" type="text" placeholder="输入公告主标题..." class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[20px] px-6 py-4 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none" />
            </div>
            
            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">公告内容</label>
              <textarea v-model="form.content" rows="6" placeholder="输入公告详细正文内容..." class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[24px] px-6 py-5 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none resize-none leading-relaxed"></textarea>
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">发布状态</label>
              <div class="flex gap-4">
                <button v-for="s in ['发布中', '已下架']" :key="s" @click="form.status = s" 
                  :class="form.status === s ? 'bg-[#5A52FF] text-white border-[#5A52FF]' : 'bg-[#0A0D18] text-gray-500 border-[#1C2136]'"
                  class="flex-1 py-4 rounded-2xl text-[13px] font-black transition-all border-2 cursor-pointer shadow-sm">
                  {{ s }}
                </button>
              </div>
            </div>
          </div>

          <div class="mt-12 flex gap-4">
            <button @click="showModal = false" class="flex-1 bg-[#1A1F30] text-gray-400 py-5 rounded-[20px] text-[15px] font-black hover:bg-[#20263d] hover:text-white transition-all border-none cursor-pointer">取消</button>
            <button @click="handleSave" class="flex-1 bg-[#5A52FF] text-white py-5 rounded-[20px] text-[15px] font-black shadow-[0_8px_25px_rgba(90,82,255,0.3)] hover:bg-[#4a42e5] hover:shadow-[0_10px_30px_rgba(90,82,255,0.4)] transition-all border-none cursor-pointer">
              {{ modalType === 'create' ? '立即发布' : '保存修改' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.animate-modal {
  animation: modal-in 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes modal-in {
  from { opacity: 0; transform: scale(0.9) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.list-enter-active,
.list-leave-active {
  transition: all 0.4s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>

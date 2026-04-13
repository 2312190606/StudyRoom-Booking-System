<script setup>
import { ref, computed, onMounted } from 'vue'
import { showConfirmDialog, showNotify, showLoadingToast } from 'vant'
import { getAdminUsers, createUser, deleteUser, updateUserStatus, updateUser } from '../../api/admin'

const loading = ref(false)
const users = ref([])

const searchText = ref('')
const currentPage = ref(1)
const pageSize = 5

const showModal = ref(false)
const modalMode = ref('add')
const form = ref({
  username: '',
  password: '',
  role: 2,
  status: 1
})

const loadUsers = async () => {
  loading.value = true
  try {
    const data = await getAdminUsers({ page: 1, size: 100 })
    const records = Array.isArray(data) ? data : (data.records || [])
    users.value = records.map(u => ({
      id: u.id,
      username: u.username,
      count: u.bookingCount || 0,
      role: u.role === 1 ? '管理员' : '普通用户',
      roleValue: u.role,
      status: u.status === 1 || u.status === 'NORMAL' ? '正常' : '禁用',
      statusClass: u.status === 1 || u.status === 'NORMAL' ? 'text-emerald-500 bg-emerald-500/10' : 'text-red-500 bg-red-500/10',
      initial: (u.username || '未').charAt(0)
    }))
  } catch (error) {
    console.error('加载用户失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})

const filteredUsers = computed(() => {
  const query = searchText.value.trim().toLowerCase()
  if (!query) return users.value
  return users.value.filter(u =>
    u.username.toLowerCase().includes(query)
  )
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredUsers.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredUsers.value.length / pageSize))

const openAddModal = () => {
  modalMode.value = 'add'
  form.value = { username: '', password: '', role: 2, status: 1 }
  showModal.value = true
}

const openEditModal = (u) => {
  modalMode.value = 'edit'
  form.value = { ...u, password: '', role: u.roleValue }
  showModal.value = true
}

const handleSaveUser = async () => {
  if (modalMode.value === 'add') {
    if (!form.value.username || !form.value.password) {
      showNotify({ type: 'warning', message: '请填写用户名和密码' })
      return
    }
  }

  showLoadingToast({ message: '保存中...', forbidClick: true })
  try {
    if (modalMode.value === 'add') {
      await createUser({
        username: form.value.username,
        password: form.value.password
      })
      showNotify({ type: 'success', message: '用户创建成功' })
    } else {
      await updateUser(form.value.id, {
        username: form.value.username,
        password: form.value.password || undefined,
        role: form.value.role,
        status: form.value.status
      })
      showNotify({ type: 'success', message: '用户信息已更新' })
    }
    showModal.value = false
    await loadUsers()
  } catch (error) {
    console.error('保存失败', error)
  }
}

const handleDeleteUser = (id) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除该用户吗？此操作不可撤销。',
    theme: 'round-button'
  }).then(async () => {
    showLoadingToast({ message: '删除中...', forbidClick: true })
    try {
      await deleteUser(id)
      showNotify({ type: 'success', message: '用户已删除' })
      await loadUsers()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

const toggleStatus = async (u) => {
  const newStatus = u.status === '正常' ? 0 : 1
  showLoadingToast({ message: '更新中...', forbidClick: true })
  try {
    await updateUserStatus(u.id, { status: newStatus })
    u.status = newStatus === 1 ? '正常' : '禁用'
    u.statusClass = newStatus === 1 ? 'text-emerald-500 bg-emerald-500/10' : 'text-red-500 bg-red-500/10'
    showNotify({
      type: newStatus === 1 ? 'success' : 'warning',
      message: `用户已被${newStatus === 1 ? '启用' : '禁用'}`
    })
  } catch (error) {
    console.error('更新状态失败', error)
  }
}

const setPage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
}
</script>

<template>
  <div class="flex flex-col gap-8 pb-10 min-h-[80vh]">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">用户管理</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">查看用户信息、管理权限及违约记录</p>
      </div>
      <div class="flex items-center gap-4">
        <div class="relative group w-[300px]">
          <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 group-focus-within:text-[#5A52FF] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
          <input v-model="searchText" type="text" placeholder="搜索姓名/学号..." class="w-full bg-[#121624] border border-[#1C2136] text-white pl-11 pr-5 py-3 rounded-2xl text-sm focus:border-[#5A52FF]/50 outline-none transition-all font-bold tracking-tight">
        </div>
        <button @click="openAddModal" class="bg-[#5A52FF] hover:bg-[#4a42e5] text-white px-7 py-3 rounded-2xl flex items-center gap-3 text-sm font-black shadow-[0 4px 16px_rgba(90,82,255,0.2)] transition-all">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path></svg>
          添加用户
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="text-gray-500 font-bold">加载中...</div>
    </div>

    <!-- Empty State -->
    <div v-else-if="users.length === 0" class="flex flex-col items-center justify-center py-20 bg-[#121624] border border-dashed border-[#1C2136] rounded-[32px]">
      <div class="w-20 h-20 rounded-[2rem] bg-[#1A1F30] flex items-center justify-center mb-6 text-gray-600">
        <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
      </div>
      <p class="text-gray-500 font-bold tracking-widest uppercase text-xs">No active users found</p>
    </div>

    <!-- Table -->
    <div v-else class="bg-[#121624] border border-[#1C2136] rounded-[32px] overflow-hidden shadow-2xl relative">
      <table class="w-full text-left border-collapse">
        <thead class="bg-[#1A1F30]/50 border-b border-[#1C2136]">
          <tr>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] border-r border-white/5">学号</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] border-r border-white/5">累计预约</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] text-center border-r border-white/5">权限</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] text-center border-r border-white/5">状态</th>
            <th class="px-8 py-6 text-[11px] font-black text-gray-500 uppercase tracking-[0.2em] text-right">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#1C2136]">
          <tr v-for="u in paginatedData" :key="u.id" class="hover:bg-white/[0.03] transition-colors group">
            <td class="px-8 py-7 border-r border-white/5">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-2xl border-2 border-[#1C2136] bg-[#1A1F30] flex items-center justify-center text-[#5A52FF] text-[15px] font-black group-hover:border-[#5A52FF]/50 transition-colors shadow-sm">
                  {{ u.initial }}
                </div>
                <div>
                  <div class="text-[15px] font-black text-white leading-none tracking-wide">{{ u.username }}</div>
                </div>
              </div>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <span class="text-[16px] font-black text-white tracking-tighter">{{ u.count }}</span>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="flex justify-center">
                <div :class="u.role === '管理员' ? 'text-purple-500 bg-purple-500/10' : 'text-blue-500 bg-blue-500/10'" class="flex items-center gap-2.5 px-4 py-2 rounded-xl text-[10px] font-black tracking-widest uppercase ring-1 ring-inset ring-white/5">
                  {{ u.role }}
                </div>
              </div>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="flex justify-center">
                <div :class="u.statusClass" class="flex items-center gap-2.5 px-4 py-2 rounded-xl text-[10px] font-black tracking-widest uppercase ring-1 ring-inset ring-white/5">
                  <div class="w-1.5 h-1.5 rounded-full" :class="u.status === '正常' ? 'bg-emerald-500' : 'bg-red-500'"></div>
                  {{ u.status }}
                </div>
              </div>
            </td>
            <td class="px-8 py-7 text-right">
              <div class="flex items-center justify-end gap-3 translate-x-4 opacity-0 group-hover:opacity-100 group-hover:translate-x-0 transition-all duration-300">
                <button @click="openEditModal(u)" class="w-12 h-12 rounded-2xl bg-[#1A1F30] border border-[#2A314D] flex items-center justify-center text-gray-400 hover:text-white hover:border-[#5A52FF] hover:bg-[#5A52FF]/10 transition-all">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path></svg>
                </button>
                <button @click="toggleStatus(u)" :title="u.status === '正常' ? '禁用' : '启用'"
                  class="w-12 h-12 rounded-2xl bg-[#1A1F30] border border-[#2A314D] flex items-center justify-center transition-all"
                  :class="u.status === '正常' ? 'text-gray-400 hover:text-red-500 hover:border-red-500/50 hover:bg-red-500/5' : 'text-emerald-500/70 hover:text-emerald-500 hover:border-emerald-500/50 hover:bg-emerald-500/5'">
                  <svg v-if="u.status === '正常'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"></path></svg>
                  <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </button>
                <button @click="handleDeleteUser(u.id)" class="w-12 h-12 rounded-2xl bg-red-500/10 border border-red-500/20 flex items-center justify-center text-red-500 hover:bg-red-500 hover:text-white hover:border-red-500 transition-all">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
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

    <!-- Add/Edit User Modal -->
    <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div @click="showModal = false" class="absolute inset-0 bg-[#0A0D18]/80 backdrop-blur-md transition-opacity"></div>

      <div class="bg-[#121624] border border-[#1C2136] rounded-[40px] w-full max-w-xl shadow-2xl relative z-10 overflow-hidden animate-modal">
        <div class="px-10 pt-10 pb-8">
          <div class="flex items-center justify-between mb-10">
            <div>
              <h2 class="text-2xl font-black text-white tracking-wide">{{ modalMode === 'add' ? '添加用户' : '编辑用户信息' }}</h2>
              <p class="text-gray-550 text-xs font-bold uppercase tracking-widest mt-1 opacity-60">User Account Setup</p>
            </div>
            <button @click="showModal = false" class="w-12 h-12 rounded-full bg-[#1A1F30] text-gray-500 hover:text-white transition flex items-center justify-center border-none cursor-pointer group">
              <svg class="w-5 h-5 group-hover:rotate-90 transition-transform duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
            </button>
          </div>

          <div class="space-y-8">
            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">用户名</label>
              <input v-model="form.username" type="text" :placeholder="modalMode === 'add' ? '输入用户名' : '输入新用户名（留空不修改）'" class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] px-6 py-4 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none" />
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">密码</label>
              <input v-model="form.password" type="password" :placeholder="modalMode === 'add' ? '输入密码' : '输入新密码（留空不修改）'" class="bg-[#0A0D18] border-2 border-[#1C2136] rounded-[22px] px-6 py-4 text-[14px] font-bold text-white focus:border-[#5A52FF] transition-all outline-none" />
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">管理权限</label>
              <div class="flex gap-4">
                <button v-for="r in [{v:1,l:'管理员'}, {v:2,l:'普通用户'}]" :key="r.v" @click="form.role = r.v"
                  :class="form.role === r.v ? 'bg-[#5A52FF] text-white border-[#5A52FF]' : 'bg-[#0A0D18] text-gray-500 border-[#1C2136]'"
                  class="flex-1 py-4 rounded-2xl text-[13px] font-black transition-all border-2 cursor-pointer shadow-sm">
                  {{ r.l }}
                </button>
              </div>
            </div>

            <div class="flex flex-col gap-3">
              <label class="text-[11px] font-black text-[#5A52FF] tracking-[0.2em] uppercase ml-1">账户状态</label>
              <div class="flex gap-4">
                <button v-for="s in [{v:1,l:'正常'}, {v:0,l:'禁用'}]" :key="s.v" @click="form.status = s.v"
                  :class="form.status === s.v ? 'bg-[#5A52FF] text-white border-[#5A52FF]' : 'bg-[#0A0D18] text-gray-500 border-[#1C2136]'"
                  class="flex-1 py-4 rounded-2xl text-[13px] font-black transition-all border-2 cursor-pointer shadow-sm">
                  {{ s.l }}
                </button>
              </div>
            </div>
          </div>

          <div class="mt-12 flex gap-4">
            <button @click="showModal = false" class="flex-1 bg-[#1A1F30] text-gray-400 py-5 rounded-[22px] text-[15px] font-black hover:bg-[#20263d] hover:text-white transition-all border-none cursor-pointer">取消</button>
            <button @click="handleSaveUser" class="flex-1 bg-[#5A52FF] text-white py-5 rounded-[22px] text-[15px] font-black shadow-[0_8px_25px_rgba(90,82,255,0.3)] hover:bg-[#4a42e5] transition-all border-none cursor-pointer">
              {{ modalMode === 'add' ? '确认添加' : '保存修改' }}
            </button>
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
  from { opacity: 0; transform: scale(0.9) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
</style>

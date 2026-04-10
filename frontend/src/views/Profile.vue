<script setup>
import { ref, computed } from 'vue'
import Navbar from '../components/Navbar.vue'
import { showSuccessToast } from 'vant'

// User Profile State
const userProfile = ref({
  name: '张同学',
  email: 'zhang@university.edu.cn',
  phone: '13800138000',
  avatar: '张'
})

// UI State
const showEditModal = ref(false)
const showPasswordModal = ref(false)
const showNotificationModal = ref(false)
const expandedNoteId = ref(null)

const toggleNote = (id) => {
  expandedNoteId.value = expandedNoteId.value === id ? null : id
}

// Edit Form State
const editForm = ref({ ...userProfile.value })
const passwordForm = ref({
  old: '',
  new: '',
  confirm: ''
})

const saveProfile = () => {
  userProfile.value = { ...editForm.value }
  showEditModal.value = false
  showSuccessToast('个人资料已更新')
}

const updatePassword = () => {
  if (passwordForm.value.new !== passwordForm.value.confirm) {
    showSuccessToast('新密码两次输入不一致')
    return
  }
  showPasswordModal.value = false
  showSuccessToast('密码已成功修改')
  passwordForm.value = { old: '', new: '', confirm: '' }
}

// Notifications Mock Data
const notifications = ref([
  { id: 1, title: '预约成功提醒', content: '您已成功预约静雅自习室 - A区 A-08 座位，请准时签到。', time: '10分钟前', unread: true },
  { id: 2, title: '信用分变动', content: '由于您按时完成了上一次自习，信用分 +2。', time: '1小时前', unread: false },
  { id: 3, title: '系统维护公告', content: '自习室系统将于今日凌晨 2:00 - 4:00 进行例行维护。', time: '5小时前', unread: false },
])

// Mock History Records (same as in Reservations.vue for consistency)
const historyRecords = Array.from({ length: 25 }, (_, i) => {
  const isCompleted = i % 3 !== 0
  const date = i % 2 === 0 ? '2024-05-11' : '2024-05-10'
  const time = i % 2 === 0 ? '09:00 - 12:00' : '19:00 - 21:00'
  const duration = i % 2 === 0 ? 3 : 2
  
  return {
    id: 25 - i,
    date,
    duration,
    status: isCompleted ? 'completed' : 'cancelled'
  }
})

const completedRecords = computed(() => historyRecords.filter(r => r.status === 'completed'))

const totalHours = computed(() => completedRecords.value.reduce((acc, cur) => acc + cur.duration, 0))

const studyDays = computed(() => {
  const dates = new Set(completedRecords.value.map(r => r.date))
  return dates.size
})
</script>

<template>
  <div class="min-h-screen bg-[#f7f8fc] flex flex-col items-center pb-12">
    <!-- Navbar -->
    <Navbar />

    <main class="w-full px-8 md:px-12 lg:px-20 py-8 flex flex-col gap-6 max-w-[1200px]">
      <!-- Top Card -->
      <div class="w-full bg-white rounded-[2rem] p-8 md:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col md:flex-row items-center md:items-start gap-8 relative overflow-hidden">
        <!-- Background Decoration -->
        <div class="absolute -top-32 -right-32 w-80 h-80 bg-[#f0f4ff] rounded-full blur-[60px] opacity-70"></div>
        
        <!-- Avatar Section -->
        <div class="relative">
          <div class="w-[120px] h-[120px] rounded-[2rem] border-[4px] border-[#5A52FF] flex items-center justify-center bg-[#f8f9fc] shadow-lg shadow-indigo-100 overflow-hidden">
            <span class="text-5xl font-black text-[#5A52FF]">{{ userProfile.avatar }}</span>
          </div>
          <!-- Edit button -->
          <button @click="showEditModal = true; editForm = { ...userProfile }" class="absolute -bottom-2 -right-2 w-10 h-10 bg-white rounded-full shadow-[0_4px_12px_rgba(0,0,0,0.1)] border border-gray-100 flex items-center justify-center text-gray-400 hover:text-[#5A52FF] transition cursor-pointer z-10">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
          </button>
        </div>

        <!-- Info Section -->
        <div class="flex-1 flex flex-col items-center md:items-start z-10 w-full mt-2">
          <div class="flex items-center gap-3 mb-4">
            <h1 class="text-2xl font-black text-gray-900 tracking-wide">{{ userProfile.name }}</h1>
          </div>

          <!-- Stats -->
          <div class="flex flex-wrap gap-4 w-full">
            <!-- Stat 1 -->
            <div class="bg-[#f8f9fc] rounded-[1rem] px-5 py-4 flex items-center gap-4 flex-1 min-w-[140px]">
              <div class="w-10 h-10 rounded-full bg-[#efefff] text-[#5A52FF] flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-[11px] text-gray-400 font-bold mb-0.5">累计专注</span>
                <span class="text-lg font-black text-gray-900">{{ totalHours }}<span class="text-xs ml-0.5">h</span></span>
              </div>
            </div>
            <!-- Stat 2 -->
            <div class="bg-[#f8f9fc] rounded-[1rem] px-5 py-4 flex items-center gap-4 flex-1 min-w-[140px]">
              <div class="w-10 h-10 rounded-full bg-[#ecfdf3] text-[#10b981] flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-[11px] text-gray-400 font-bold mb-0.5">学习天数</span>
                <span class="text-lg font-black text-gray-900">{{ studyDays }}<span class="text-xs ml-0.5">天</span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Settings Grids -->
      <div class="flex flex-col md:flex-row gap-6 w-full mt-2 z-10">
        
        <!-- Account Settings -->
        <div class="flex-1 bg-white rounded-[2rem] p-8 md:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col">
          <h2 class="text-lg font-black text-gray-900 mb-6 tracking-wide">账户设置</h2>
          <div class="flex flex-col space-y-2">
            
            <button @click="showEditModal = true; editForm = { ...userProfile }" class="flex items-center justify-between py-4 group cursor-pointer border-none bg-transparent border-b border-transparent hover:border-gray-50 text-left w-full">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-xl bg-blue-50 text-blue-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
                </div>
                <span class="text-[14px] font-bold text-gray-700 group-hover:text-gray-900">个人资料修改</span>
              </div>
              <svg class="w-4 h-4 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </button>

            <button @click="showPasswordModal = true" class="flex items-center justify-between py-4 group cursor-pointer border-none bg-transparent border-b border-transparent hover:border-gray-50 text-left w-full">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
                </div>
                <span class="text-[14px] font-bold text-gray-700 group-hover:text-gray-900">修改登录密码</span>
              </div>
              <svg class="w-4 h-4 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </button>

            <button @click="showNotificationModal = true" class="flex items-center justify-between py-4 group cursor-pointer border-none bg-transparent border-b border-transparent hover:border-gray-50 text-left w-full">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-xl bg-amber-50 text-amber-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                </div>
                <span class="text-[14px] font-bold text-gray-700 group-hover:text-gray-900">系统通知管理</span>
              </div>
              <svg class="w-4 h-4 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </button>

          </div>
        </div>

        <!-- More Support -->
        <div class="flex-1 bg-white rounded-[2rem] p-8 md:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col">
          <h2 class="text-lg font-black text-gray-900 mb-6 tracking-wide">更多支持</h2>
          <div class="flex flex-col space-y-2">
            
            <a @click="$router.push('/guide')" class="flex items-center justify-between py-4 group cursor-pointer border-b border-transparent hover:border-gray-50">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </div>
                <span class="text-[14px] font-bold text-gray-700 group-hover:text-gray-900">使用指南 & FAQ</span>
              </div>
              <svg class="w-4 h-4 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </a>

            <a @click="$router.push('/about')" class="flex items-center justify-between py-4 group cursor-pointer border-b border-transparent hover:border-gray-50">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-xl bg-purple-50 text-purple-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
                </div>
                <span class="text-[14px] font-bold text-gray-700 group-hover:text-gray-900">关于静学自习室</span>
              </div>
              <svg class="w-4 h-4 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </a>

            <a @click="$router.push('/login')" class="flex items-center justify-between py-4 group cursor-pointer border-b border-transparent hover:border-gray-50">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-xl bg-orange-50 text-orange-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path></svg>
                </div>
                <span class="text-[14px] font-bold text-gray-700 group-hover:text-gray-900">退出登录</span>
              </div>
              <svg class="w-4 h-4 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </a>

          </div>
        </div>
      </div>
    </main>


  </div>

  <!-- Edit Profile Modal -->
  <div v-if="showEditModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6">
    <div @click="showEditModal = false" class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm transition-opacity"></div>
    <div class="bg-white rounded-[2.5rem] w-full max-w-lg shadow-2xl relative z-10 overflow-hidden animate-in fade-in zoom-in-95 duration-300">
      <div class="px-8 pt-10 pb-6">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-2xl font-black text-gray-900 tracking-wide">修改个人资料</h2>
          <button @click="showEditModal = false" class="w-10 h-10 rounded-full bg-gray-50 text-gray-400 hover:text-gray-600 transition flex items-center justify-center border-none cursor-pointer">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>

        <div class="space-y-6">
          <div class="flex flex-col gap-2">
            <label class="text-[13px] font-black text-gray-400 tracking-wide ml-1">头像文字 (首字母)</label>
            <input v-model="editForm.avatar" type="text" maxlength="1" class="bg-gray-50 border-2 border-gray-100 rounded-2xl px-6 py-4 text-[14px] font-bold text-gray-900 focus:border-[#5A52FF] focus:bg-white transition-all outline-none" />
          </div>
          <div class="flex flex-col gap-2">
            <label class="text-[13px] font-black text-gray-400 tracking-wide ml-1">真实姓名</label>
            <input v-model="editForm.name" type="text" class="bg-gray-50 border-2 border-gray-100 rounded-2xl px-6 py-4 text-[14px] font-bold text-gray-900 focus:border-[#5A52FF] focus:bg-white transition-all outline-none" />
          </div>
          <div class="flex flex-col gap-2">
            <label class="text-[13px] font-black text-gray-400 tracking-wide ml-1">绑定邮箱</label>
            <input v-model="editForm.email" type="email" class="bg-gray-50 border-2 border-gray-100 rounded-2xl px-6 py-4 text-[14px] font-bold text-gray-900 focus:border-[#5A52FF] focus:bg-white transition-all outline-none" />
          </div>
          <div class="flex flex-col gap-2">
            <label class="text-[13px] font-black text-gray-400 tracking-wide ml-1">手机号码</label>
            <input v-model="editForm.phone" type="tel" class="bg-gray-50 border-2 border-gray-100 rounded-2xl px-6 py-4 text-[14px] font-bold text-gray-900 focus:border-[#5A52FF] focus:bg-white transition-all outline-none" />
          </div>
        </div>

        <div class="mt-10 flex gap-4">
          <button @click="showEditModal = false" class="flex-1 bg-gray-50 text-gray-900 py-4 rounded-2xl text-[14px] font-black hover:bg-gray-100 transition border-none cursor-pointer" style="color: black !important;">取消退出</button>
          <button @click="saveProfile" class="flex-1 bg-[#5A52FF] text-white py-4 rounded-2xl text-[14px] font-black shadow-lg shadow-indigo-200 hover:bg-[#4a42e5] transition border-none cursor-pointer" style="color: white !important;">保存修改</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Notifications Modal -->
  <div v-if="showNotificationModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6">
    <div @click="showNotificationModal = false" class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm transition-opacity"></div>
    <div class="bg-white rounded-[2.5rem] w-full max-w-lg shadow-2xl relative z-10 overflow-hidden animate-in fade-in zoom-in-95 duration-300 flex flex-col max-h-[80vh]">
      <div class="px-8 pt-10 pb-6 border-b border-gray-50 flex items-center justify-between">
        <h2 class="text-2xl font-black text-gray-900 tracking-wide">系统通知</h2>
        <button @click="showNotificationModal = false" class="w-10 h-10 rounded-full bg-gray-50 text-gray-400 hover:text-gray-600 transition flex items-center justify-center border-none cursor-pointer">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
        </button>
      </div>
      
      <div class="flex-1 overflow-y-auto px-8 py-6 space-y-4">
        <div v-for="note in notifications" :key="note.id" @click="toggleNote(note.id)" class="p-6 rounded-3xl border border-gray-50 relative group transition-all cursor-pointer" :class="note.unread ? 'bg-indigo-50/50 border-indigo-100' : 'bg-white hover:bg-gray-50'">
          <div class="flex flex-col gap-2">
            <div class="flex items-center justify-between">
              <span class="text-[15px] font-black text-gray-900">{{ note.title }}</span>
              <div class="flex items-center gap-2">
                <span class="text-[11px] font-bold text-gray-400">{{ note.time }}</span>
                <svg :class="expandedNoteId === note.id ? 'rotate-180' : ''" class="w-4 h-4 text-gray-300 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" stroke-width="2.5"></path></svg>
              </div>
            </div>
            <p v-if="expandedNoteId === note.id" class="text-[13px] font-medium text-gray-500 leading-relaxed mt-1 animate-in slide-in-from-top-2 duration-300">
              {{ note.content }}
            </p>
            <p v-else class="text-[13px] font-medium text-gray-400 leading-relaxed truncate">
              {{ note.content }}
            </p>
          </div>
        </div>
      </div>

      <div class="p-8 border-t border-gray-50">
        <button @click="showNotificationModal = false" class="w-full bg-[#5A52FF] text-white py-4 rounded-2xl text-[14px] font-black shadow-lg shadow-indigo-200 hover:bg-[#4a42e5] transition border-none cursor-pointer" style="color: white !important;">
          我知道了
        </button>
      </div>
    </div>
  </div>

  <!-- Password Modal -->
  <div v-if="showPasswordModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6">
    <div @click="showPasswordModal = false" class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm transition-opacity"></div>
    <div class="bg-white rounded-[2.5rem] w-full max-w-lg shadow-2xl relative z-10 overflow-hidden animate-in fade-in zoom-in-95 duration-300">
      <div class="px-8 pt-10 pb-6">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-2xl font-black text-gray-900 tracking-wide">修改登录密码</h2>
          <button @click="showPasswordModal = false" class="w-10 h-10 rounded-full bg-gray-50 text-gray-400 hover:text-gray-600 transition flex items-center justify-center border-none cursor-pointer">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>

        <div class="space-y-6">
          <div class="flex flex-col gap-2">
            <label class="text-[13px] font-black text-gray-400 tracking-wide ml-1">当前旧密码</label>
            <input v-model="passwordForm.old" type="password" placeholder="请输入旧密码" class="bg-gray-50 border-2 border-gray-100 rounded-2xl px-6 py-4 text-[14px] font-bold text-gray-900 focus:border-[#5A52FF] focus:bg-white transition-all outline-none" />
          </div>
          <div class="flex flex-col gap-2">
            <label class="text-[13px] font-black text-gray-400 tracking-wide ml-1">设置新密码</label>
            <input v-model="passwordForm.new" type="password" placeholder="请输入新密码" class="bg-gray-50 border-2 border-gray-100 rounded-2xl px-6 py-4 text-[14px] font-bold text-gray-900 focus:border-[#5A52FF] focus:bg-white transition-all outline-none" />
          </div>
          <div class="flex flex-col gap-2">
            <label class="text-[13px] font-black text-gray-400 tracking-wide ml-1">确认新密码</label>
            <input v-model="passwordForm.confirm" type="password" placeholder="请确认新密码" class="bg-gray-50 border-2 border-gray-100 rounded-2xl px-6 py-4 text-[14px] font-bold text-gray-900 focus:border-[#5A52FF] focus:bg-white transition-all outline-none" />
          </div>
        </div>

        <div class="mt-10 flex gap-4">
          <button @click="showPasswordModal = false" class="flex-1 bg-gray-50 text-gray-900 py-4 rounded-2xl text-[14px] font-black hover:bg-gray-100 transition border-none cursor-pointer" style="color: black !important;">取消</button>
          <button @click="updatePassword" class="flex-1 bg-[#5A52FF] text-white py-4 rounded-2xl text-[14px] font-black shadow-lg shadow-indigo-200 hover:bg-[#4a42e5] transition border-none cursor-pointer" style="color: white !important;">确认修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

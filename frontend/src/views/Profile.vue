<script setup>
import { ref, computed, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import { showSuccessToast, showToast } from 'vant'
import { getUserProfile, updateUserProfile, getCreditScore } from '../api/user'
import { getMyReservations } from '../api/reservations'

// User Profile State
const userProfile = ref({
  username: '',
  phone: '',
  avatar: '',
  creditScore: 100
})

// 预约记录
const reservations = ref([])

// 加载预约数据
const loadReservations = async () => {
  try {
    const data = await getMyReservations({ page: 1, size: 100 })
    reservations.value = data.records || []
  } catch (error) {
    console.error('加载预约失败', error)
  }
}

// 加载用户信息
const loadProfile = async () => {
  try {
    const data = await getUserProfile()
    userProfile.value = {
      username: data.username || '',
      phone: data.phone || '',
      avatar: data.avatar || (data.username || 'U').charAt(0).toUpperCase()
    }
    // 获取信誉分
    try {
      const score = await getCreditScore()
      userProfile.value.creditScore = score || 100
    } catch (e) {
      console.error('获取信誉分失败', e)
    }
  } catch (error) {
    console.error('加载用户信息失败', error)
  }
}

onMounted(() => {
  loadProfile()
  loadReservations()
})

// UI State
const showEditModal = ref(false)
const showPasswordModal = ref(false)

// Edit Form State
const editForm = ref({
  username: '',
  avatar: '',
  phone: ''
})
const passwordForm = ref({
  old: '',
  new: '',
  confirm: ''
})

const saveProfile = async () => {
  try {
    await updateUserProfile({
      username: editForm.value.username,
      phone: editForm.value.phone
    })
    userProfile.value = { ...editForm.value }
    userProfile.value.avatar = (editForm.value.username || 'U').charAt(0).toUpperCase()
    showEditModal.value = false
    showSuccessToast('个人资料已更新')
  } catch (error) {
    console.error('保存失败', error)
  }
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

// 历史记录 (已完成的预约)
const completedRecords = computed(() => {
  return reservations.value.filter(r => r.status === 3)
})

// 累计专注时间（小时）
const totalHours = computed(() => {
  let totalMinutes = 0
  completedRecords.value.forEach(r => {
    if (r.startTime && r.endTime) {
      // 解析 HH:mm 格式的时间
      const [startH, startM] = r.startTime.split(':').map(Number)
      const [endH, endM] = r.endTime.split(':').map(Number)
      totalMinutes += (endH * 60 + endM) - (startH * 60 + startM)
    }
  })
  return Math.round(totalMinutes / 60 * 10) / 10 // 保留一位小数
})

const studyDays = computed(() => {
  const dates = new Set()
  completedRecords.value.forEach(r => {
    if (r.checkInTime) {
      dates.add(r.checkInTime.split(' ')[0]) // 取日期部分
    } else if (r.startTime) {
      dates.add(r.startTime.split(' ')[0])
    }
  })
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
          <div class="w-[140px] h-[140px] rounded-[2rem] border-[4px] border-[#5A52FF] flex items-center justify-center bg-[#f8f9fc] shadow-lg shadow-indigo-100 overflow-hidden">
            <span class="text-6xl font-black text-[#5A52FF]">{{ userProfile.avatar }}</span>
          </div>
          <!-- Edit button -->
          <button @click="showEditModal = true; editForm = { username: userProfile.username, phone: userProfile.phone }" class="absolute -bottom-2 -right-2 w-12 h-12 bg-white rounded-full shadow-[0_4px_12px_rgba(0,0,0,0.1)] border border-gray-100 flex items-center justify-center text-gray-400 hover:text-[#5A52FF] transition cursor-pointer z-10">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
          </button>
        </div>

        <!-- Info Section -->
        <div class="flex-1 flex flex-col items-center md:items-start z-10 w-full mt-2">
          <div class="flex items-center gap-3 mb-4">
            <h1 class="text-2xl font-black text-gray-900 tracking-wide">{{ userProfile.username }}</h1>
          </div>

          <!-- Stats -->
          <div class="flex flex-wrap gap-4 w-full">
            <!-- Stat 1 -->
            <div class="bg-[#f8f9fc] rounded-[1rem] px-6 py-5 flex items-center gap-4 flex-1 min-w-[160px]">
              <div class="w-12 h-12 rounded-full bg-[#efefff] text-[#5A52FF] flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-[13px] text-gray-400 font-bold mb-0.5">累计专注</span>
                <span class="text-xl font-black text-gray-900">{{ totalHours || 0 }}<span class="text-xs ml-0.5">h</span></span>
              </div>
            </div>
            <!-- Stat 2 -->
            <div class="bg-[#f8f9fc] rounded-[1rem] px-6 py-5 flex items-center gap-4 flex-1 min-w-[160px]">
              <div class="w-12 h-12 rounded-full bg-[#ecfdf3] text-[#10b981] flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-[13px] text-gray-400 font-bold mb-0.5">学习天数</span>
                <span class="text-xl font-black text-gray-900">{{ studyDays }}<span class="text-xs ml-0.5">天</span></span>
              </div>
            </div>
            <!-- Stat 3 -->
            <div class="bg-[#f8f9fc] rounded-[1rem] px-6 py-5 flex items-center gap-4 flex-1 min-w-[160px]">
              <div class="w-12 h-12 rounded-full bg-[#fef3c7] text-[#d97706] flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.02z"></path></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-[13px] text-gray-400 font-bold mb-0.5">信誉分</span>
                <span class="text-xl font-black text-gray-900">{{ userProfile.creditScore }}<span class="text-xs ml-0.5">分</span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Settings Grids -->
      <div class="flex flex-col md:flex-row gap-6 w-full mt-2 z-10">
        
        <!-- Account Settings -->
        <div class="flex-1 bg-white rounded-[2rem] p-8 md:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col">
          <h2 class="text-xl font-black text-gray-900 mb-6 tracking-wide">账户设置</h2>
          <div class="flex flex-col space-y-2">
            
            <button @click="showEditModal = true; editForm = { username: userProfile.username, phone: userProfile.phone }" class="flex items-center justify-between py-5 group cursor-pointer border-none bg-transparent border-b border-transparent hover:border-gray-50 text-left w-full">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-xl bg-blue-50 text-blue-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
                </div>
                <span class="text-[16px] font-bold text-gray-700 group-hover:text-gray-900">个人资料修改</span>
              </div>
              <svg class="w-5 h-5 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </button>

            <button @click="showPasswordModal = true" class="flex items-center justify-between py-5 group cursor-pointer border-none bg-transparent border-b border-transparent hover:border-gray-50 text-left w-full">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
                </div>
                <span class="text-[16px] font-bold text-gray-700 group-hover:text-gray-900">修改登录密码</span>
              </div>
              <svg class="w-5 h-5 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </button>

          </div>
        </div>

        <!-- More Support -->
        <div class="flex-1 bg-white rounded-[2rem] p-8 md:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col">
          <h2 class="text-xl font-black text-gray-900 mb-6 tracking-wide">更多支持</h2>
          <div class="flex flex-col space-y-2">
            
            <a @click="$router.push('/guide')" class="flex items-center justify-between py-5 group cursor-pointer border-b border-transparent hover:border-gray-50">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-xl bg-indigo-50 text-indigo-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </div>
                <span class="text-[16px] font-bold text-gray-700 group-hover:text-gray-900">使用指南 & FAQ</span>
              </div>
              <svg class="w-5 h-5 text-gray-300 group-hover:text-gray-500 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"></path></svg>
            </a>

            <a @click="$router.push('/about')" class="flex items-center justify-between py-5 group cursor-pointer border-b border-transparent hover:border-gray-50">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-xl bg-purple-50 text-purple-500 flex items-center justify-center transition-transform group-hover:scale-110">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
                </div>
                <span class="text-[16px] font-bold text-gray-700 group-hover:text-gray-900">关于静学自习室</span>
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
            <label class="text-[13px] font-black text-gray-400 tracking-wide ml-1">用户名</label>
            <input v-model="editForm.username" type="text" class="bg-gray-50 border-2 border-gray-100 rounded-2xl px-6 py-4 text-[14px] font-bold text-gray-900 focus:border-[#5A52FF] focus:bg-white transition-all outline-none" />
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

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const account = ref('')
const password = ref('')
const confirmPassword = ref('')

const handleRegister = () => {
  console.log('Register attempt', { account: account.value, password: password.value })
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-[#f0f2fb] via-white to-[#f4f7fc] flex items-center justify-center p-6 lg:p-12 relative overflow-hidden">
    <div class="absolute -top-20 -left-20 w-96 h-96 bg-indigo-100/50 rounded-full blur-[80px]"></div>
    <div class="absolute -bottom-32 -right-32 w-[30rem] h-[30rem] bg-indigo-50/70 rounded-full blur-[100px]"></div>

    <div class="w-full max-w-[28rem] bg-white rounded-[2.5rem] px-8 py-10 sm:px-12 sm:py-14 shadow-[0_20px_50px_rgba(0,0,0,0.04)] relative z-10 flex flex-col items-center animate-in fade-in zoom-in duration-500">
      
      <div class="w-20 h-20 bg-[#5A52FF] rounded-3xl flex items-center justify-center shadow-xl shadow-[#5A52FF]/30 mb-7 transform hover:scale-105 transition duration-300">
        <svg class="w-9 h-9 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2">
          <rect x="3" y="4" width="18" height="18" rx="2.5" ry="2.5"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
        </svg>
      </div>

      <h1 class="text-[2rem] font-black text-gray-900 mb-2.5 tracking-wide text-center">注册新账号</h1>
      <p class="text-[14px] text-gray-400 font-medium mb-10 text-center tracking-wide">加入静学自习室，开启专注时刻</p>

      <form @submit.prevent="handleRegister" class="w-full flex-col flex gap-5">
        
        <div class="flex flex-col gap-2.5">
          <label class="text-[13px] font-bold text-gray-700 select-none">学号 / 手机号</label>
          <div class="relative flex items-center group">
            <div class="absolute left-4.5 text-gray-400 transition-colors group-focus-within:text-[#5A52FF] z-10">
              <svg class="w-5 h-5 ml-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
            </div>
            <input v-model="account" type="text" placeholder="设置您的账号" class="w-full bg-[#f8f9fc] border-2 border-transparent focus:bg-white focus:border-[#5A52FF]/30 transition-all rounded-[1rem] py-4 pl-[3.5rem] pr-5 text-[14px] outline-none placeholder-gray-400 text-gray-900 font-semibold" required />
          </div>
        </div>

        <div class="flex flex-col gap-2.5 mt-2">
          <label class="text-[13px] font-bold text-gray-700 select-none">登录密码</label>
          <div class="relative flex items-center group">
            <div class="absolute left-4.5 text-gray-400 transition-colors group-focus-within:text-[#5A52FF] z-10">
              <svg class="w-5 h-5 ml-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
            </div>
            <input v-model="password" type="password" placeholder="设置密码" class="w-full bg-[#f8f9fc] border-2 border-transparent focus:bg-white focus:border-[#5A52FF]/30 transition-all rounded-[1rem] py-4 pl-[3.5rem] pr-5 text-[14px] outline-none placeholder-gray-400 text-gray-900 font-semibold" required />
          </div>
        </div>

        <div class="flex flex-col gap-2.5 mt-2">
          <label class="text-[13px] font-bold text-gray-700 select-none">确认密码</label>
          <div class="relative flex items-center group">
            <div class="absolute left-4.5 text-gray-400 transition-colors group-focus-within:text-[#5A52FF] z-10">
              <svg class="w-5 h-5 ml-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
            </div>
            <input v-model="confirmPassword" type="password" placeholder="再次输入密码" class="w-full bg-[#f8f9fc] border-2 border-transparent focus:bg-white focus:border-[#5A52FF]/30 transition-all rounded-[1rem] py-4 pl-[3.5rem] pr-5 text-[14px] outline-none placeholder-gray-400 text-gray-900 font-semibold" required />
          </div>
        </div>

        <div class="mt-6 flex flex-col gap-3.5 w-full">
          <button type="submit" class="w-full bg-[#5A52FF] text-white rounded-[1rem] py-4 text-[15px] font-bold shadow-[0_6px_20px_rgba(90,82,255,0.3)] hover:bg-[#4a42e5] hover:-translate-y-0.5 hover:shadow-[0_8px_25px_rgba(90,82,255,0.4)] transition-all flex items-center justify-center gap-2 group border-none cursor-pointer" style="color: white !important;">
            立即注册 
            <svg class="w-[18px] h-[18px] transition-transform group-hover:translate-x-1" style="color: white !important;" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
          </button>
          
          <button type="button" @click="$router.push('/login')" class="w-full bg-white text-[#5A52FF] border-2 border-[#5A52FF]/10 rounded-[1rem] py-[14px] text-[14px] font-bold hover:bg-[#F7F8FA] hover:border-[#5A52FF]/20 transition-all focus:outline-none focus:ring-4 focus:ring-[#5A52FF]/10 active:bg-gray-50">
            已有账号？返回登录
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

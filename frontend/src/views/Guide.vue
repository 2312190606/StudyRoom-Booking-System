<script setup>
import { ref } from 'vue'
import Navbar from '../components/Navbar.vue'

const activeNames = ref(['1'])

const faqs = [
  {
    id: '1',
    title: '如何进行在线预约？',
    content: '在首页找到您心仪的自习室，点击进入座位图视图。选择绿色的可用座位，设定预约起止时间段，点击“确认预约”即可。系统将为您保留座位。'
  },
  {
    id: '2',
    title: '签到与退座流程是怎样的？',
    content: '到达自习室后，请在预约时间开始后的15分钟内，通过系统“我的预约”界面点击对应记录进行签到。结束学习后，点击“结束自习”释放座位，方便他人使用。'
  },
  {
    id: '3',
    title: '预约可以提前多久取消？',
    content: '如果您因故无法前往，请务必在预约起始时间前30分钟取消。超时未签到将计入“迟到”状态，多次违约将影响您的个人信用分。'
  },
  {
    id: '4',
    title: '信用分有什么作用？',
    content: '初始信用分为100。按时履约加2分，违约/迟到扣除5-10分。当信用分低于60分时，将进入黑名单，暂缓预约权限7天。'
  },
  {
    id: '5',
    title: '自习室内可以进行讨论吗？',
    content: '静学自习室旨在提供极度安静的学习环境。除专门划出的“研讨区”外，所有区域禁止大声喧哗、外放音频及高分贝交流。'
  }
]

const rules = [
  { title: '一人一座', icon: 'User', desc: '系统实行实名制预约，禁止代替占座或非法倒卖资源。' },
  { title: '准时签到', icon: 'Clock', desc: '迟到超过15分钟且未操作，预约将自动释放并计入违约。' },
  { title: '文明自习', icon: 'Info', desc: '请保持周围环境整洁，离开时请随手带走垃圾。' }
]
</script>

<template>
  <div class="min-h-screen bg-[#f7f8fc] flex flex-col items-center pb-12">
    <Navbar />

    <main class="w-full px-8 md:px-12 lg:px-20 py-8 flex flex-col gap-6 max-w-[1200px]">
      <!-- Header Section -->
      <div class="w-full bg-white rounded-[2rem] p-8 md:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col relative overflow-hidden">
        <div class="absolute -top-32 -right-32 w-80 h-80 bg-[#f0f4ff] rounded-full blur-[60px] opacity-70"></div>
        <div class="z-10">
          <div class="flex items-center gap-4 mb-4">
            <button @click="$router.back()" class="w-10 h-10 rounded-full bg-indigo-50 text-[#5A52FF] flex items-center justify-center hover:bg-indigo-100 transition border-none cursor-pointer">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"></path></svg>
            </button>
            <h1 class="text-3xl font-black text-gray-900 tracking-tight">使用指南 & FAQ</h1>
          </div>
          <p class="text-gray-400 font-bold ml-14">帮助您更好地享受静学带来的专注体验</p>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- System Rules Card -->
        <div class="md:col-span-1 bg-white rounded-[2rem] p-8 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col">
          <h2 class="text-xl font-black text-gray-900 mb-8 flex items-center gap-2">
            <span class="w-2 h-6 bg-[#5A52FF] rounded-full"></span>
            核心原则
          </h2>
          <div class="space-y-8">
            <div v-for="rule in rules" :key="rule.title" class="flex gap-4">
              <div class="w-10 h-10 rounded-xl bg-indigo-50 text-[#5A52FF] flex-shrink-0 flex items-center justify-center">
                <svg v-if="rule.icon === 'User'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
                <svg v-if="rule.icon === 'Clock'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                <svg v-if="rule.icon === 'Info'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-[15px] font-black text-gray-900 mb-1">{{ rule.title }}</span>
                <p class="text-[13px] font-bold text-gray-400 leading-relaxed">{{ rule.desc }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- FAQ Section -->
        <div class="md:col-span-2 bg-white rounded-[2rem] p-8 md:p-10 shadow-[0_10px_40px_rgba(0,0,0,0.03)] flex flex-col">
          <h2 class="text-xl font-black text-gray-900 mb-8 flex items-center gap-2">
            <span class="w-2 h-6 bg-[#5A52FF] rounded-full"></span>
            常见问题 (FAQ)
          </h2>
          
          <div class="space-y-4">
            <van-collapse v-model="activeNames" accordion border="false">
              <van-collapse-item v-for="faq in faqs" :key="faq.id" :name="faq.id" class="!border-none">
                <template #title>
                  <span class="text-[15px] font-black text-gray-800">{{ faq.title }}</span>
                </template>
                <div class="text-[14px] font-medium text-gray-500 leading-relaxed py-2">
                  {{ faq.content }}
                </div>
              </van-collapse-item>
            </van-collapse>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style>
/* Custom Vant Collapse styling to match the design */
.van-collapse-item {
  margin-bottom: 0.5rem;
  border-radius: 1.5rem !important;
  overflow: hidden;
  border: 1px solid #f8f9fc !important;
}
.van-collapse-item__title {
  padding: 1.5rem !important;
  background-color: #f8f9fc !important;
  transition: all 0.3s ease !important;
}
.van-collapse-item--expanded .van-collapse-item__title {
  background-color: white !important;
}
.van-collapse-item__content {
  padding: 1.5rem !important;
  background-color: white !important;
}
.van-cell::after {
  display: none !important;
}
</style>

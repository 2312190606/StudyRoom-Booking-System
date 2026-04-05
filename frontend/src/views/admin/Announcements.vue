<script setup>
import { ref } from 'vue'

const announcements = [
  {
    id: 1,
    title: '关于图书馆 A 区维护的通知',
    date: '2024-03-11',
    content: '图书馆 A 区自习室本周六由于设备检修暂停开放，请同学们前往 B 区或 C 区自习。',
    reads: 256,
    status: '发布中',
    statusClass: 'text-emerald-500 bg-emerald-500/10'
  },
  {
    id: 2,
    title: '期末周预约时限调整',
    date: '2024-03-08',
    content: '期末周将至，为了提高座位周转率，自习室预约时限调整为 4 小时。',
    reads: 128,
    status: '已下架',
    statusClass: 'text-gray-500 bg-gray-500/10'
  },
  {
    id: 3,
    title: '严厉打击占座行为的公告',
    date: '2024-03-05',
    content: '严禁任何形式的占座行为，离开超过 30 分钟系统将自动释放座位。',
    reads: 432,
    status: '发布中',
    statusClass: 'text-emerald-500 bg-emerald-500/10'
  }
]
</script>

<template>
  <div class="flex flex-col gap-8 pb-10">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">公告管理</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">发布系统公告、维护通知及规则说明</p>
      </div>
      <div class="flex items-center gap-4">
        <div class="relative group w-[300px]">
          <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 group-focus-within:text-[#5A52FF] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
          <input type="text" placeholder="搜索标题..." class="w-full bg-[#121624] border border-[#1C2136] text-white pl-11 pr-5 py-3 rounded-xl text-sm focus:border-[#5A52FF]/50 outline-none transition-all font-bold tracking-tight">
        </div>
        <button class="bg-[#5A52FF] hover:bg-[#4a42e5] text-white px-6 py-2.5 rounded-xl flex items-center gap-2 text-sm font-black shadow-[0_4px_12px_rgba(90,82,255,0.3)] transition-all">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path></svg>
          发布新公告
        </button>
      </div>
    </div>

    <div class="flex flex-col gap-6">
      <div v-for="anno in announcements" :key="anno.id" class="bg-[#121624] border border-[#1C2136] rounded-[24px] p-8 flex flex-col md:flex-row gap-6 hover:border-[#303753] transition-all group overflow-hidden relative">
        <div class="absolute -right-16 -top-16 w-48 h-48 rounded-full blur-[80px] bg-[#5A52FF]/5 opacity-0 group-hover:opacity-100 transition-opacity"></div>
        
        <div class="flex-1 flex flex-col justify-between">
          <div>
            <div class="flex items-center gap-4 mb-4">
              <span :class="anno.statusClass" class="px-3 py-1.5 rounded-lg text-[10px] font-black tracking-widest uppercase">{{ anno.status }}</span>
              <div class="flex items-center gap-1.5 text-gray-550 text-[11px] font-bold opacity-60">
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
                {{ anno.date }}
              </div>
            </div>
            
            <h3 class="text-xl font-black text-white mb-3 tracking-wide group-hover:text-[#5A52FF] transition-colors">{{ anno.title }}</h3>
            <p class="text-[14px] font-bold text-gray-500 leading-relaxed max-w-[80%]">{{ anno.content }}</p>
          </div>
        </div>

        <div class="flex flex-row md:flex-col items-center justify-between md:justify-center gap-6 border-l md:pl-10 border-[#1C2136] order-last md:order-none">
          <div class="flex flex-col items-center">
            <span class="text-[11px] font-bold text-gray-550 uppercase tracking-widest opacity-60 mb-1">阅读量</span>
            <span class="text-xl font-black text-white tracking-tighter">{{ anno.reads }}</span>
          </div>
          
          <div class="flex gap-3">
             <button class="w-10 h-10 rounded-xl bg-[#1A1F30] border border-[#2A314D] flex items-center justify-center text-gray-400 hover:text-white hover:border-white/30 transition-all">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
             </button>
             <button class="w-10 h-10 rounded-xl bg-red-500/10 border border-red-500/20 flex items-center justify-center text-red-500 hover:bg-red-500 hover:text-white transition-all">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
             </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const rooms = [
  {
    id: 1,
    name: '静雅自习室 - A区',
    location: '图书馆 3 楼',
    totalSeats: 60,
    availableSeats: 12,
    rating: 4.9,
    status: '开放中',
    statusClass: 'text-emerald-500 bg-emerald-500/10',
    image: 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&q=80&w=800'
  },
  {
    id: 2,
    name: '晨曦自习室 - B区',
    location: '教学楼 C 座',
    totalSeats: 45,
    availableSeats: 5,
    rating: 4.7,
    status: '维护中',
    statusClass: 'text-amber-500 bg-amber-500/10',
    image: 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=800'
  },
  {
    id: 3,
    name: '极客自习室 - 24h',
    location: '实训楼 1 楼',
    totalSeats: 30,
    availableSeats: 28,
    rating: 4.8,
    status: '开放中',
    statusClass: 'text-emerald-500 bg-emerald-500/10',
    image: 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&q=80&w=800'
  }
]
</script>

<template>
  <div class="flex flex-col gap-8 pb-10">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">自习室管理</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">配置自习室空间布局、开放时段及座位资源</p>
      </div>
      <div class="flex items-center gap-4">
        <div class="relative group">
          <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 group-focus-within:text-[#5A52FF] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
          <input type="text" placeholder="搜索自习室..." class="bg-[#121624] border border-[#1C2136] text-white pl-11 pr-5 py-2.5 rounded-xl text-sm focus:border-[#5A52FF]/50 outline-none transition-all w-64 font-bold">
        </div>
        <button class="bg-[#5A52FF] hover:bg-[#4a42e5] text-white px-6 py-2.5 rounded-xl flex items-center gap-2 text-sm font-black shadow-[0_4px_12px_rgba(90,82,255,0.3)] transition-all active:scale-95">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path></svg>
          新增自习室
        </button>
      </div>
    </div>

    <div class="grid grid-cols-1 gap-6">
      <div v-for="room in rooms" :key="room.id" class="bg-[#121624] border border-[#1C2136] rounded-[24px] overflow-hidden flex flex-col md:flex-row group hover:border-[#303753] transition-all">
        <div class="md:w-1/3 h-64 md:h-auto relative">
          <img :src="room.image" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" alt="Room image">
          <div class="absolute inset-0 bg-gradient-to-r from-[#121624]/80 to-transparent"></div>
          <div class="absolute bottom-4 left-4">
            <span :class="room.statusClass" class="px-3 py-1 rounded-lg text-[11px] font-black tracking-widest uppercase shadow-sm">
              {{ room.status }}
            </span>
          </div>
        </div>
        
        <div class="flex-1 p-8 flex flex-col justify-between">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-xl font-black text-white mb-2 tracking-wide">{{ room.name }}</h3>
              <div class="flex items-center gap-2 text-gray-500 text-sm font-bold">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                {{ room.location }}
              </div>
            </div>
            <div class="flex items-center gap-1.5 bg-[#1A1F30] px-3 py-1.5 rounded-xl border border-[#1C2136]">
              <svg class="w-4 h-4 text-amber-500" fill="currentColor" viewBox="0 0 20 20"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path></svg>
              <span class="text-white font-black text-sm">{{ room.rating }}</span>
            </div>
          </div>

          <div class="grid grid-cols-3 gap-8 py-8 border-y border-dashed border-[#1C2136] my-6">
            <div>
              <div class="text-[11px] font-bold text-gray-500 tracking-widest uppercase mb-1">座位总数</div>
              <div class="text-xl font-black text-white tracking-tight">{{ room.totalSeats }}</div>
            </div>
            <div>
              <div class="text-[11px] font-bold text-gray-500 tracking-widest uppercase mb-1">当前可用</div>
              <div class="text-xl font-black text-emerald-500 tracking-tight">{{ room.availableSeats }}</div>
            </div>
            <div>
              <div class="text-[11px] font-bold text-gray-500 tracking-widest uppercase mb-1">评分</div>
              <div class="text-xl font-black text-white tracking-tight">{{ room.rating }}</div>
            </div>
          </div>

          <div class="flex items-center gap-3">
            <button class="flex-1 bg-[#1A1F30] hover:bg-[#252b41] border border-[#2A314D] text-white py-3 rounded-xl text-[13px] font-bold transition-all flex items-center justify-center gap-2">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
              布局设置
            </button>
            <button class="w-12 h-12 bg-[#1A1F30] hover:bg-gray-700/30 border border-[#2A314D] rounded-xl flex items-center justify-center text-gray-400 hover:text-white transition-all">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
            </button>
            <button class="w-12 h-12 bg-red-500/10 hover:bg-red-500 text-red-500 hover:text-white border border-red-500/20 rounded-xl flex items-center justify-center transition-all">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Add Add New Room Card -->
      <div class="bg-[#121624] border border-[#1C2136] border-dashed rounded-[24px] p-12 flex flex-col items-center justify-center group hover:border-[#5A52FF] hover:bg-[#5A52FF]/5 transition-all cursor-pointer">
        <div class="w-16 h-16 rounded-full bg-[#1A1F30] border-2 border-[#1C2136] flex items-center justify-center text-gray-500 group-hover:text-[#5A52FF] group-hover:border-[#5A52FF]/50 transition-all mb-4">
          <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path></svg>
        </div>
        <h3 class="text-xl font-black text-gray-500 group-hover:text-white transition-colors mb-2">添加新自习室</h3>
        <p class="text-sm font-bold text-gray-600 tracking-wider">点击上传图片并配置空间属性</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const bookings = [
  { id: 'BK-10294', type: 'STANDARD TYPE', user: '张同学', userId: '20220100', room: 'A区', seat: 'A-08', time: '14:20', duration: '1H', status: '已签到', statusClass: 'bg-emerald-500/10 text-emerald-500', initial: '张' },
  { id: 'BK-10293', type: 'STANDARD TYPE', user: '李同学', userId: '20220101', room: 'B区', seat: 'B-12', time: '14:15', duration: '2H', status: '预约中', statusClass: 'bg-blue-500/10 text-blue-500', initial: '李' },
  { id: 'BK-10292', type: 'STANDARD TYPE', user: '王同学', userId: '20220102', room: 'A区', seat: 'A-15', time: '14:10', duration: '1H', status: '已过期', statusClass: 'bg-red-500/10 text-red-500', initial: '王' },
  { id: 'BK-10291', type: 'STANDARD TYPE', user: '赵同学', userId: '20220103', room: 'C区', seat: 'C-05', time: '14:05', duration: '3H', status: '已签到', statusClass: 'bg-emerald-500/10 text-emerald-500', initial: '赵' },
  { id: 'BK-10290', type: 'STANDARD TYPE', user: '陈同学', userId: '20220104', room: 'A区', seat: 'A-01', time: '13:50', duration: '2H', status: '已取消', statusClass: 'bg-red-500/10 text-red-500', initial: '陈' },
  { id: 'BK-10289', type: 'STANDARD TYPE', user: '刘同学', userId: '20220105', room: 'B区', seat: 'B-08', time: '13:45', duration: '1H', status: '已完成', statusClass: 'bg-blue-500/10 text-blue-500', initial: '刘' }
]

const activeFilter = ref('全部状态')
const filters = ['全部状态', '进行中', '已完成', '已取消', '违约记录']
</script>

<template>
  <div class="flex flex-col gap-8 pb-10">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">预约记录</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">查看用户预约详情、处理签到及违约状态</p>
      </div>
      <div class="flex items-center gap-4">
        <button class="bg-[#1A1F30] hover:bg-[#252b41] border border-[#2A314D] text-gray-300 px-6 py-2.5 rounded-xl text-sm font-black transition-all flex items-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path></svg>
          导出报表 
        </button>
      </div>
    </div>

    <!-- Search & Filters -->
    <div class="bg-[#121624] border border-[#1C2136] rounded-[24px] p-8 flex flex-col gap-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <h3 class="text-lg font-black text-white tracking-wide">预约记录检索</h3>
        <div class="flex items-center gap-4">
           <div class="relative group w-[300px]">
            <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 group-focus-within:text-[#5A52FF] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
            <input type="text" placeholder="搜索姓名/订单号..." class="w-full bg-[#1A1F30] border border-[#2A314D] text-white pl-11 pr-5 py-3 rounded-xl text-sm focus:border-[#5A52FF]/50 outline-none transition-all font-bold">
          </div>
          <button class="w-12 h-12 bg-[#1A1F30] hover:bg-[#252b41] border border-[#1C2136] rounded-xl flex items-center justify-center text-gray-400">
             <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"></path></svg>
          </button>
        </div>
      </div>
      
      <div class="flex flex-wrap gap-3">
        <button v-for="f in filters" :key="f" @click="activeFilter = f"
          :class="activeFilter === f ? 'bg-[#5A52FF] text-white' : 'bg-[#1A1F30] text-gray-400 hover:text-white'"
          class="px-6 py-2.5 rounded-xl text-[13px] font-black tracking-wide transition-all active:scale-95">
          {{ f }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-[#121624] border border-[#1C2136] rounded-[24px] overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-[#1A1F30] border-b border-[#1C2136]">
          <tr>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest">订单信息</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest">用户详情</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest">自习室/座位</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest">预约时间</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest text-center">状态</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest text-right">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#1C2136]">
          <tr v-for="b in bookings" :key="b.id" class="hover:bg-white/[0.02] transition-colors group">
            <td class="px-8 py-6">
              <div class="text-[14px] font-black text-white tracking-wide mb-1">{{ b.id }}</div>
              <div class="text-[10px] text-gray-550 font-bold tracking-widest opacity-60">{{ b.type }}</div>
            </td>
            <td class="px-8 py-6">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-full bg-[#1A1F30] border border-[#2A314D] flex items-center justify-center text-[#5A52FF] text-[13px] font-black">
                  {{ b.initial }}
                </div>
                <div>
                  <div class="text-[14px] font-black text-white leading-none">{{ b.user }}</div>
                  <div class="text-[11px] text-gray-550 font-bold tracking-widest opacity-60 mt-1">{{ b.userId }}</div>
                </div>
              </div>
            </td>
            <td class="px-8 py-6">
              <div class="text-[14px] font-black text-white tracking-wide mb-1">{{ b.room }}</div>
              <div class="text-[11px] text-[#5A52FF] font-black tracking-widest uppercase">{{ b.seat }}</div>
            </td>
            <td class="px-8 py-6">
              <div class="text-[14px] font-black text-white tracking-wide mb-1">{{ b.time }}</div>
              <div class="flex items-center gap-1.5 text-[10px] text-gray-500 font-bold">
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                DURATION: {{ b.duration }}
              </div>
            </td>
            <td class="px-8 py-6">
              <div class="flex justify-center">
                <div :class="b.statusClass" class="text-[11px] font-black px-4 py-2 rounded-full tracking-widest uppercase text-center min-w-[70px]">
                  {{ b.status }}
                </div>
              </div>
            </td>
            <td class="px-8 py-6 text-right">
              <button class="text-gray-500 hover:text-white transition-colors">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"></path></svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="px-8 py-6 bg-[#1A1F30]/50 flex items-center justify-between border-t border-[#1C2136]">
        <span class="text-[12px] font-bold text-gray-500 tracking-wider">显示第 1-10 条记录，共 1,284 条</span>
        <div class="flex gap-3">
          <button class="bg-[#1A1F30] border border-[#2A314D] text-gray-400 px-4 py-2 rounded-lg text-xs font-bold hover:text-white transition-all">上一页</button>
          <button class="bg-[#1A1F30] border border-[#2A314D] text-gray-400 px-4 py-2 rounded-lg text-xs font-bold hover:text-white transition-all">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

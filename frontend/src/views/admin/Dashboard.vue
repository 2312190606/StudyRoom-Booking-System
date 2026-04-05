<script setup>
import { ref } from 'vue'

const stats = [
  { label: '总预约量', value: '1,284', trend: '+12.5%', isUp: true, icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z', color: 'text-[#5A52FF]', bg: 'bg-[#5A52FF]/10' },
  { label: '活跃用户', value: '842', trend: '+8.2%', isUp: true, icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z', color: 'text-[#a855f7]', bg: 'bg-[#a855f7]/10' },
  { label: '平均利用率', value: '82.4%', trend: '-2.1%', isUp: false, icon: 'M13 7h8m0 0v8m0-8l-8 8-4-4-6 6', color: 'text-[#10b981]', bg: 'bg-[#10b981]/10' },
  { label: '自习室总数', value: '8', trend: '+1', isUp: true, icon: 'M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z M15 11a3 3 0 11-6 0 3 3 0 016 0z', color: 'text-[#f59e0b]', bg: 'bg-[#f59e0b]/10' }
]

const recentActivities = [
  { user: '张同学', room: 'A区 · A-08', time: '14:20', status: '已签到', statusClass: 'bg-emerald-500/10 text-emerald-500', initial: '张' },
  { user: '李同学', room: 'B区 · B-12', time: '14:15', status: '预约中', statusClass: 'bg-blue-500/10 text-blue-500', initial: '李' },
  { user: '王同学', room: 'A区 · A-15', time: '14:10', status: '已过期', statusClass: 'bg-red-500/10 text-red-500', initial: '王' },
  { user: '赵同学', room: 'C区 · C-05', time: '14:05', status: '已签到', statusClass: 'bg-emerald-500/10 text-emerald-500', initial: '赵' }
]
</script>

<template>
  <div class="flex flex-col gap-8 pb-10">
    
    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div v-for="stat in stats" :key="stat.label" class="bg-[#121624] border border-[#1C2136] rounded-[24px] p-6 flex flex-col justify-between hover:border-[#303753] transition-colors relative overflow-hidden group">
        <div class="absolute -right-10 -top-10 w-32 h-32 rounded-full blur-[50px] opacity-10 transition-opacity group-hover:opacity-20 pointer-events-none" :class="stat.bg"></div>
        
        <div class="flex items-center justify-between mb-8 relative z-10">
          <div class="w-12 h-12 rounded-2xl flex items-center justify-center shadow-lg" :class="[stat.bg, stat.color]">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" :d="stat.icon"></path></svg>
          </div>
          <div class="flex items-center gap-1.5 px-3 py-1.5 rounded-full text-[12px] font-black tracking-wider" :class="stat.isUp ? 'bg-emerald-500/10 text-emerald-500' : 'bg-red-500/10 text-red-500'">
            <svg v-if="stat.isUp" class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M7 11l5-5m0 0l5 5m-5-5v12"></path></svg>
            <svg v-else class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M17 13l-5 5m0 0l-5-5m5 5V6"></path></svg>
            {{ stat.trend }}
          </div>
        </div>
        
        <div class="relative z-10">
          <div class="text-[12px] font-bold text-gray-500 tracking-wider mb-1.5">{{ stat.label }}</div>
          <div class="text-[2rem] font-black text-white leading-none tracking-tight shrink-0">{{ stat.value }}</div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      
      <!-- Chart Area (Left) -->
      <div class="lg:col-span-2 bg-[#121624] border border-[#1C2136] rounded-[24px] p-8 flex flex-col">
        <div class="flex items-center justify-between mb-8">
          <div>
            <h3 class="text-lg font-black text-white tracking-wide mb-1">预约流量统计</h3>
            <p class="text-[12px] font-bold text-gray-500 tracking-wider">最近 7 天的每日预约趋势与座位利用率</p>
          </div>
          <button class="bg-[#1A1F30] hover:bg-[#252b41] border border-[#2A314D] text-gray-300 px-5 py-2.5 rounded-[12px] text-[13px] font-bold transition-all shadow-sm">
            导出报告
          </button>
        </div>
        
        <!-- Mock Chart -->
        <div class="flex-1 w-full bg-[#0B0F19] rounded-2xl border border-[#1C2136] p-6 relative flex items-end justify-between overflow-hidden">
          <div class="absolute inset-0 bg-gradient-to-t from-[#5A52FF]/10 to-transparent opacity-50"></div>
          
          <!-- Y-axis -->
          <div class="absolute left-6 top-6 bottom-6 flex flex-col justify-between text-[11px] font-bold text-gray-600 tracking-wider h-[calc(100%-3rem)] pb-8">
            <span>240</span><span>180</span><span>120</span><span>60</span><span>0</span>
          </div>
          
          <!-- Mock Chart SVG line -->
          <svg class="absolute inset-0 w-full h-full pb-10 pl-16 pr-6 pt-6 preserve-3d" preserveAspectRatio="none">
            <polyline fill="none" class="stroke-[#5A52FF]" stroke-width="4" stroke-linejoin="round" stroke-linecap="round" 
              points="0,180 150,165 300,220 450,150 600,60 750,20 900,40 " />
          </svg>
          
          <div class="w-full flex items-end justify-between px-16 z-10 pt-48 border-t border-dashed border-[#2A314D]/50">
            <span class="text-[11px] font-bold text-gray-500 tracking-wider uppercase">Mon</span>
            <span class="text-[11px] font-bold text-gray-500 tracking-wider uppercase">Tue</span>
            <span class="text-[11px] font-bold text-gray-500 tracking-wider uppercase">Wed</span>
            <span class="text-[11px] font-bold text-gray-500 tracking-wider uppercase">Thu</span>
            <span class="text-[11px] font-bold text-gray-500 tracking-wider uppercase">Fri</span>
            <span class="text-[11px] font-bold text-gray-500 tracking-wider uppercase">Sat</span>
            <span class="text-[11px] font-bold text-gray-500 tracking-wider uppercase">Sun</span>
          </div>
        </div>
      </div>

      <!-- Recent Activities (Right) -->
      <div class="bg-[#121624] border border-[#1C2136] rounded-[24px] p-8 flex flex-col">
        <div class="flex items-center justify-between mb-8 cursor-pointer group">
          <h3 class="text-lg font-black text-white tracking-wide">最新动态</h3>
          <svg class="w-5 h-5 text-gray-500 group-hover:text-gray-300 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"></path></svg>
        </div>
        
        <div class="flex-1 flex flex-col gap-6">
          <div v-for="act in recentActivities" :key="act.user" class="flex items-center gap-4 group">
            <div class="w-[42px] h-[42px] shrink-0 rounded-full border-2 border-[#1C2136] bg-[#1A1F30] flex items-center justify-center text-[#5A52FF] text-[15px] font-black group-hover:border-[#5A52FF]/50 transition-colors">
              {{ act.initial }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="text-[14px] font-black text-white tracking-wide truncate">{{ act.user }}</div>
              <div class="text-[11px] text-gray-500 font-bold truncate tracking-wide mt-0.5">{{ act.room }}</div>
            </div>
            <div class="text-right flex flex-col items-end shrink-0">
              <div class="text-[13px] font-black text-gray-300 mb-1 tracking-wider">{{ act.time }}</div>
              <div :class="act.statusClass" class="text-[10px] font-black px-2 py-[3px] rounded-md tracking-widest text-center min-w-[46px]">{{ act.status }}</div>
            </div>
          </div>
        </div>
        
        <button class="w-full mt-8 bg-[#1A1F30] hover:bg-[#252b41] border border-[#2A314D] text-gray-300 py-3.5 rounded-[12px] text-[13px] font-bold transition-all shadow-sm">
          查看全部记录
        </button>
      </div>

    </div>

    <!-- Bottom Search bar -->
    <div class="w-full bg-gradient-to-r from-[#5A52FF] to-[#a855f7] rounded-[24px] p-8 flex flex-col md:flex-row items-center justify-between gap-6 relative overflow-hidden shadow-[0_10px_40px_rgba(90,82,255,0.2)]">
      <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMiIgY3k9IjIiIHI9IjIiIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4wNSIvPjwvc3ZnPg==')] pointer-events-none"></div>
      
      <div class="relative z-10">
        <h3 class="text-2xl font-black text-white tracking-wide mb-1.5">全局数据查询</h3>
        <p class="text-[14px] font-medium text-white/80 tracking-wide">快速搜索特定用户、预约订单或自习室编号，即时获取详细运行报告。</p>
      </div>
      
      <div class="relative w-full md:w-[400px] z-10 group">
        <div class="absolute left-5 top-0 bottom-0 flex items-center text-white/60 group-focus-within:text-white transition-colors">
          <svg class="w-[18px] h-[18px]" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
        </div>
        <input type="text" placeholder="输入姓名、学号或订单号..." class="w-full bg-[#0B0F19]/40 backdrop-blur-md border border-white/10 text-white placeholder-white/50 pl-12 pr-6 py-4 rounded-2xl outline-none focus:bg-[#0B0F19]/60 focus:border-white/30 transition-all font-bold text-[14px]">
      </div>
    </div>
    
  </div>
</template>

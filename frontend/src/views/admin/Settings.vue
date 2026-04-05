<script setup>
import { ref } from 'vue'

const autoViolation = ref(true)
const banThreshold = ref(3)
const maxDuration = ref(4)
const advanceDays = ref(3)
const checkinWindow = ref(15)
const maxUsers = ref(1)
</script>

<template>
  <div class="flex flex-col gap-8 pb-10">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">系统设置</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">全局参数配置、安全策略及规则定义</p>
      </div>
      <div class="flex items-center gap-4">
        <button class="bg-[#1A1F30] hover:bg-[#252b41] border border-[#2A314D] text-gray-300 px-6 py-2.5 rounded-xl text-sm font-black transition-all flex items-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
          重置默认
        </button>
        <button class="bg-[#5A52FF] hover:bg-[#4a42e5] text-white px-6 py-2.5 rounded-xl flex items-center gap-2 text-sm font-black shadow-[0_4px_12px_rgba(90,82,255,0.3)] transition-all">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4"></path></svg>
          保存配置
        </button>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      
      <!-- Left Config Groups -->
      <div class="lg:col-span-2 flex flex-col gap-8">
        
        <!-- Reservation Rules -->
        <div class="bg-[#121624] border border-[#1C2136] rounded-[24px] p-8 flex flex-col gap-8">
          <div class="flex items-center gap-4 mb-2">
            <div class="w-12 h-12 rounded-2xl bg-[#5A52FF]/10 text-[#5A52FF] flex items-center justify-center">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            </div>
            <h3 class="text-xl font-black text-white tracking-wide">预约规则配置</h3>
          </div>
          
          <div class="grid grid-cols-2 gap-8">
            <div class="flex flex-col gap-3">
              <label class="text-[12px] font-bold text-gray-500 uppercase tracking-widest pl-1">最长预约时长 (小时)</label>
              <input type="number" v-model="maxDuration" class="bg-[#0B0F19] border border-[#1C2136] text-white px-6 py-4 rounded-2xl outline-none focus:border-[#5A52FF]/50 transition-all font-black text-lg">
            </div>
            <div class="flex flex-col gap-3">
              <label class="text-[12px] font-bold text-gray-500 uppercase tracking-widest pl-1">提前预约天数</label>
              <input type="number" v-model="advanceDays" class="bg-[#0B0F19] border border-[#1C2136] text-white px-6 py-4 rounded-2xl outline-none focus:border-[#5A52FF]/50 transition-all font-black text-lg">
            </div>
            <div class="flex flex-col gap-3">
              <label class="text-[12px] font-bold text-gray-500 uppercase tracking-widest pl-1">签到窗口期 (分钟)</label>
              <input type="number" v-model="checkinWindow" class="bg-[#0B0F19] border border-[#1C2136] text-white px-6 py-4 rounded-2xl outline-none focus:border-[#5A52FF]/50 transition-all font-black text-lg">
            </div>
            <div class="flex flex-col gap-3">
              <label class="text-[12px] font-bold text-gray-500 uppercase tracking-widest pl-1">单次最大人数</label>
              <input type="number" v-model="maxUsers" class="bg-[#0B0F19] border border-[#1C2136] text-white px-6 py-4 rounded-2xl outline-none focus:border-[#5A52FF]/50 transition-all font-black text-lg">
            </div>
          </div>
        </div>

        <!-- Violation Policy -->
        <div class="bg-[#121624] border border-[#1C2136] rounded-[24px] p-8 flex flex-col gap-8">
          <div class="flex items-center gap-4 mb-2">
            <div class="w-12 h-12 rounded-2xl bg-red-500/10 text-red-500 flex items-center justify-center">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
            </div>
            <h3 class="text-xl font-black text-white tracking-wide">违约及处罚策略</h3>
          </div>
          
          <div class="flex flex-col gap-8">
            <div class="flex items-center justify-between p-6 bg-[#0B0F19] border border-[#1C2136] rounded-2xl">
              <div>
                <h4 class="text-[15px] font-black text-white mb-1">启用自动化违约检测</h4>
                <p class="text-[12px] font-bold text-gray-550 opacity-60 tracking-wider">系统将自动扫描超过 15 分钟未签到的记录并记为违约。</p>
              </div>
              <button @click="autoViolation = !autoViolation" :class="autoViolation ? 'bg-[#5A52FF]' : 'bg-gray-700'" class="w-12 h-6 rounded-full relative transition-colors duration-300">
                <div :class="autoViolation ? 'translate-x-7' : 'translate-x-1'" class="absolute top-1 w-4 h-4 bg-white rounded-full transition-transform duration-300"></div>
              </button>
            </div>
            
            <div class="flex items-center justify-between p-6 bg-[#0B0F19] border border-[#1C2136] rounded-2xl">
              <div>
                <h4 class="text-[15px] font-black text-white mb-1">封禁阈值设置 (次/月)</h4>
                <p class="text-[12px] font-bold text-gray-550 opacity-60 tracking-wider">达到此数值后，账号将被自动锁定 7 天。</p>
              </div>
              <input type="number" v-model="banThreshold" class="bg-[#121624] border border-[#2A314D] text-white px-6 py-3 rounded-xl outline-none focus:border-[#5A52FF]/50 transition-all font-black text-lg w-24 text-center">
            </div>
          </div>
        </div>
      </div>

      <!-- Right Side Panels -->
      <div class="flex flex-col gap-8">
        
        <!-- About System -->
        <div class="bg-[#121624] border border-[#1C2136] rounded-[24px] p-8 flex flex-col gap-6">
          <h3 class="text-lg font-black text-white tracking-wide">关于系统运行</h3>
          
          <div class="bg-blue-500/5 border border-blue-500/10 p-6 rounded-2xl flex gap-4">
            <svg class="w-5 h-5 text-blue-500 shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            <p class="text-[12px] font-bold text-blue-200/70 leading-relaxed tracking-wide">
              当前系统版本为 <span class="text-blue-400 font-black">v2.4.8-stable</span>。所有配置更改将实时同步至前台应用，并在下一个预约周期内生效。
            </p>
          </div>
          
          <div class="flex flex-col gap-4 py-2">
            <div class="flex items-center justify-between">
              <span class="text-[13px] font-bold text-gray-500 uppercase tracking-widest">服务器状态</span>
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)]"></div>
                <span class="text-[13px] font-black text-white">运行中 (Stable)</span>
              </div>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-[13px] font-bold text-gray-500 uppercase tracking-widest">上次备份时间</span>
              <div class="text-right">
                <div class="text-[13px] font-black text-white">2024-03-12 04:00 AM</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Quick Tools -->
        <div class="bg-gradient-to-br from-[#1A1F30] to-[#121624] border border-[#1C2136] rounded-[24px] p-8 flex flex-col gap-8 shadow-2xl">
          <h3 class="text-lg font-black text-indigo-100 tracking-wide">快捷工具</h3>
          
          <div class="flex flex-col gap-4">
            <button class="bg-[#0B0F19]/60 hover:bg-[#5A52FF]/10 hover:border-[#5A52FF]/30 border border-[#2A314D] p-5 rounded-2xl flex items-center justify-between transition-all group">
              <span class="text-[14px] font-black text-gray-300 group-hover:text-white">手动清空过期记录</span>
              <svg class="w-4 h-4 text-gray-500 group-hover:text-[#5A52FF] group-hover:rotate-180 transition-all duration-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
            </button>
            <button class="bg-[#0B0F19]/60 hover:bg-emerald-500/10 hover:border-emerald-500/30 border border-[#2A314D] p-5 rounded-2xl flex items-center justify-between transition-all group">
              <span class="text-[14px] font-black text-gray-300 group-hover:text-white">导出系统日志 (Log)</span>
              <svg class="w-4 h-4 text-gray-500 group-hover:text-emerald-500 transition-all" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path></svg>
            </button>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

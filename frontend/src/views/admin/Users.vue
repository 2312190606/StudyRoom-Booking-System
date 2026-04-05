<script setup>
import { ref } from 'vue'

const users = [
  { id: '20220101', name: '张同学', joinDate: '2023-09-01', major: '计算机科学', count: 42, status: '正常', statusClass: 'text-emerald-500 bg-emerald-500/10', initial: '张' },
  { id: '20220102', name: '李同学', joinDate: '2023-09-05', major: '软件工程', count: 28, status: '正常', statusClass: 'text-emerald-500 bg-emerald-500/10', initial: '李' },
  { id: '20220103', name: '王同学', joinDate: '2023-10-12', major: '人工智能', count: 15, status: '禁用', statusClass: 'text-red-500 bg-red-500/10', initial: '王' },
  { id: '20220104', name: '赵同学', joinDate: '2023-09-20', major: '网络安全', count: 36, status: '正常', statusClass: 'text-emerald-500 bg-emerald-500/10', initial: '赵' },
  { id: '20220105', name: '陈同学', joinDate: '2023-11-01', major: '数据科学', count: 22, status: '正常', statusClass: 'text-emerald-500 bg-emerald-500/10', initial: '陈' }
]
</script>

<template>
  <div class="flex flex-col gap-8 pb-10">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-[1.75rem] font-black text-white tracking-wide mb-1">用户管理</h2>
        <p class="text-[14px] font-bold text-gray-500 tracking-wider">查看用户信息、管理权限及违约记录</p>
      </div>
      <div class="flex items-center gap-4">
        <div class="relative group w-[300px]">
          <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 group-focus-within:text-[#5A52FF] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
          <input type="text" placeholder="搜索姓名/学号..." class="w-full bg-[#121624] border border-[#1C2136] text-white pl-11 pr-5 py-3 rounded-xl text-sm focus:border-[#5A52FF]/50 outline-none transition-all font-bold">
        </div>
        <button class="bg-[#5A52FF] hover:bg-[#4a42e5] text-white px-6 py-2.5 rounded-xl flex items-center gap-2 text-sm font-black shadow-[0_4px_12px_rgba(90,82,255,0.3)] transition-all">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path></svg>
          手动添加用户
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-[#121624] border border-[#1C2136] rounded-[24px] overflow-hidden">
      <table class="w-full text-left border-collapse">
        <thead class="bg-[#1A1F30] border-b border-[#1C2136]">
          <tr>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest border-r border-white/5">学号</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest border-r border-white/5">基本信息</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest border-r border-white/5">专业</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest border-r border-white/5">累计预约</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest text-center border-r border-white/5">状态</th>
            <th class="px-8 py-5 text-[11px] font-black text-gray-500 uppercase tracking-widest text-right">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#1C2136]">
          <tr v-for="u in users" :key="u.id" class="hover:bg-white/[0.02] transition-colors group">
            <td class="px-8 py-7 border-r border-white/5">
              <span class="text-[14px] font-black text-white tracking-wide">{{ u.id }}</span>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-full border-2 border-[#1C2136] bg-[#1A1F30] flex items-center justify-center text-[#5A52FF] text-[14px] font-black group-hover:border-[#5A52FF]/50 transition-colors">
                  {{ u.initial }}
                </div>
                <div>
                  <div class="text-[14px] font-black text-white leading-none tracking-wide mb-1">{{ u.name }}</div>
                  <div class="text-[11px] text-gray-500 font-bold tracking-tight">注册于 {{ u.joinDate }}</div>
                </div>
              </div>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <span class="text-[14px] font-bold text-gray-300">{{ u.major }}</span>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="flex flex-col">
                <span class="text-[15px] font-black text-white">{{ u.count }}</span>
                <span class="text-[10px] text-gray-500 font-bold uppercase tracking-widest opacity-60">次预约</span>
              </div>
            </td>
            <td class="px-8 py-7 border-r border-white/5">
              <div class="flex justify-center">
                <div :class="u.statusClass" class="flex items-center gap-2 px-4 py-2 rounded-xl text-[11px] font-black tracking-widest uppercase">
                  <div class="w-1.5 h-1.5 rounded-full" :class="u.status === '正常' ? 'bg-emerald-500' : 'bg-red-500'"></div>
                  {{ u.status }}
                </div>
              </div>
            </td>
            <td class="px-8 py-7 text-right">
              <div class="flex items-center justify-end gap-3 opacity-0 group-hover:opacity-100 transition-opacity">
                <button class="w-10 h-10 rounded-xl bg-[#1A1F30] border border-[#2A314D] flex items-center justify-center text-gray-400 hover:text-white hover:border-white/30 transition-all">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path></svg>
                </button>
                 <button class="w-10 h-10 rounded-xl bg-[#1A1F30] border border-[#2A314D] flex items-center justify-center text-gray-400 hover:text-white hover:border-white/30 transition-all">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"></path></svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

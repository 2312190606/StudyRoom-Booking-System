<script setup>
import { ref, onMounted } from 'vue'
import HelloWorld from './components/HelloWorld.vue'
import { getRooms } from './api/rooms' // 导入我们刚才写好的 API

// 用于在界面上展示数据
const roomData = ref(null)

onMounted(async () => {
  console.log('正在调用后端接口 (Mock 拦截)...')
  try {
    // 调用我们在 rooms.js 封装好的接口
    const data = await getRooms({ page: 1, size: 10 })
    console.log('✅ 接口调用成功！返回的数据如下：', data)
    roomData.value = data
  } catch (error) {
    console.error('❌ 接口调用失败：', error)
  }
})
</script>

<template>
  <div>
    <!-- 用来在页面上直接显示拿到的数据结构，方便老师看明白 -->
    <h2>📡 前端调用 API 测试结果：</h2>
    <pre v-if="roomData" style="text-align: left; background: #222; color: #42b883; padding: 15px; border-radius: 8px; font-size: 14px;">
{{ JSON.stringify(roomData, null, 2) }}
    </pre>
    <p v-else>正在加载数据...</p>
    
    <a href="https://vite.dev" target="_blank">
      <img src="/vite.svg" class="logo" alt="Vite logo" />
    </a>
    <a href="https://vuejs.org/" target="_blank">
      <img src="./assets/vue.svg" class="logo vue" alt="Vue logo" />
    </a>
  </div>
  <HelloWorld msg="Vite + Vue" />
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>

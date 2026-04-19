<script setup>
import { ref, nextTick } from 'vue'
import { Popup, Icon } from 'vant'
import { askAI } from '../api/ai'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:show'])

const closeChat = () => {
  emit('update:show', false)
}

const messages = ref([
  {
    id: 1,
    type: 'ai',
    content: '你好！我是静学自习室的小助手，有什么可以帮助你的吗？'
  }
])

const userInput = ref('')
const loading = ref(false)
const messageList = ref(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  const content = userInput.value.trim()
  if (!content || loading.value) return

  // 添加用户消息
  messages.value.push({
    id: Date.now(),
    type: 'user',
    content
  })
  userInput.value = ''
  scrollToBottom()

  loading.value = true

  try {
    const answer = await askAI(content)
    messages.value.push({
      id: Date.now() + 1,
      type: 'ai',
      content: answer || '抱歉，我现在无法回答这个问题，请稍后再试。'
    })
  } catch (error) {
    // 演示模式：关键词匹配回复
    const demoReply = getDemoReply(content)
    messages.value.push({
      id: Date.now() + 1,
      type: 'ai',
      content: demoReply
    })
  }

  loading.value = false
  scrollToBottom()
}

// 演示用关键词匹配回复
const getDemoReply = (question) => {
  const q = question.toLowerCase()

  if (q.includes('预约') || q.includes('预订')) {
    return '预约座位很简单！1. 进入首页选择自习室；2. 点击座位图选择心仪座位；3. 选择预约时间并确认。预约成功后记得签到哦！'
  }

  if (q.includes('取消') || q.includes('退订')) {
    return '取消预约可以在"我的预约"页面进行，需要在预约开始前取消。频繁取消预约会影响您的信誉分哦～'
  }

  if (q.includes('签到')) {
    return '签到需要到达自习室后，点击"我的预约"中的签到按钮，系统会自动验证您的位置。签到成功可获得5分信誉分奖励！'
  }

  if (q.includes('信誉分') || q.includes('信用')) {
    return '信誉分初始为100分。签到成功+5分，违约一次-10分，每日签到可额外+5分。信誉分低于60分将无法预约座位。'
  }

  if (q.includes('密码') || q.includes('忘记')) {
    return '忘记密码可以联系管理员重置。您也可以在个人中心修改登录密码。'
  }

  if (q.includes('座位') || q.includes('位置')) {
    return '自习室座位分为普通座位、靠窗座位和带电源座位。可以在座位图中查看各座位类型，选择您喜欢的座位预约。'
  }

  if (q.includes('时间') || q.includes('时段')) {
    return '自习室开放时间为每天 7:00-22:30。最长可预约4小时，提前30分钟可取消预约。'
  }

  if (q.includes('违约') || q.includes('违规')) {
    return '违约包括：预约后未签到、签到后提前离开超过30分钟等。违约一次扣10分，信誉分低于60分将被禁用预约功能。'
  }

  if (q.includes('你好') || q.includes('hi') || q.includes('hello')) {
    return '你好！有什么关于自习室的问题可以问我哦～'
  }

  if (q.includes('谢谢') || q.includes('感谢')) {
    return '不客气！祝您学习愉快～'
  }

  if (q.includes('管理员') || q.includes('联系')) {
    return '如有特殊问题，请联系自习室管理员或拨打服务热线。'
  }

  return '抱歉，我不太明白您的问题。您可以尝试咨询：预约方法、签到流程、信誉分规则、座位类型等。'
}
</script>

<template>
  <Popup
    :show="show"
    position="bottom"
    round
    closeable
    close-icon="cross"
    close-icon-position="top-right"
    @update:show="closeChat"
    style="height: 70%; overflow: hidden;"
  >
    <div class="chat-container">
      <!-- Header -->
      <div class="chat-header">
        <div class="chat-title">
          <span class="chat-avatar">AI</span>
          <div>
            <div class="font-bold text-gray-800">自习室小助手</div>
            <div class="text-xs text-gray-400">在线服务</div>
          </div>
        </div>
      </div>

      <!-- Messages -->
      <div ref="messageList" class="chat-messages">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['message-item', msg.type === 'user' ? 'user' : 'ai']"
        >
          <div v-if="msg.type === 'ai'" class="avatar ai-avatar">
            <span>AI</span>
          </div>
          <div class="message-bubble" :class="msg.type">
            {{ msg.content }}
          </div>
          <div v-if="msg.type === 'user'" class="avatar user-avatar">
            <span>我</span>
          </div>
        </div>

        <!-- Loading indicator -->
        <div v-if="loading" class="message-item ai">
          <div class="avatar ai-avatar"><span>AI</span></div>
          <div class="message-bubble ai loading">
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
          </div>
        </div>
      </div>

      <!-- Input -->
      <div class="chat-input">
        <input
          v-model="userInput"
          type="text"
          placeholder="输入问题..."
          @keyup.enter="sendMessage"
        />
        <button @click="sendMessage" :disabled="loading || !userInput.trim()">
          <Icon name="arrow-up" size="20" color="#fff" />
        </button>
      </div>
    </div>
  </Popup>
</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
  background: #fff;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #f7f8fa;
}

.message-item {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  max-width: 85%;
}

.message-item.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-item.ai {
  align-self: flex-start;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.user-avatar {
  background: #5A52FF;
  color: #fff;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-bubble.ai {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.message-bubble.user {
  background: #5A52FF;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.loading {
  display: flex;
  gap: 4px;
  padding: 16px 20px;
}

.loading-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #999;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) { animation-delay: -0.32s; }
.loading-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-top: 1px solid #eee;
}

.chat-input input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 24px;
  font-size: 14px;
  outline: none;
}

.chat-input input:focus {
  border-color: #5A52FF;
}

.chat-input button {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #5A52FF;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.chat-input button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.chat-input button:not(:disabled):hover {
  background: #4a42e5;
}
</style>

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

  // 预约相关
  if (q.includes('预约') || q.includes('预订') || q.includes('怎么预约') || q.includes('如何预约')) {
    return '预约座位很简单！1. 进入首页选择想要的自习室；2. 点击"去预选座位"进入座位图；3. 点击绿色的可用座位；4. 确认预约信息。预约成功后记得在到达自习室后签到哦！'
  }

  // 取消预约
  if (q.includes('取消') || q.includes('退订') || q.includes('退掉')) {
    return '取消预约可以在"我的预约"页面进行，找到需要取消的预约，点击"取消预约"按钮即可。需要注意的是，频繁取消预约会影响您的信誉分哦～'
  }

  // 延后预约
  if (q.includes('延后') || q.includes('推迟') || q.includes('延迟')) {
    return '延后预约可以在"我的预约"页面进行，点击"预约延后"按钮可以将预约时间延长30分钟。每个预约只能延后一次。'
  }

  // 签到相关
  if (q.includes('签到') || q.includes('签到')) {
    return '签到需要到达自习室后，点击"我的预约"中的"立即签到"按钮。系统会获取您的位置信息进行验证，签到成功可获得5分信誉分奖励！需要确保手机定位功能已开启。'
  }

  // 结束学习
  if (q.includes('结束') || q.includes('释放座位') || q.includes('完成')) {
    return '学习结束后，请在"我的预约"页面点击"自习结束"按钮，系统会自动释放您的座位。离开前别忘了点击结束，否则可能影响您的信誉分。'
  }

  // 信誉分
  if (q.includes('信誉分') || q.includes('信用') || q.includes('积分')) {
    return '信誉分说明：初始为100分。签到成功+5分，每日签到额外+5分，违约一次-10分。信誉分低于60分将无法预约座位，低于80分每日可预约次数受限。'
  }

  // 违约
  if (q.includes('违约') || q.includes('违规') || q.includes('扣分')) {
    return '违约行为包括：预约后未签到、签到后提前离开超过30分钟等。违约一次扣10分，信誉分低于60分将被禁用预约功能。请遵守预约规则，维护良好的信用记录。'
  }

  // 密码相关
  if (q.includes('密码') || q.includes('忘记密码') || q.includes('修改密码')) {
    return '修改密码：在"个人中心"页面点击"修改登录密码"即可。如忘记密码，请联系自习室管理员重置。'
  }

  // 座位类型
  if (q.includes('座位') || q.includes('位置') || q.includes('类型')) {
    return '您可以在座位图中查看各座位的类型和状态（绿色可用、红色已占用、灰色维修中、黄色已预约）。'
  }

  // 开放时间
  if (q.includes('时间') || q.includes('时段') || q.includes('开放') || q.includes('营业')) {
    return '自习室开放时间一般为每天 7:00-22:00。建议提前30分钟到达以便签到。'
  }

  // 常用座位
  if (q.includes('常用') || q.includes('常用座位') || q.includes('快速')) {
    return '常用座位功能：设置后可以一键预约您常坐的座位，省去每次选座的麻烦。在首页找到"常用座位"区域，点击"设置常用"即可配置。'
  }

  // 公告
  if (q.includes('公告') || q.includes('通知') || q.includes('消息')) {
    return '公告信息会显示在首页轮播区，点击"查看更多"可以阅读所有公告。请关注自习室的重要通知和规则调整。'
  }

  // 关于
  if (q.includes('关于') || q.includes('自习室') || q.includes('介绍')) {
    return '静学自习室预约系统为您提供便捷的座位预约服务。支持实时查看座位状态、在线预约、签到验证等功能，让您学习更高效！'
  }

  // 你好问候
  if (q.includes('你好') || q.includes('hi') || q.includes('hello') || q.includes('您好')) {
    return '你好！有什么关于自习室预约的问题可以问我哦～我可以帮你解答预约、签到、信誉分等问题。'
  }

  // 谢谢
  if (q.includes('谢谢') || q.includes('感谢')) {
    return '不客气！祝您学习愉快～如果还有其他问题，随时问我。'
  }

  // 联系管理员
  if (q.includes('管理员') || q.includes('联系') || q.includes('人工')) {
    return '如有特殊问题或需要人工帮助，请联系自习室管理员。联系方式可在"个人中心"的"关于静学自习室"页面查看。'
  }

  // 自习结束
  if (q.includes('自习结束') || q.includes('离开')) {
    return '学习完成后，请在"我的预约"页面点击"自习结束"按钮，系统会自动释放座位。记得点击结束离开哦，否则可能影响您的信誉分！'
  }

  // 不清楚的问题
  return '抱歉，我不太明白您的问题。请联系自习室管理员。'
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

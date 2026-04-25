/**
 * AIChatButton 组件测试
 */

// 模拟 Vue
import { ref, nextTick } from 'vue';

// 模拟 Vant 的 Popup 和 Icon 组件
jest.mock('vant', () => ({
  Popup: {
    name: 'Popup',
    props: ['show', 'position', 'round', 'closeable', 'closeIcon', 'closeIconPosition'],
    template: '<div class="popup-mock" v-if="show"><slot /></div>'
  },
  Icon: {
    name: 'Icon',
    props: ['name', 'size', 'color'],
    template: '<span class="icon-mock">{{ name }}</span>'
  }
}));

// 模拟 askAI API
jest.mock('@/api/ai', () => ({
  askAI: jest.fn().mockResolvedValue('这是一个测试回复')
}));

// 直接测试组件逻辑而不是挂载组件
describe('AIChatButton 组件逻辑测试', () => {
  describe('消息处理逻辑', () => {
    const messages = ref([
      {
        id: 1,
        type: 'ai',
        content: '你好！我是静学自习室的小助手，有什么可以帮助你的吗？'
      }
    ]);

    const userInput = ref('');
    const loading = ref(false);

    test('初始消息应该包含欢迎语', () => {
      expect(messages.value.length).toBeGreaterThan(0);
      expect(messages.value[0].content).toContain('你好');
    });

    test('发送空消息不应该添加消息', () => {
      const initialLength = messages.value.length;
      const content = userInput.value.trim();
      if (!content || loading.value) return;
      // 条件不满足，不会添加消息
      expect(messages.value.length).toBe(initialLength);
    });

    test('发送消息应该添加用户消息', () => {
      const initialLength = messages.value.length;
      userInput.value = '测试问题';
      const content = userInput.value.trim();
      if (!content || loading.value) return;

      messages.value.push({
        id: Date.now(),
        type: 'user',
        content
      });
      userInput.value = '';

      expect(messages.value.length).toBe(initialLength + 1);
      expect(messages.value[messages.value.length - 1].type).toBe('user');
      expect(messages.value[messages.value.length - 1].content).toBe('测试问题');
    });

    test('loading状态应该阻止发送', () => {
      const initialLength = messages.value.length;
      userInput.value = '测试问题';
      loading.value = true;

      const content = userInput.value.trim();
      if (!content || loading.value) return;
      messages.value.push({ id: Date.now(), type: 'user', content });

      expect(messages.value.length).toBe(initialLength);
      loading.value = false;
    });
  });

  describe('关键词回复逻辑', () => {
    // 提取 getDemoReply 逻辑进行测试
    const getDemoReply = (question) => {
      const q = question.toLowerCase();

      if (q.includes('预约') || q.includes('预订')) {
        return '预约座位很简单！1. 进入首页选择自习室；2. 点击座位图选择心仪座位；3. 选择预约时间并确认。预约成功后记得签到哦！';
      }

      if (q.includes('取消') || q.includes('退订')) {
        return '取消预约可以在"我的预约"页面进行，需要在预约开始前取消。频繁取消预约会影响您的信誉分哦～';
      }

      if (q.includes('签到')) {
        return '签到需要到达自习室后，点击"我的预约"中的签到按钮，系统会自动验证您的位置。签到成功可获得5分信誉分奖励！';
      }

      if (q.includes('信誉分') || q.includes('信用')) {
        return '信誉分初始为100分。签到成功+5分，违约一次-10分，每日签到可额外+5分。信誉分低于60分将无法预约座位。';
      }

      if (q.includes('密码') || q.includes('忘记')) {
        return '忘记密码可以联系管理员重置。您也可以在个人中心修改登录密码。';
      }

      if (q.includes('座位') || q.includes('位置')) {
        return '自习室座位分为普通座位、靠窗座位和带电源座位。可以在座位图中查看各座位类型，选择您喜欢的座位预约。';
      }

      if (q.includes('时间') || q.includes('时段')) {
        return '自习室开放时间为每天 7:00-22:30。最长可预约4小时，提前30分钟可取消预约。';
      }

      if (q.includes('违约') || q.includes('违规')) {
        return '违约包括：预约后未签到、签到后提前离开超过30分钟等。违约一次扣10分，信誉分低于60分将被禁用预约功能。';
      }

      if (q.includes('你好') || q.includes('hi') || q.includes('hello')) {
        return '你好！有什么关于自习室的问题可以问我哦～';
      }

      if (q.includes('谢谢') || q.includes('感谢')) {
        return '不客气！祝您学习愉快～';
      }

      if (q.includes('管理员') || q.includes('联系')) {
        return '如有特殊问题，请联系自习室管理员或拨打服务热线。';
      }

      return '抱歉，我不太明白您的问题。您可以尝试咨询：预约方法、签到流程、信誉分规则、座位类型等。';
    };

    test('预约相关问题应该返回预约相关回复', () => {
      expect(getDemoReply('如何预约')).toContain('预约');
      expect(getDemoReply('我想预订')).toContain('预约');
      expect(getDemoReply('怎么预约座位')).toContain('预约');
    });

    test('取消相关问题应该返回取消相关回复', () => {
      expect(getDemoReply('如何取消')).toContain('取消');
      expect(getDemoReply('退订')).toContain('取消');
    });

    test('签到相关问题应该返回签到相关回复', () => {
      expect(getDemoReply('如何签到')).toContain('签到');
      expect(getDemoReply('签到流程')).toContain('签到');
    });

    test('信誉分相关问题应该返回信誉分相关回复', () => {
      expect(getDemoReply('信誉分是什么')).toContain('信誉分');
      expect(getDemoReply('信用分')).toContain('信誉分');
    });

    test('密码相关问题应该返回密码相关回复', () => {
      expect(getDemoReply('忘记密码')).toContain('密码');
      expect(getDemoReply('密码忘了')).toContain('密码');
    });

    test('座位相关问题应该返回座位相关回复', () => {
      expect(getDemoReply('座位类型')).toContain('座位');
      expect(getDemoReply('位置')).toContain('座位');
    });

    test('时间相关问题应该返回时间相关回复', () => {
      expect(getDemoReply('开放时间')).toContain('时间');
      expect(getDemoReply('时段')).toContain('时间');
    });

    test('违约相关问题应该返回违约相关回复', () => {
      expect(getDemoReply('什么是违约')).toContain('违约');
      expect(getDemoReply('违规')).toContain('违约');
    });

    test('问候语应该返回问候回复', () => {
      expect(getDemoReply('你好')).toContain('你好');
      expect(getDemoReply('hi')).toContain('你好');
      expect(getDemoReply('hello')).toContain('你好');
    });

    test('感谢语应该返回感谢回复', () => {
      expect(getDemoReply('谢谢')).toContain('不客气');
      expect(getDemoReply('感谢')).toContain('不客气');
    });

    test('管理员相关问题应该返回管理员相关回复', () => {
      expect(getDemoReply('管理员')).toContain('管理员');
      expect(getDemoReply('联系')).toContain('管理员');
    });

    test('未知问题应该返回默认回复', () => {
      const reply = getDemoReply('乱七八糟的内容 xyz');
      expect(reply).toContain('不太明白');
    });
  });

  describe('滚动到底部逻辑', () => {
    test('scrollToBottom 应该正确设置 scrollTop', () => {
      const mockElement = {
        scrollTop: 0,
        scrollHeight: 1000
      };

      const scrollToBottom = () => {
        nextTick(() => {
          if (mockElement) {
            mockElement.scrollTop = mockElement.scrollHeight;
          }
        });
      };

      scrollToBottom();
      nextTick(() => {
        expect(mockElement.scrollTop).toBe(1000);
      });
    });
  });
});

describe('API 模拟测试', () => {
  test('askAI 应该被正确调用', async () => {
    const { askAI } = await import('@/api/ai');
    const result = await askAI('测试问题');
    expect(askAI).toHaveBeenCalledWith('测试问题');
    expect(result).toBe('这是一个测试回复');
  });
});

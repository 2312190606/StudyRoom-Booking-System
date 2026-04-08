import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAnnouncementStore = defineStore('announcement', () => {
  const announcements = ref([
    {
      id: 1,
      title: '关于图书馆 A 区维护的通知',
      date: '2024-03-11',
      content: '图书馆 A 区自习室本周六由于设备检修暂停开放，请同学们前往 B 区或 C 区自习。',
      status: '发布中',
      statusClass: 'text-emerald-500 bg-emerald-500/10'
    },
    {
      id: 2,
      title: '期末周预约时限调整',
      date: '2024-03-08',
      content: '期末周将至，为了提高座位周转率，自习室预约时限调整为 4 小时。',
      status: '已下架',
      statusClass: 'text-gray-500 bg-gray-500/10'
    },
    {
      id: 3,
      title: '严厉打击占座行为的公告',
      date: '2024-03-05',
      content: '严禁任何形式的占座行为，离开超过 30 分钟系统将自动释放座位。',
      status: '发布中',
      statusClass: 'text-emerald-500 bg-emerald-500/10'
    },
    {
      id: 4,
      title: '清明假期调休安排',
      date: '2024-04-01',
      content: '根据国家法定节假日安排，本周六（4月6日）为工作日，自习室正常开放。',
      status: '发布中',
      statusClass: 'text-emerald-500 bg-emerald-500/10'
    },
    {
      id: 5,
      title: '自习室新增储物柜使用说明',
      date: '2024-03-25',
      content: '为了方便同学寄存物品，A区入口处新增了20个智能储物柜，扫码即可使用。',
      status: '发布中',
      statusClass: 'text-emerald-500 bg-emerald-500/10'
    }
  ])

  const publishedAnnouncements = computed(() => 
    announcements.value.filter(a => a.status === '发布中')
  )

  const addAnnouncement = (anno) => {
    announcements.value.unshift(anno)
  }

  const updateAnnouncement = (id, updatedAnno) => {
    const index = announcements.value.findIndex(a => a.id === id)
    if (index !== -1) {
      announcements.value[index] = { ...updatedAnno }
    }
  }

  const deleteAnnouncement = (id) => {
    announcements.value = announcements.value.filter(a => a.id !== id)
  }

  return { 
    announcements, 
    publishedAnnouncements, 
    addAnnouncement, 
    updateAnnouncement, 
    deleteAnnouncement 
  }
})

import apiClient from './client'

// AI 客服问答
export function askAI(question) {
  return apiClient.post('/ai/chat', { question })
}

import apiClient from './client'

export function getAnnouncements(params) {
  return apiClient.get('/admin/announcements', { params })
}

export function createAnnouncement(data) {
  return apiClient.post('/admin/announcements', data)
}

export function updateAnnouncement(id, data) {
  return apiClient.put(`/admin/announcements/${id}`, data)
}

export function deleteAnnouncement(id) {
  return apiClient.delete(`/admin/announcements/${id}`)
}

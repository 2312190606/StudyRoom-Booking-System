package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.mapper.*;
import com.example.studyroom.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务实现类
 */
@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudyRoomMapper studyRoomMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private CarouselMapper carouselMapper;

    /**
     * 获取仪表盘统计数据
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 今日预约量
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Long todayReservations = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                .ge(Reservation::getCreatedAt, todayStart));
        
        // 用户总数
        Long totalUsers = userMapper.selectCount(null);
        
        // 自习室总数
        Long totalRooms = studyRoomMapper.selectCount(null);
        
        // 座位总数
        Long totalSeats = seatMapper.selectCount(null);

        stats.put("todayReservations", todayReservations);
        stats.put("totalUsers", totalUsers);
        stats.put("totalRooms", totalRooms);
        stats.put("totalSeats", totalSeats);
        
        return stats;
    }

    /**
     * 获取自习室列表 (后台管理用)
     */
    public List<StudyRoom> getAllRooms() {
        return studyRoomMapper.selectList(null);
    }

    /**
     * 新增自习室
     */
    public void addRoom(StudyRoom room) {
        studyRoomMapper.insert(room);
    }

    /**
     * 更新自习室信息
     */
    public void updateRoom(StudyRoom room) {
        studyRoomMapper.updateById(room);
    }

    /**
     * 删除自习室
     */
    public void deleteRoom(Long id) {
        studyRoomMapper.deleteById(id);
        // 同时删除该自习室下的所有座位
        seatMapper.delete(new LambdaQueryWrapper<Seat>().eq(Seat::getRoomId, id));
    }

    /**
     * 批量新增座位
     */
    public void batchAddSeats(Long roomId, List<Seat> seats) {
        for (Seat seat : seats) {
            seat.setRoomId(roomId);
            seatMapper.insert(seat);
        }
    }

    /**
     * 修改单个座位属性
     */
    public void updateSeat(Seat seat) {
        seatMapper.updateById(seat);
    }

    /**
     * 批量删除座位
     */
    public void batchDeleteSeats(List<Long> ids) {
        seatMapper.deleteBatchIds(ids);
    }

    /**
     * 批量修改座位状态
     */
    public void batchUpdateSeatStatus(List<Long> ids, Integer status) {
        for (Long id : ids) {
            Seat seat = new Seat();
            seat.setId(id);
            seat.setStatus(status);
            seatMapper.updateById(seat);
        }
    }

    /**
     * 获取所有预约记录
     */
    public List<Reservation> getAllReservations() {
        return reservationMapper.selectList(new LambdaQueryWrapper<Reservation>()
                .orderByDesc(Reservation::getCreatedAt));
    }

    /**
     * 后台强制取消预约
     */
    public void adminCancelReservation(Long id) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(0);
        reservationMapper.updateById(reservation);
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    /**
     * 获取系统内容 (公告与轮播图)
     */
    public List<Announcement> getAllAnnouncements() {
        return announcementMapper.selectList(null);
    }

    public void addAnnouncement(Announcement announcement) {
        announcementMapper.insert(announcement);
    }

    public void updateAnnouncement(Announcement announcement) {
        announcementMapper.updateById(announcement);
    }

    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    public List<Carousel> getAllCarousels() {
        return carouselMapper.selectList(null);
    }

    public void addCarousel(Carousel carousel) {
        carouselMapper.insert(carousel);
    }

    public void deleteCarousel(Long id) {
        carouselMapper.deleteById(id);
    }

    private Map<String, Object> systemConfig = new HashMap<>();

    public Map<String, Object> getConfig() {
        if (systemConfig.isEmpty()) {
            systemConfig.put("minDuration", 60);
            systemConfig.put("maxDuration", 240);
            systemConfig.put("allowCancelBefore", 30);
        }
        return systemConfig;
    }

    public void updateConfig(Map<String, Object> config) {
        systemConfig.putAll(config);
    }
}

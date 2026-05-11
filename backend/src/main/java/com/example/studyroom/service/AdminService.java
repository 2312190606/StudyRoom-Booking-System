package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.mapper.*;
import com.example.studyroom.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private PasswordEncoder passwordEncoder;

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
     * 获取自习室利用率
     */
    public Map<String, Object> getDashboardUtilization() {
        Map<String, Object> utilization = new HashMap<>();

        // 各自习室的预约情况
        List<StudyRoom> rooms = studyRoomMapper.selectList(null);
        for (StudyRoom room : rooms) {
            Long seatCount = seatMapper.selectCount(new LambdaQueryWrapper<Seat>().eq(Seat::getRoomId, room.getId()));
            Long reservedCount = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                    .eq(Reservation::getSeatId, room.getId())
                    .ne(Reservation::getStatus, 0));

            Map<String, Object> roomData = new HashMap<>();
            roomData.put("seatCount", seatCount);
            roomData.put("reservedCount", reservedCount);
            roomData.put("utilizationRate", seatCount > 0 ? (double) reservedCount / seatCount * 100 : 0);

            utilization.put(room.getName(), roomData);
        }

        return utilization;
    }

    /**
     * 获取用户增长趋势
     */
    public List<Map<String, Object>> getDashboardUsersTrend() {
        List<Map<String, Object>> trend = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();

        // 最近7天用户注册趋势
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .ge(User::getCreatedAt, dayStart)
                    .lt(User::getCreatedAt, dayEnd));

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("count", count);
            trend.add(dayData);
        }

        return trend;
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
        // 计算并设置总座位数和可用座位数
        Integer rows = room.getSeatRows() != null ? room.getSeatRows() : 5;
        Integer cols = room.getCols() != null ? room.getCols() : 8;
        int totalSeats = rows * cols;
        room.setTotalSeats(totalSeats);
        room.setAvailableSeats(totalSeats);
        studyRoomMapper.insert(room);
        // 自动创建座位记录
        int seatNo = 1;
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                Seat seat = new Seat();
                seat.setRoomId(room.getId());
                seat.setSeatNumber(String.valueOf(seatNo++));
                seat.setPositionX(r);
                seat.setPositionY(c);
                seat.setHasPower(false);
                seat.setIsWindow(false);
                seat.setStatus(1); // 默认可用
                seatMapper.insert(seat);
            }
        }
    }

/**
     * 更新自习室信息
     */
    public void updateRoom(StudyRoom room) {
        StudyRoom existing = studyRoomMapper.selectById(room.getId());
        if (existing != null) {
            Integer newRows = room.getSeatRows() != null ? room.getSeatRows() : existing.getSeatRows();
            Integer newCols = room.getCols() != null ? room.getCols() : existing.getCols();
            room.setTotalSeats(newRows * newCols);

            // 始终处理维修座位，过滤超出范围的
            String existingMaintenance = existing.getMaintenanceSeats();
            String newMaintenance = room.getMaintenanceSeats();

            // 如果有新值，使用新值；否则过滤旧值
            if (newMaintenance != null && !newMaintenance.isEmpty() && !newMaintenance.equals("[]")) {
                // 前端传了新的维修座位，直接使用
            } else {
                // 过滤超出范围的旧维修座位
                if (existingMaintenance != null && !existingMaintenance.isEmpty()) {
                    StringBuilder filtered = new StringBuilder("[");
                    String[] positions = existingMaintenance.replace("[", "").replace("]", "").replace("\"", "").split(",");
                    int count = 0;
                    for (String pos : positions) {
                        pos = pos.trim();
                        if (pos.isEmpty()) continue;
                        String[] parts = pos.split("-");
                        if (parts.length == 2) {
                            try {
                                int r = Integer.parseInt(parts[0]);
                                int c = Integer.parseInt(parts[1]);
                                if (r <= newRows && c <= newCols) {
                                    if (count > 0) filtered.append(",");
                                    filtered.append("\"").append(pos).append("\"");
                                    count++;
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    filtered.append("]");
                    room.setMaintenanceSeats(filtered.toString());
                } else {
                    room.setMaintenanceSeats("[]");
                }
            }
        }
        studyRoomMapper.updateById(room);

        // 重新计算可用座位数
        if (room.getId() != null) {
            StudyRoom updated = studyRoomMapper.selectById(room.getId());
            int maintenanceCount = 0;
            String maintenance = updated.getMaintenanceSeats();
            if (maintenance != null && !maintenance.isEmpty()) {
                String[] positions = maintenance.replace("[", "").replace("]", "").replace("\"", "").split(",");
                for (String pos : positions) {
                    if (!pos.trim().isEmpty()) maintenanceCount++;
                }
            }
            Integer rows = updated.getSeatRows() != null ? updated.getSeatRows() : 5;
            Integer cols = updated.getCols() != null ? updated.getCols() : 8;
            StudyRoom updateRoom = new StudyRoom();
            updateRoom.setId(room.getId());
            updateRoom.setAvailableSeats(rows * cols - maintenanceCount);
            studyRoomMapper.updateById(updateRoom);
        }
    }

    /**
     * 更新维修座位列表
     */
    public void updateMaintenanceSeats(Long roomId, String maintenanceSeats) {
        StudyRoom room = studyRoomMapper.selectById(roomId);
        if (room == null) return;

        // 解析维修位置，计算数量
        int maintenanceCount = 0;
        if (maintenanceSeats != null && !maintenanceSeats.isEmpty()) {
            String[] positions = maintenanceSeats.replace("[", "").replace("]", "").replace("\"", "").split(",");
            for (String pos : positions) {
                if (!pos.trim().isEmpty()) maintenanceCount++;
            }
        }

        Integer rows = room.getSeatRows() != null ? room.getSeatRows() : 5;
        Integer cols = room.getCols() != null ? room.getCols() : 8;

        StudyRoom updateRoom = new StudyRoom();
        updateRoom.setId(roomId);
        updateRoom.setMaintenanceSeats(maintenanceSeats);
        updateRoom.setAvailableSeats(rows * cols - maintenanceCount);
        studyRoomMapper.updateById(updateRoom);
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
        List<Reservation> reservations = reservationMapper.selectList(new LambdaQueryWrapper<Reservation>()
                .ne(Reservation::getStatus, -1) // 排除已删除的
                .orderByDesc(Reservation::getId));

        // 填充用户和座位信息
        for (Reservation r : reservations) {
            // 填充用户信息
            if (r.getUserId() != null) {
                User user = userMapper.selectById(r.getUserId());
                if (user != null) {
                    r.setUserName(user.getUsername());
                }
            }
            // 填充座位信息
            if (r.getSeatId() != null) {
                Seat seat = seatMapper.selectById(r.getSeatId());
                if (seat != null) {
                    r.setSeat(seat);
                    // 填充自习室名称
                    StudyRoom room = studyRoomMapper.selectById(seat.getRoomId());
                    if (room != null) {
                        r.setRoomName(room.getName());
                    }
                }
            }
        }
        return reservations;
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

    @Autowired
    private LoginAttemptService loginAttemptService;

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        users.forEach(u -> {
            u.setPassword(null);
            // 如果用户原本状态正常，则检查其是否因为连续登录失败被临时锁定
            // 若被锁定，则在列表展示中将其状态置为 0 (禁用)，方便管理员查看
            if (u.getStatus() == 1 && loginAttemptService.isLocked(null, u.getUsername())) {
                u.setStatus(0);
            }
        });
        return users;
    }

    /**
     * 创建新用户
     */
    public void createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(2); // 默认普通用户
        user.setStatus(1); // 默认正常
        userMapper.insert(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long id, Integer status) {
        User targetUser = userMapper.selectById(id);
        // 如果目标用户是管理员，且试图将其设置为禁用(0)，则拦截
        if (targetUser != null && targetUser.getRole() != null && targetUser.getRole() == 1 && status == 0) {
            throw new com.example.studyroom.common.BaseException("安全限制：不能禁用管理员账号");
        }

        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    /**
     * 更新用户信息（用户名、密码、角色）
     */
    public void updateUser(Long id, String username, String password, Integer role, Integer status) {
        User targetUser = userMapper.selectById(id);
        // 如果目标用户是管理员，且试图通过更新详情将其设置为禁用(0)，则拦截
        if (targetUser != null && targetUser.getRole() != null && targetUser.getRole() == 1 && status != null && status == 0) {
            throw new com.example.studyroom.common.BaseException("安全限制：不能禁用管理员账号");
        }

        User user = new User();
        user.setId(id);
        if (username != null && !username.isEmpty()) {
            user.setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        if (role != null) {
            user.setRole(role);
        }
        if (status != null) {
            user.setStatus(status);
        }
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

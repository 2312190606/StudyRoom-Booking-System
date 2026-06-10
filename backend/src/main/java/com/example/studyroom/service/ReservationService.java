package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.mapper.StudyRoomMapper;
import com.example.studyroom.model.entity.Reservation;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约服务实现类
 */
@Service
public class ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private StudyRoomMapper studyRoomMapper;

    @Autowired
    private UserService userService;

    /**
     * 提交预约申请
     */
    @Transactional
    public void createReservation(Long userId, Long seatId, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 基本校验
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BaseException("开始时间不能早于当前时间");
        }
        if (endTime.isBefore(startTime)) {
            throw new BaseException("结束时间不能早于开始时间");
        }

        // 2. 检查座位状态
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null || seat.getStatus() == 0) {
            throw new BaseException("该座位目前不可用");
        }

        // 3. 冲突检查: 检查该座位在选定时段内是否已有重叠预约
        // 重叠条件: (start1 < end2) AND (end1 > start2)
        Long count = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getSeatId, seatId)
                .notIn(Reservation::getStatus, 0, -1, 3) // 排除已取消、已删除、已完成的
                .lt(Reservation::getStartTime, endTime)
                .gt(Reservation::getEndTime, startTime));
        
        if (count > 0) {
            throw new BaseException("该时段座位已被预约");
        }

        // 4. 检查该用户是否已有活跃预约（同一用户不能同时预约多个座位）
        Long userActiveCount = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .in(Reservation::getStatus, 1, 2) // 待使用或使用中
                .lt(Reservation::getStartTime, endTime)
                .gt(Reservation::getEndTime, startTime));

        if (userActiveCount > 0) {
            throw new BaseException("您已有一个正在使用或待使用的预约，请先取消或完成当前预约后再预约新座位");
        }

        // 5. 创建预约
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSeatId(seatId);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus(1); // 待使用
        reservation.setExtended(0); // 未延后
        reservationMapper.insert(reservation);
    }

    /**
     * 获取我的预约列表
     */
    public Page<Reservation> getMyReservations(Long userId, int page, int size) {
        Page<Reservation> pageResult = reservationMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .ne(Reservation::getStatus, -1) // 排除已删除的
                .orderByDesc(Reservation::getCreatedAt));

        // 填充座位和自习室信息
        for (Reservation r : pageResult.getRecords()) {
            Seat seat = seatMapper.selectById(r.getSeatId());
            if (seat != null) {
                r.setSeat(seat);
                StudyRoom room = studyRoomMapper.selectById(seat.getRoomId());
                if (room != null) {
                    r.setRoomName(room.getName());
                    r.setLatitude(room.getLatitude());
                    r.setLongitude(room.getLongitude());
                }
            }
        }
        return pageResult;
    }

    /**
     * 获取预约详情
     */
    public Reservation getReservationById(Long id) {
        return reservationMapper.selectById(id);
    }

    /**
     * 删除预约记录（软删除）
     */
    public void deleteReservation(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new BaseException("预约不存在或无权操作");
        }
        reservation.setStatus(-1); // 已删除
        reservationMapper.updateById(reservation);
    }

    /**
     * 取消预约
     */
    public void cancelReservation(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new BaseException("预约不存在或无权操作");
        }
        if (reservation.getStatus() != 1) {
            throw new BaseException("当前状态不可取消");
        }

        // 如果已超过开始时间但未签到，算违约，扣10分
        if (LocalDateTime.now().isAfter(reservation.getStartTime())) {
            userService.deductCreditScore(userId, 10);
        }

        reservation.setStatus(0); // 已取消
        reservationMapper.updateById(reservation);
    }

    /**
     * 延长预约 (限1次，延长30分钟)
     */
    public void extendReservation(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new BaseException("预约不存在或无权操作");
        }
        if (reservation.getStatus() != 1 && reservation.getStatus() != 2) {
            throw new BaseException("当前状态不可延长");
        }
        if (reservation.getExtended() != null && reservation.getExtended() == 1) {
            throw new BaseException("已延后过一次，无法再次延后");
        }

        LocalDateTime newEndTime = reservation.getEndTime().plusMinutes(30);
        
        // 冲突检查 (排除自己当前的预约)
        Long count = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getSeatId, reservation.getSeatId())
                .ne(Reservation::getId, id)
                .notIn(Reservation::getStatus, 0, -1, 3) // 排除已取消、已删除、已完成的
                .lt(Reservation::getStartTime, newEndTime)
                .gt(Reservation::getEndTime, reservation.getEndTime()));
        
        if (count > 0) {
            throw new BaseException("延长时段与后续预约冲突");
        }

        reservation.setEndTime(newEndTime);
        reservation.setExtended(1);
        reservationMapper.updateById(reservation);
    }

    /**
     * 签到
     * @param id 预约ID
     * @param userId 用户ID
     * @param latitude 用户纬度
     * @param longitude 用户经度
     */
    public void checkIn(Long id, Long userId, Double latitude, Double longitude) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new BaseException("预约不存在或无权操作");
        }
        if (reservation.getStatus() != 1) {
            throw new BaseException("当前状态不可签到");
        }

        // 获取座位对应的自习室
        Seat seat = seatMapper.selectById(reservation.getSeatId());
        if (seat == null) {
            throw new BaseException("座位信息不存在");
        }
        StudyRoom room = studyRoomMapper.selectById(seat.getRoomId());
        if (room == null) {
            throw new BaseException("自习室信息不存在");
        }

        // 如果自习室设置了经纬度，则进行距离校验
        if (room.getLatitude() != null && room.getLongitude() != null && latitude != null && longitude != null) {
            double distance = calculateDistance(
                latitude, longitude,
                room.getLatitude(), room.getLongitude()
            );
            // 允许签到范围：200米
            if (distance > 200) {
                throw new BaseException("不在签到范围内，请到自习室附近再试");
            }
        }

        reservation.setCheckInTime(LocalDateTime.now());
        reservation.setStatus(2); // 使用中
        reservationMapper.updateById(reservation);

        // 签到成功，增加5分信誉分
        userService.addCreditScore(userId, 5);
    }

    /**
     * 计算两点之间的直线距离（单位：米）
     * 使用 Haversine 公式
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000; // 地球半径（米）
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                  Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * 结束学习
     */
    @Transactional
    public void endStudy(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new BaseException("预约不存在或无权操作");
        }
        if (reservation.getStatus() != 2) {
            throw new BaseException("当前状态不可结束");
        }

        // 释放座位（将座位状态重置为可用）
        Seat seat = seatMapper.selectById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus(1); // 可用
            seatMapper.updateById(seat);
        }

        reservation.setStatus(3); // 已完成
        reservationMapper.updateById(reservation);
    }
}

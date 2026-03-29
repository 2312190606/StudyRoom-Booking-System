package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.model.entity.Reservation;
import com.example.studyroom.model.entity.Seat;
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
                .ne(Reservation::getStatus, 0) // 排除已取消的
                .lt(Reservation::getStartTime, endTime)
                .gt(Reservation::getEndTime, startTime));
        
        if (count > 0) {
            throw new BaseException("该时段座位已被预约");
        }

        // 4. 创建预约
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSeatId(seatId);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus(1); // 待使用
        reservationMapper.insert(reservation);
    }

    /**
     * 获取我的预约列表
     */
    public List<Reservation> getMyReservations(Long userId) {
        return reservationMapper.selectList(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .orderByDesc(Reservation::getCreatedAt));
    }

    /**
     * 获取预约详情
     */
    public Reservation getReservationById(Long id) {
        return reservationMapper.selectById(id);
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
        if (reservation.getStatus() != 2) {
            throw new BaseException("只有正在使用中的预约可以延长");
        }

        LocalDateTime newEndTime = reservation.getEndTime().plusMinutes(30);
        
        // 冲突检查 (排除自己当前的预约)
        Long count = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getSeatId, reservation.getSeatId())
                .ne(Reservation::getId, id)
                .ne(Reservation::getStatus, 0)
                .lt(Reservation::getStartTime, newEndTime)
                .gt(Reservation::getEndTime, reservation.getEndTime()));
        
        if (count > 0) {
            throw new BaseException("延长时段与后续预约冲突");
        }

        reservation.setEndTime(newEndTime);
        reservationMapper.updateById(reservation);
    }

    /**
     * 签到
     */
    public void checkIn(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new BaseException("预约不存在或无权操作");
        }
        if (reservation.getStatus() != 1) {
            throw new BaseException("当前状态不可签到");
        }
        
        // 这里可以添加定位校验逻辑
        
        reservation.setCheckInTime(LocalDateTime.now());
        reservation.setStatus(2); // 使用中
        reservationMapper.updateById(reservation);
    }
}

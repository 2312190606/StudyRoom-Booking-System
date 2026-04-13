package com.example.studyroom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studyroom.common.Result;
import com.example.studyroom.model.dto.CheckInDTO;
import com.example.studyroom.model.dto.ReservationDTO;
import com.example.studyroom.model.entity.Reservation;
import com.example.studyroom.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约中心控制器
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 提交预约请求
     */
    @PostMapping
    public Result<?> createReservation(@Validated @RequestBody ReservationDTO reservationDTO) {
        reservationService.createReservation(getCurrentUserId(), reservationDTO.getSeatId(), 
                reservationDTO.getStartTime(), reservationDTO.getEndTime());
        return Result.success();
    }

    /**
     * 获取我的预约列表
     */
    @GetMapping("/me")
    public Result<Page<Reservation>> getMyReservations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reservationService.getMyReservations(getCurrentUserId(), page, size));
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public Result<Reservation> getReservation(@PathVariable Long id) {
        return Result.success(reservationService.getReservationById(id));
    }

    /**
     * 取消预约
     */
    @PutMapping("/{id}/cancel")
    public Result<?> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id, getCurrentUserId());
        return Result.success();
    }

    /**
     * 延长预约
     */
    @PutMapping("/{id}/extend")
    public Result<?> extendReservation(@PathVariable Long id) {
        reservationService.extendReservation(id, getCurrentUserId());
        return Result.success();
    }

    /**
     * 签到验证
     */
    @PostMapping("/{id}/check-in")
    public Result<?> checkIn(@PathVariable Long id, @RequestBody(required = false) CheckInDTO checkInDTO) {
        reservationService.checkIn(id, getCurrentUserId());
        return Result.success();
    }

    /**
     * 结束学习
     */
    @PutMapping("/{id}/end")
    public Result<?> endStudy(@PathVariable Long id) {
        reservationService.endStudy(id, getCurrentUserId());
        return Result.success();
    }

    /**
     * 删除预约记录
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id, getCurrentUserId());
        return Result.success();
    }
}

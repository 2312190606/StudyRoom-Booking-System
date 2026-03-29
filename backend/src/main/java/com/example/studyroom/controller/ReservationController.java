package com.example.studyroom.controller;

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
    public Result<List<Reservation>> getMyReservations() {
        return Result.success(reservationService.getMyReservations(getCurrentUserId()));
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
}

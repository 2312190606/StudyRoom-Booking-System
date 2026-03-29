package com.example.studyroom.controller.admin;

import com.example.studyroom.common.Result;
import com.example.studyroom.model.entity.Reservation;
import com.example.studyroom.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员预约记录管理控制器
 */
@RestController
@RequestMapping("/api/admin/reservations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReservationController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public Result<List<Reservation>> getAllReservations() {
        return Result.success(adminService.getAllReservations());
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancelReservation(@PathVariable Long id) {
        adminService.adminCancelReservation(id);
        return Result.success();
    }
}

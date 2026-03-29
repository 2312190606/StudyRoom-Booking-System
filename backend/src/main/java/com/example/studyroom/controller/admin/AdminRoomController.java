package com.example.studyroom.controller.admin;

import com.example.studyroom.common.Result;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import com.example.studyroom.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员自习室与座位管理控制器
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {

    @Autowired
    private AdminService adminService;

    // --- 自习室管理 ---

    @GetMapping("/rooms")
    public Result<List<StudyRoom>> getRooms() {
        return Result.success(adminService.getAllRooms());
    }

    @PostMapping("/rooms")
    public Result<?> addRoom(@RequestBody StudyRoom room) {
        adminService.addRoom(room);
        return Result.success();
    }

    @PutMapping("/rooms/{id}")
    public Result<?> updateRoom(@PathVariable Long id, @RequestBody StudyRoom room) {
        room.setId(id);
        adminService.updateRoom(room);
        return Result.success();
    }

    @DeleteMapping("/rooms/{id}")
    public Result<?> deleteRoom(@PathVariable Long id) {
        adminService.deleteRoom(id);
        return Result.success();
    }

    // --- 座位管理 ---

    @PostMapping("/rooms/{id}/seats/batch")
    public Result<?> batchAddSeats(@PathVariable Long id, @RequestBody List<Seat> seats) {
        adminService.batchAddSeats(id, seats);
        return Result.success();
    }

    @PutMapping("/seats/{id}")
    public Result<?> updateSeat(@PathVariable Long id, @RequestBody Seat seat) {
        seat.setId(id);
        adminService.updateSeat(seat);
        return Result.success();
    }

    @DeleteMapping("/seats")
    public Result<?> batchDeleteSeats(@RequestBody List<Long> ids) {
        adminService.batchDeleteSeats(ids);
        return Result.success();
    }

    @PutMapping("/seats/status")
    @SuppressWarnings("unchecked")
    public Result<?> batchUpdateSeatStatus(@RequestBody Map<String, Object> params) {
        List<Long> ids = (List<Long>) params.get("ids");
        Integer status = (Integer) params.get("status");
        adminService.batchUpdateSeatStatus(ids, status);
        return Result.success();
    }
}

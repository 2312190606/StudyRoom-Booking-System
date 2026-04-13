package com.example.studyroom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studyroom.common.Result;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import com.example.studyroom.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 自习室与座位控制器
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * 分页获取自习室列表
     */
    @GetMapping
    public Result<Page<StudyRoom>> getRooms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer floor) {
        return Result.success(roomService.getRooms(page, size, location, floor));
    }

    /**
     * 获取自习室详情
     */
    @GetMapping("/{id}")
    public Result<StudyRoom> getRoom(@PathVariable Long id) {
        return Result.success(roomService.getRoomById(id));
    }

    /**
     * 获取自习室座位视图
     */
    @GetMapping("/{id}/seats")
    public Result<List<Seat>> getSeats(@PathVariable Long id) {
        return Result.success(roomService.getSeatsByRoomId(id));
    }
}

package com.example.studyroom.controller.admin;

import com.example.studyroom.common.Result;
import com.example.studyroom.model.entity.Announcement;
import com.example.studyroom.model.entity.Carousel;
import com.example.studyroom.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员内容管理控制器 (公告与轮播图)
 */
@RestController
@RequestMapping("/api/admin/content")
@PreAuthorize("hasRole('ADMIN')")
public class AdminContentController {

    @Autowired
    private AdminService adminService;

    // --- 公告管理 ---

    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        return Result.success(adminService.getAllAnnouncements());
    }

    @PostMapping("/announcements")
    public Result<?> addAnnouncement(@RequestBody Announcement announcement) {
        adminService.addAnnouncement(announcement);
        return Result.success();
    }

    @PutMapping("/announcements/{id}")
    public Result<?> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        adminService.updateAnnouncement(announcement);
        return Result.success();
    }

    @DeleteMapping("/announcements/{id}")
    public Result<?> deleteAnnouncement(@PathVariable Long id) {
        adminService.deleteAnnouncement(id);
        return Result.success();
    }

    // --- 轮播图管理 ---

    @GetMapping("/carousels")
    public Result<List<Carousel>> getCarousels() {
        return Result.success(adminService.getAllCarousels());
    }

    @PostMapping("/carousels")
    public Result<?> addCarousel(@RequestBody Carousel carousel) {
        adminService.addCarousel(carousel);
        return Result.success();
    }

    @DeleteMapping("/carousels/{id}")
    public Result<?> deleteCarousel(@PathVariable Long id) {
        adminService.deleteCarousel(id);
        return Result.success();
    }
}

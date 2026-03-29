package com.example.studyroom.controller;

import com.example.studyroom.common.Result;
import com.example.studyroom.model.entity.Announcement;
import com.example.studyroom.model.entity.Carousel;
import com.example.studyroom.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公共信息控制器
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private PublicService publicService;

    /**
     * 获取首页轮播图
     */
    @GetMapping("/carousels")
    public Result<List<Carousel>> getCarousels() {
        return Result.success(publicService.getCarousels());
    }

    /**
     * 获取系统公告列表
     */
    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        return Result.success(publicService.getAnnouncements());
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/announcements/{id}")
    public Result<Announcement> getAnnouncement(@PathVariable Long id) {
        return Result.success(publicService.getAnnouncementById(id));
    }
}

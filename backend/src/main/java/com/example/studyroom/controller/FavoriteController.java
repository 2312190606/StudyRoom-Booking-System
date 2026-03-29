package com.example.studyroom.controller;

import com.example.studyroom.common.Result;
import com.example.studyroom.model.entity.Favorite;
import com.example.studyroom.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 常用座位控制器
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 获取常用座位列表
     */
    @GetMapping
    public Result<List<Favorite>> getFavorites() {
        return Result.success(favoriteService.getMyFavorites(getCurrentUserId()));
    }

    /**
     * 添加常用座位
     */
    @PostMapping
    public Result<?> addFavorite(@RequestBody Map<String, Long> params) {
        favoriteService.addFavorite(getCurrentUserId(), params.get("seatId"));
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id, getCurrentUserId());
        return Result.success();
    }

    /**
     * 一键预约
     */
    @PostMapping("/{id}/quick-reserve")
    public Result<?> quickReserve(@PathVariable Long id) {
        favoriteService.quickReserve(id, getCurrentUserId());
        return Result.success();
    }
}

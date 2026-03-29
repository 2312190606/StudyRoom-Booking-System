package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.FavoriteMapper;
import com.example.studyroom.model.entity.Favorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 常用座位服务实现类
 */
@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ReservationService reservationService;

    /**
     * 获取常用座位列表
     */
    public List<Favorite> getMyFavorites(Long userId) {
        return favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId));
    }

    /**
     * 添加常用座位
     */
    public void addFavorite(Long userId, Long seatId) {
        // 检查是否已收藏
        Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getSeatId, seatId));
        if (count > 0) {
            throw new BaseException("该座位已在收藏列表中");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setSeatId(seatId);
        favoriteMapper.insert(favorite);
    }

    /**
     * 取消收藏
     */
    public void deleteFavorite(Long id, Long userId) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getId, id)
                .eq(Favorite::getUserId, userId));
    }

    /**
     * 一键预约
     */
    public void quickReserve(Long id, Long userId) {
        Favorite favorite = favoriteMapper.selectById(id);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            throw new BaseException("收藏记录不存在");
        }
        
        // 默认预约当前时间后1小时
        LocalDateTime now = LocalDateTime.now();
        reservationService.createReservation(userId, favorite.getSeatId(), now, now.plusHours(1));
    }
}

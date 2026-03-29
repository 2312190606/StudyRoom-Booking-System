package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.mapper.AnnouncementMapper;
import com.example.studyroom.mapper.CarouselMapper;
import com.example.studyroom.model.entity.Announcement;
import com.example.studyroom.model.entity.Carousel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公共信息服务实现类
 */
@Service
public class PublicService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    /**
     * 获取活跃的轮播图列表
     */
    public List<Carousel> getCarousels() {
        return carouselMapper.selectList(new LambdaQueryWrapper<Carousel>()
                .eq(Carousel::getStatus, 1)
                .orderByAsc(Carousel::getSortOrder));
    }

    /**
     * 获取发布的公告列表
     */
    public List<Announcement> getAnnouncements() {
        return announcementMapper.selectList(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, 1)
                .orderByDesc(Announcement::getCreatedAt));
    }

    /**
     * 获取公告详情
     */
    public Announcement getAnnouncementById(Long id) {
        return announcementMapper.selectById(id);
    }
}

package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 轮播图实体类
 */
@Data
@TableName("carousels")
public class Carousel {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String imageUrl;
    
    private Integer sortOrder;
    
    /**
     * 启用状态 (0: 禁用, 1: 启用)
     */
    private Integer status;
}

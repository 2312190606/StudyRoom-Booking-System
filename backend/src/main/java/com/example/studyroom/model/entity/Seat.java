package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 座位实体类
 */
@Data
@TableName("seats")
public class Seat {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long roomId;
    
    private String seatNumber;
    
    private Integer positionX;
    
    private Integer positionY;
    
    private Boolean hasPower;
    
    private Boolean isWindow;
    
    /**
     * 当前状态 (0: 维修, 1: 可用, 2: 占用)
     */
    private Integer status;
}

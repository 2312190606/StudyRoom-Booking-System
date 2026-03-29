package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@TableName("reservations")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long seatId;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private LocalDateTime checkInTime;
    
    /**
     * 状态 (0:取消, 1:待使用, 2:使用中, 3:已完成, 4:违约)
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 违约记录实体类
 */
@Data
@TableName("violations")
public class Violation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long reservationId;
    
    private String reason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

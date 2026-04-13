package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime endTime;

    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime checkInTime;

    /**
     * 状态 (0:取消, 1:待使用, 2:使用中, 3:已完成, 4:违约)
     */
    private Integer status;

    /**
     * 是否已延后 (0:未延后, 1:已延后)
     */
    private Integer extended;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime createdAt;

    // 关联的座位信息（非数据库字段）
    @TableField(exist = false)
    private Seat seat;

    // 关联的自习室名称（非数据库字段）
    @TableField(exist = false)
    private String roomName;

    // 关联的用户名（非数据库字段）
    @TableField(exist = false)
    private String userName;
}

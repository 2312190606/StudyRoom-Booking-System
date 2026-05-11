package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录失败记录实体
 */
@Data
@TableName("login_attempts")
public class LoginAttempt {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名或手机号
     */
    private String account;

    /**
     * IP 地址
     */
    private String ip;

    /**
     * 失败次数
     */
    private Integer failCount;

    /**
     * 锁定截止时间
     */
    private LocalDateTime lockUntil;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;

    private String password;

    private String avatar;
    
    private String phone;
    
    private String email;

    /**
     * 角色 (1: 管理员, 2: 普通用户)
     */
    private Integer role;
    
    /**
     * 状态 (0: 禁用, 1: 正常)
     */
    private Integer status;

    /**
     * 信誉分 (初始100, 低于60禁用用户)
     */
    @TableField("credit_score")
    private Integer creditScore;

    /**
     * 上次信誉增加时间（用于每日增加判断）
     */
    @TableField("last_credit_time")
    private LocalDateTime lastCreditTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

package com.example.studyroom.model.dto;

import lombok.Data;

/**
 * 用户资料更新对象
 */
@Data
public class UserProfileDTO {
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
}

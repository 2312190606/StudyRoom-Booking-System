package com.example.studyroom.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码更新单对象
 */
@Data
public class PasswordUpdateDTO {
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

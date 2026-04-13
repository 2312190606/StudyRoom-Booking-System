package com.example.studyroom.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 注册数据传输对象
 */
@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}

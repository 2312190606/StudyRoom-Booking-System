package com.example.studyroom.controller;

import com.example.studyroom.common.Result;
import com.example.studyroom.model.dto.LoginDTO;
import com.example.studyroom.model.dto.RegisterDTO;
import com.example.studyroom.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@Validated @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO.getUsername(), registerDTO.getPassword());
        return Result.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<String> login(@Validated @RequestBody LoginDTO loginDTO) {
        String token = authService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(token);
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return Result.success(authService.refreshToken(token));
    }
}

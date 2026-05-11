package com.example.studyroom.controller;

import com.example.studyroom.common.BaseException;
import com.example.studyroom.common.Result;
import com.example.studyroom.model.dto.LoginDTO;
import com.example.studyroom.model.dto.RegisterDTO;
import com.example.studyroom.service.AuthService;
import com.example.studyroom.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private LoginAttemptService loginAttemptService;

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
    public Result<String> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String username = loginDTO.getUsername();
        String ip = getClientIp(request);

        // 检查是否被锁定
        if (loginAttemptService.isLocked(ip, username)) {
            long remainingMinutes = loginAttemptService.getRemainingLockMinutes(ip, username);
            throw new BaseException("登录失败次数过多，请 " + remainingMinutes + " 分钟后再试");
        }

        try {
            String token = authService.login(username, loginDTO.getPassword());
            // 登录成功，清除失败记录
            loginAttemptService.clearFailedAttempts(ip, username);
            return Result.success(token);
        } catch (BaseException e) {
            // 登录失败，记录失败次数
            loginAttemptService.recordFailedAttempt(ip, username);
            throw e;
        }
    }

    /**
     * 获取客户端真实 IP（支持代理）
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
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

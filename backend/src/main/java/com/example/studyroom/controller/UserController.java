package com.example.studyroom.controller;

import com.example.studyroom.common.Result;
import com.example.studyroom.model.dto.PasswordUpdateDTO;
import com.example.studyroom.model.dto.UserProfileDTO;
import com.example.studyroom.model.entity.StudyTimeStat;
import com.example.studyroom.model.entity.User;
import com.example.studyroom.model.entity.Violation;
import com.example.studyroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 个人中心控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<User> getProfile() {
        return Result.success(userService.getProfile(getCurrentUserId()));
    }

    /**
     * 修改个人资料
     */
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody UserProfileDTO profileDTO) {
        userService.updateProfile(getCurrentUserId(), profileDTO.getNickname(), 
                profileDTO.getAvatar(), profileDTO.getPhone(), profileDTO.getEmail());
        return Result.success();
    }

    /**
     * 修改登录密码
     */
    @PutMapping("/password")
    public Result<?> updatePassword(@Validated @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        userService.updatePassword(getCurrentUserId(), passwordUpdateDTO.getOldPassword(), 
                passwordUpdateDTO.getNewPassword());
        return Result.success();
    }

    /**
     * 获取学习时长统计
     */
    @GetMapping("/stats")
    public Result<List<StudyTimeStat>> getStats() {
        return Result.success(userService.getStats(getCurrentUserId()));
    }

    /**
     * 获取违约记录列表
     */
    @GetMapping("/violations")
    public Result<List<Violation>> getViolations() {
        return Result.success(userService.getViolations(getCurrentUserId()));
    }
}

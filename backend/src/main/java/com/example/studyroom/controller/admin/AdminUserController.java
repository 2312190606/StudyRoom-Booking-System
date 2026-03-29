package com.example.studyroom.controller.admin;

import com.example.studyroom.common.Result;
import com.example.studyroom.model.entity.User;
import com.example.studyroom.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(adminService.getAllUsers());
    }

    @PutMapping("/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        adminService.updateUserStatus(id, params.get("status"));
        return Result.success();
    }
}

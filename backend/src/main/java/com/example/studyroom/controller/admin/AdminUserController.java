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

    /**
     * 创建新用户
     */
    @PostMapping
    public Result<?> createUser(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        adminService.createUser(username, password);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        adminService.updateUserStatus(id, params.get("status"));
        return Result.success();
    }

    /**
     * 更新用户信息（用户名、密码、角色）
     */
    @PutMapping("/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        Integer role = (Integer) params.get("role");
        Integer status = (Integer) params.get("status");
        adminService.updateUser(id, username, password, role, status);
        return Result.success();
    }
}

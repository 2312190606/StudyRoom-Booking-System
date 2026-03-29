package com.example.studyroom.controller.admin;

import com.example.studyroom.common.Result;
import com.example.studyroom.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员系统配置控制器
 */
@RestController
@RequestMapping("/api/admin/config")
@PreAuthorize("hasRole('ADMIN')")
public class AdminConfigController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public Result<Map<String, Object>> getConfig() {
        return Result.success(adminService.getConfig());
    }

    @PutMapping
    public Result<?> updateConfig(@RequestBody Map<String, Object> config) {
        adminService.updateConfig(config);
        return Result.success();
    }
}

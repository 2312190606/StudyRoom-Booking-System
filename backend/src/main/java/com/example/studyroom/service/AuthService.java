package com.example.studyroom.service;

import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.model.entity.User;
import com.example.studyroom.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户注册
     */
    public void register(String username, String password) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (existingUser != null) {
            throw new BaseException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(2); // 默认普通用户
        user.setStatus(1); // 默认正常
        userMapper.insert(user);
    }

    /**
     * 用户登录（支持用户名或手机号登录）
     */
    public String login(String username, String password) {
        // 先按用户名查找，找不到则按手机号查找
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, username));
        }
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BaseException("账号已被禁用");
        }

        // 生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());

        return jwtUtils.createToken(claims);
    }

    /**
     * 刷新 Token
     */
    public String refreshToken(String token) {
        // 解析旧 token 获取用户信息
        Claims claims = jwtUtils.parseToken(token);
        Long userId = claims.get("userId", Long.class);

        // 查询用户最新状态
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BaseException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new BaseException("账号已被禁用");
        }

        // 生成新 token
        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("userId", user.getId());
        newClaims.put("role", user.getRole());

        return jwtUtils.createToken(newClaims);
    }
}

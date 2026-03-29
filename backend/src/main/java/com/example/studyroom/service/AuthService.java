package com.example.studyroom.service;

import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.model.entity.User;
import com.example.studyroom.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
     * 用户登录
     */
    public String login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
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
}

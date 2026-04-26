package com.example.studyroom.service;

import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.model.entity.User;
import com.example.studyroom.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(2);
        testUser.setStatus(1);
    }

    /**
     * 测试注册 - 成功
     */
    @Test
    void register_Success() {
        when(userMapper.selectOne(any())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        assertDoesNotThrow(() -> authService.register("newuser", "password123"));

        verify(userMapper).insert(any(User.class));
    }

    /**
     * 测试注册 - 用户名已存在
     */
    @Test
    void register_UsernameExists() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        BaseException exception = assertThrows(BaseException.class,
                () -> authService.register("testuser", "password123"));

        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    /**
     * 测试登录 - 成功
     */
    @Test
    void login_Success() {
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.createToken(anyMap())).thenReturn("jwt-token");

        String token = authService.login("testuser", "password123");

        assertEquals("jwt-token", token);
    }

    /**
     * 测试登录 - 用户名不存在
     */
    @Test
    void login_UserNotFound() {
        when(userMapper.selectOne(any())).thenReturn(null);

        BaseException exception = assertThrows(BaseException.class,
                () -> authService.login("nonexistent", "password123"));

        assertEquals("用户名或密码错误", exception.getMessage());
    }

    /**
     * 测试登录 - 密码错误
     */
    @Test
    void login_WrongPassword() {
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        BaseException exception = assertThrows(BaseException.class,
                () -> authService.login("testuser", "wrongpassword"));

        assertEquals("用户名或密码错误", exception.getMessage());
    }

    /**
     * 测试登录 - 账号被禁用
     */
    @Test
    void login_AccountDisabled() {
        testUser.setStatus(0);
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        BaseException exception = assertThrows(BaseException.class,
                () -> authService.login("testuser", "password123"));

        assertEquals("账号已被禁用", exception.getMessage());
    }

    /**
     * 测试刷新 Token - 成功
     */
    @Test
    void refreshToken_Success() {
        // JwtUtils.parseToken returns Claims, not Map
        when(jwtUtils.parseToken(anyString())).thenReturn(mock(Claims.class));
        when(userMapper.selectById(any())).thenReturn(testUser);
        when(jwtUtils.createToken(anyMap())).thenReturn("new-jwt-token");

        String newToken = authService.refreshToken("old-token");

        assertEquals("new-jwt-token", newToken);
    }

    /**
     * 测试刷新 Token - 用户不存在
     */
    @Test
    void refreshToken_UserNotFound() {
        when(jwtUtils.parseToken(anyString())).thenReturn(mock(Claims.class));
        when(userMapper.selectById(any())).thenReturn(null);

        assertThrows(BaseException.class, () -> authService.refreshToken("old-token"));
    }
}
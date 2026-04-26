package com.example.studyroom.service;

import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.StudyTimeStatMapper;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.mapper.ViolationMapper;
import com.example.studyroom.model.entity.StudyTimeStat;
import com.example.studyroom.model.entity.User;
import com.example.studyroom.model.entity.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private ViolationMapper violationMapper;

    @Mock
    private StudyTimeStatMapper studyTimeStatMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setCreditScore(80);
        testUser.setStatus(1);
    }

    /**
     * 测试获取用户信息 - 成功
     */
    @Test
    void getProfile_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        User result = userService.getProfile(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword()); // 密码应该被隐藏
    }

    /**
     * 测试更新用户资料 - 成功
     */
    @Test
    void updateProfile_Success() {
        assertDoesNotThrow(() -> userService.updateProfile(1L, "newusername", "123456789"));

        verify(userMapper).updateById(any(User.class));
    }

    /**
     * 测试更新用户资料 - 空字段不更新
     */
    @Test
    void updateProfile_EmptyFields() {
        assertDoesNotThrow(() -> userService.updateProfile(1L, "", null));

        verify(userMapper).updateById(any(User.class));
    }

    /**
     * 测试修改密码 - 成功
     */
    @Test
    void updatePassword_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("oldpassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        assertDoesNotThrow(() -> userService.updatePassword(1L, "oldpassword", "newpassword"));

        verify(userMapper).updateById(any(User.class));
    }

    /**
     * 测试修改密码 - 原密码错误
     */
    @Test
    void updatePassword_WrongOldPassword() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        BaseException exception = assertThrows(BaseException.class,
                () -> userService.updatePassword(1L, "wrongpassword", "newpassword"));

        assertEquals("原密码错误", exception.getMessage());
    }

    /**
     * 测试获取违约记录
     */
    @Test
    void getViolations_Success() {
        List<Violation> violations = Arrays.asList(new Violation(), new Violation());
        when(violationMapper.selectList(any())).thenReturn(violations);

        List<Violation> result = userService.getViolations(1L);

        assertEquals(2, result.size());
    }

    /**
     * 测试获取学习统计数据
     */
    @Test
    void getStats_Success() {
        List<StudyTimeStat> stats = Arrays.asList(new StudyTimeStat(), new StudyTimeStat());
        when(studyTimeStatMapper.selectList(any())).thenReturn(stats);

        List<StudyTimeStat> result = userService.getStats(1L);

        assertEquals(2, result.size());
    }

    /**
     * 测试增加信誉分 - 未超过上限
     */
    @Test
    void addCreditScore_UnderLimit() {
        testUser.setCreditScore(90);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.addCreditScore(1L, 10);

        verify(userMapper).updateById(argThat(user -> user.getCreditScore() == 100));
    }

    /**
     * 测试增加信誉分 - 超过上限
     */
    @Test
    void addCreditScore_ExceedLimit() {
        testUser.setCreditScore(95);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.addCreditScore(1L, 10);

        verify(userMapper).updateById(argThat(user -> user.getCreditScore() == 100));
    }

    /**
     * 测试扣除信誉分 - 正常扣除
     */
    @Test
    void deductCreditScore_Normal() {
        testUser.setCreditScore(80);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.deductCreditScore(1L, 10);

        verify(userMapper).updateById(argThat(user -> user.getCreditScore() == 70));
    }

    /**
     * 测试扣除信誉分 - 低于0设为0
     */
    @Test
    void deductCreditScore_BelowZero() {
        testUser.setCreditScore(5);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.deductCreditScore(1L, 10);

        verify(userMapper).updateById(argThat(user -> user.getCreditScore() == 0));
    }

    /**
     * 测试扣除信誉分 - 低于60分禁用账号
     */
    @Test
    void deductCreditScore_DisableAccount() {
        testUser.setCreditScore(65);
        testUser.setStatus(1);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.deductCreditScore(1L, 10);

        verify(userMapper).updateById(argThat(user ->
                user.getCreditScore() == 55 && user.getStatus() == 0));
    }

    /**
     * 测试获取用户信誉分 - 用户存在
     */
    @Test
    void getCreditScore_UserExists() {
        testUser.setCreditScore(85);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        int score = userService.getCreditScore(1L);

        assertEquals(85, score);
    }

    /**
     * 测试获取用户信誉分 - 用户不存在
     */
    @Test
    void getCreditScore_UserNotExists() {
        when(userMapper.selectById(1L)).thenReturn(null);

        int score = userService.getCreditScore(1L);

        assertEquals(100, score); // 默认满分
    }

    /**
     * 测试每日信誉分增加 - 今日已增加不重复增加
     */
    @Test
    void checkAndAddDailyCredit_AlreadyAddedToday() {
        testUser.setCreditScore(80);
        testUser.setLastCreditTime(LocalDateTime.now()); // 今天已增加
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.checkAndAddDailyCredit(1L);

        verify(userMapper, never()).updateById(any(User.class));
    }

    /**
     * 测试每日信誉分增加 - 今日未增加
     */
    @Test
    void checkAndAddDailyCredit_NotAddedToday() {
        testUser.setCreditScore(80);
        testUser.setLastCreditTime(LocalDateTime.now().minusDays(1)); // 昨天已增加
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.checkAndAddDailyCredit(1L);

        verify(userMapper).updateById(any(User.class));
    }
}
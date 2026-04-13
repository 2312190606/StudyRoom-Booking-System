package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.StudyTimeStatMapper;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.mapper.ViolationMapper;
import com.example.studyroom.model.entity.StudyTimeStat;
import com.example.studyroom.model.entity.User;
import com.example.studyroom.model.entity.Violation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ViolationMapper violationMapper;

    @Autowired
    private StudyTimeStatMapper studyTimeStatMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户信息
     */
    public User getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null); // 安全协议：隐藏密码
        }
        return user;
    }

    /**
     * 更新用户资料
     */
    public void updateProfile(Long userId, String username, String phone) {
        User user = new User();
        user.setId(userId);
        // 只更新非空字段
        if (username != null && !username.isEmpty()) {
            user.setUsername(username);
        }
        if (phone != null && !phone.isEmpty()) {
            user.setPhone(phone);
        }
        userMapper.updateById(user);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BaseException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    /**
     * 获取违约记录
     */
    public List<Violation> getViolations(Long userId) {
        return violationMapper.selectList(new LambdaQueryWrapper<Violation>()
                .eq(Violation::getUserId, userId)
                .orderByDesc(Violation::getCreatedAt));
    }

    /**
     * 获取学习统计数据
     */
    public List<StudyTimeStat> getStats(Long userId) {
        return studyTimeStatMapper.selectList(new LambdaQueryWrapper<StudyTimeStat>()
                .eq(StudyTimeStat::getUserId, userId)
                .orderByDesc(StudyTimeStat::getStatDate)
                .last("LIMIT 30")); // 获取最近30天数据
    }

    /**
     * 增加信誉分
     */
    public void addCreditScore(Long userId, int points) {
        User user = userMapper.selectById(userId);
        if (user == null) return;

        int newScore = user.getCreditScore() + points;
        if (newScore > 100) newScore = 100; // 最高100分
        user.setCreditScore(newScore);
        userMapper.updateById(user);
    }

    /**
     * 扣除信誉分
     */
    public void deductCreditScore(Long userId, int points) {
        User user = userMapper.selectById(userId);
        if (user == null) return;

        int newScore = user.getCreditScore() - points;
        if (newScore < 0) newScore = 0;
        user.setCreditScore(newScore);

        // 低于60分自动禁用
        if (newScore < 60) {
            user.setStatus(0); // 禁用
        }
        userMapper.updateById(user);
    }

    /**
     * 检查并执行每日信誉分增加
     * 每天只能增加一次
     */
    public void checkAndAddDailyCredit(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return;

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime lastCredit = user.getLastCreditTime();

        // 如果上次增加时间不是今天，则增加5分
        if (lastCredit == null || !lastCredit.toLocalDate().equals(now.toLocalDate())) {
            int newScore = Math.min(user.getCreditScore() + 5, 100);
            user.setCreditScore(newScore);
            user.setLastCreditTime(now);
            userMapper.updateById(user);
        }
    }

    /**
     * 获取用户信誉分
     */
    public int getCreditScore(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getCreditScore() : 100;
    }
}

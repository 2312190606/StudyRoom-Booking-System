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
    public void updateProfile(Long userId, String nickname, String avatar, String phone, String email) {
        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setPhone(phone);
        user.setEmail(email);
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
}

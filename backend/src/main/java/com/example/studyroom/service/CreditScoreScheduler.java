package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 信誉分定时任务
 */
@Component
public class CreditScoreScheduler {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /**
     * 每天凌晨2点检查并增加信誉分
     * 规则：每天签到一次可获得5分，最高100分
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void addDailyCreditScore() {
        // 获取所有正常状态的用户
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1));

        for (User user : users) {
            userService.checkAndAddDailyCredit(user.getId());
        }
    }
}

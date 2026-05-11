package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.mapper.LoginAttemptMapper;
import com.example.studyroom.mapper.UserMapper;
import com.example.studyroom.model.entity.LoginAttempt;
import com.example.studyroom.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 登录失败次数记录服务
 * 同一账号 5 次失败后锁定 10 分钟
 */
@Service
@Slf4j
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 10;
    private static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");

    @Autowired
    private LoginAttemptMapper loginAttemptMapper;

    /**
     * 检查是否已被锁定
     */
    public boolean isLocked(String ip, String account) {
        LocalDateTime now = LocalDateTime.now(ZONE_SHANGHAI);
        
        // 仅检查账号是否被锁定，不拦截 IP，防止误伤同一 IP 下的其他用户
        if (isKeyLocked("ACC:" + account, now)) {
            log.warn("登录拦截：账号 [{}] 处于锁定状态", account);
            return true;
        }
        
        return false;
    }

    private boolean isKeyLocked(String key, LocalDateTime now) {
        List<LoginAttempt> attempts = loginAttemptMapper.selectList(
                new LambdaQueryWrapper<LoginAttempt>()
                        .eq(LoginAttempt::getAccount, key)
                        .gt(LoginAttempt::getLockUntil, now)
                        .last("LIMIT 1")
        );
        return !attempts.isEmpty();
    }

    /**
     * 获取剩余锁定时间（分钟）
     */
    public long getRemainingLockMinutes(String ip, String account) {
        LocalDateTime now = LocalDateTime.now(ZONE_SHANGHAI);
        LoginAttempt accAttempt = getLockRecord("ACC:" + account, now);
        
        if (accAttempt == null || accAttempt.getLockUntil() == null) return 0;
        
        long minutes = java.time.Duration.between(now, accAttempt.getLockUntil()).toMinutes();
        return Math.max(0, minutes) + 1;
    }

    private LoginAttempt getLockRecord(String key, LocalDateTime now) {
        List<LoginAttempt> list = loginAttemptMapper.selectList(
                new LambdaQueryWrapper<LoginAttempt>()
                        .eq(LoginAttempt::getAccount, key)
                        .gt(LoginAttempt::getLockUntil, now)
                        .last("LIMIT 1")
        );
        return list.isEmpty() ? null : list.get(0);
    }

    @Autowired
    private UserMapper userMapper;

    /**
     * 记录登录失败，增加失败次数
     */
    public void recordFailedAttempt(String ip, String account) {
        // 检查该账号是否为管理员
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, account));
        if (user != null && user.getRole() != null && user.getRole() == 1) {
            log.info("管理员账号 [{}] 登录失败，跳过锁定计数", account);
            return;
        }

        // 仅为普通账号记录并触发锁定
        incrementAttempt("ACC:" + account, ip);
        
        log.info("记录登录失败：账号={} (IP={})", account, ip);
    }

    private void incrementAttempt(String key, String ip) {
        LocalDateTime now = LocalDateTime.now(ZONE_SHANGHAI);
        
        List<LoginAttempt> list = loginAttemptMapper.selectList(
                new LambdaQueryWrapper<LoginAttempt>()
                        .eq(LoginAttempt::getAccount, key)
                        .last("LIMIT 1")
        );

        if (list.isEmpty()) {
            LoginAttempt attempt = new LoginAttempt();
            attempt.setAccount(key);
            attempt.setIp(ip);
            attempt.setFailCount(1);
            loginAttemptMapper.insert(attempt);
        } else {
            LoginAttempt attempt = list.get(0);
            attempt.setFailCount(attempt.getFailCount() + 1);
            
            // 达到上限，设置锁定时间
            if (attempt.getFailCount() >= MAX_ATTEMPTS) {
                attempt.setLockUntil(now.plusMinutes(LOCK_MINUTES));
                log.warn("账号锁定触发：{} 连续失败达 {} 次，锁定 {} 分钟", key, attempt.getFailCount(), LOCK_MINUTES);
            }
            loginAttemptMapper.updateById(attempt);
        }
    }

    /**
     * 登录成功时清除失败记录
     */
    public void clearFailedAttempts(String ip, String account) {
        loginAttemptMapper.delete(new LambdaQueryWrapper<LoginAttempt>().eq(LoginAttempt::getAccount, "ACC:" + account));
        log.info("登录成功，清除账号锁定记录：{}", account);
    }
}




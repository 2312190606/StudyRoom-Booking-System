package com.example.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studyroom.model.entity.LoginAttempt;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录失败记录 Mapper
 */
@Mapper
public interface LoginAttemptMapper extends BaseMapper<LoginAttempt> {
}

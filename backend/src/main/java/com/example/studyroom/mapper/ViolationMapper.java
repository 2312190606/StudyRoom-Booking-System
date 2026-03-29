package com.example.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studyroom.model.entity.Violation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ViolationMapper extends BaseMapper<Violation> {
}

package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

/**
 * 学习时长统计实体类
 */
@Data
@TableName("study_time_stats")
public class StudyTimeStat {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private LocalDate statDate;
    
    private Integer totalMinutes;
}

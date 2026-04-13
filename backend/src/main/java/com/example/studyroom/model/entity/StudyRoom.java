package com.example.studyroom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalTime;

/**
 * 自习室实体类
 */
@Data
@TableName("study_rooms")
public class StudyRoom {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String location;

    private Integer floor;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private String description;

    private Integer seatRows;

    private Integer cols;

    private Integer totalSeats;

    private Integer availableSeats;

    private String image;

    /**
     * 维修中的座位位置，存储JSON格式如 ["1-1", "2-3"]
     */
    private String maintenanceSeats;

    /**
     * 运营状态 (0: 维护中/关闭, 1: 开放)
     */
    private Integer status;
}

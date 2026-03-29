package com.example.studyroom.model.dto;

import lombok.Data;

/**
 * 签到数据传输对象
 */
@Data
public class CheckInDTO {
    private Double longitude;
    private Double latitude;
    private String address;
}

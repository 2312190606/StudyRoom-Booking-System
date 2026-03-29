package com.example.studyroom.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约申请数据传输对象
 */
@Data
public class ReservationDTO {
    @NotNull(message = "座位ID不能为空")
    private Long seatId;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
}

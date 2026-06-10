package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.mapper.StudyRoomMapper;
import com.example.studyroom.model.entity.Reservation;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自习室与座位服务实现类
 */
@Service
public class RoomService {

    @Autowired
    private StudyRoomMapper studyRoomMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    /**
     * 分页查询自习室
     */
    public Page<StudyRoom> getRooms(int current, int size, String location, Integer floor) {
        Page<StudyRoom> page = new Page<>(current, size);
        LambdaQueryWrapper<StudyRoom> queryWrapper = new LambdaQueryWrapper<StudyRoom>()
                .eq(StudyRoom::getStatus, 1) // 仅开放的
                .like(StringUtils.hasText(location), StudyRoom::getLocation, location)
                .eq(floor != null, StudyRoom::getFloor, floor);
        return studyRoomMapper.selectPage(page, queryWrapper);
    }

    /**
     * 获取自习室详情
     */
    public StudyRoom getRoomById(Long id) {
        return studyRoomMapper.selectById(id);
    }

    /**
     * 获取自习室座位信息
     */
    public List<Seat> getSeatsByRoomId(Long roomId) {
        StudyRoom room = studyRoomMapper.selectById(roomId);
        List<Seat> seats = seatMapper.selectList(new LambdaQueryWrapper<Seat>()
                .eq(Seat::getRoomId, roomId)
                .orderByAsc(Seat::getPositionX, Seat::getPositionY));

        // 当前时间，用于检查座位是否被预约
        LocalDateTime now = LocalDateTime.now();

        // 解析维修座位位置集合
        String[] maintenancePositions = new String[0];
        if (room != null && room.getMaintenanceSeats() != null) {
            maintenancePositions = room.getMaintenanceSeats()
                    .replace("[", "").replace("]", "").replace("\"", "").split(",");
        }

        for (Seat seat : seats) {
            String pos = seat.getPositionX() + "-" + seat.getPositionY();
            boolean isMaintenance = false;
            for (String m : maintenancePositions) {
                if (m.trim().equals(pos)) {
                    isMaintenance = true;
                    break;
                }
            }
            if (isMaintenance) {
                seat.setStatus(0); // 维修中
            } else {
                // 检查该座位当前是否有活跃预约（正在进行中）
                Long activeCount = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getSeatId, seat.getId())
                        .in(Reservation::getStatus, 1, 2) // 待使用(1)或使用中(2)
                        .lt(Reservation::getStartTime, now.plusHours(24)) // 开始时间在24小时内
                        .gt(Reservation::getEndTime, now)); // 结束时间在当前时间之后（仍在进行）
                if (activeCount > 0) {
                    seat.setStatus(2); // 已被预约/占用
                }
            }
        }
        return seats;
    }
}

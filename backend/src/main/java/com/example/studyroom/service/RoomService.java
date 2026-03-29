package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.mapper.StudyRoomMapper;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        return seatMapper.selectList(new LambdaQueryWrapper<Seat>()
                .eq(Seat::getRoomId, roomId));
    }
}

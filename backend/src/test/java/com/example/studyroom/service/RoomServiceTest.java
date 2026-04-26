package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.mapper.StudyRoomMapper;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RoomService 单元测试
 */
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private StudyRoomMapper studyRoomMapper;

    @Mock
    private SeatMapper seatMapper;

    @InjectMocks
    private RoomService roomService;

    private StudyRoom testRoom;
    private Seat testSeat;

    @BeforeEach
    void setUp() {
        testRoom = new StudyRoom();
        testRoom.setId(1L);
        testRoom.setName("测试自习室");
        testRoom.setLocation("图书馆3楼");
        testRoom.setTotalSeats(50);
        testRoom.setStatus(1);

        testSeat = new Seat();
        testSeat.setId(1L);
        testSeat.setRoomId(1L);
        testSeat.setSeatNumber("A-01");
        testSeat.setStatus(1);
    }

    /**
     * 测试分页获取自习室
     */
    @Test
    void getRooms_Success() {
        Page<StudyRoom> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testRoom));
        when(studyRoomMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<StudyRoom> result = roomService.getRooms(1, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("测试自习室", result.getRecords().get(0).getName());
    }

    /**
     * 测试根据 ID 获取自习室
     */
    @Test
    void getRoomById_Success() {
        when(studyRoomMapper.selectById(1L)).thenReturn(testRoom);

        StudyRoom result = roomService.getRoomById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    /**
     * 测试根据 ID 获取自习室 - 不存在
     */
    @Test
    void getRoomById_NotFound() {
        when(studyRoomMapper.selectById(99L)).thenReturn(null);

        StudyRoom result = roomService.getRoomById(99L);

        assertNull(result);
    }

    /**
     * 测试获取自习室的座位列表
     */
    @Test
    void getSeatsByRoomId_Success() {
        List<Seat> seats = Arrays.asList(testSeat);
        when(seatMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(seats);

        List<Seat> result = roomService.getSeatsByRoomId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A-01", result.get(0).getSeatNumber());
    }
}
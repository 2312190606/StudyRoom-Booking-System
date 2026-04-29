package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.ReservationMapper;
import com.example.studyroom.mapper.SeatMapper;
import com.example.studyroom.mapper.StudyRoomMapper;
import com.example.studyroom.model.entity.Reservation;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import com.example.studyroom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ReservationService 单元测试
 */
@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private SeatMapper seatMapper;

    @Mock
    private StudyRoomMapper studyRoomMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private Seat testSeat;
    private StudyRoom testRoom;

    @BeforeEach
    void setUp() {
        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setUserId(1L);
        testReservation.setSeatId(1L);
        testReservation.setStatus(1);
        testReservation.setStartTime(LocalDateTime.now().plusHours(1));
        testReservation.setEndTime(LocalDateTime.now().plusHours(3));
        testReservation.setExtended(0);

        testSeat = new Seat();
        testSeat.setId(1L);
        testSeat.setRoomId(1L);
        testSeat.setStatus(1); // 可用

        testRoom = new StudyRoom();
        testRoom.setId(1L);
        testRoom.setName("测试自习室");
    }

    /**
     * 测试创建预约 - 成功
     */
    @Test
    void createReservation_Success() {
        when(seatMapper.selectById(1L)).thenReturn(testSeat);
        when(reservationMapper.selectCount(any())).thenReturn(0L);

        assertDoesNotThrow(() ->
                reservationService.createReservation(1L, 1L,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(3)));

        verify(reservationMapper).insert(any(Reservation.class));
    }

    /**
     * 测试创建预约 - 开始时间早于当前时间
     */
    @Test
    void createReservation_StartTimeBeforeNow() {
        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.createReservation(1L, 1L,
                        LocalDateTime.now().minusHours(1),
                        LocalDateTime.now().plusHours(1)));

        assertEquals("开始时间不能早于当前时间", exception.getMessage());
    }

    /**
     * 测试创建预约 - 结束时间早于开始时间
     */
    @Test
    void createReservation_EndTimeBeforeStartTime() {
        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.createReservation(1L, 1L,
                        LocalDateTime.now().plusHours(3),
                        LocalDateTime.now().plusHours(1)));

        assertEquals("结束时间不能早于开始时间", exception.getMessage());
    }

    /**
     * 测试创建预约 - 座位不可用
     */
    @Test
    void createReservation_SeatUnavailable() {
        testSeat.setStatus(0); // 不可用
        when(seatMapper.selectById(1L)).thenReturn(testSeat);

        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.createReservation(1L, 1L,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(3)));

        assertEquals("该座位目前不可用", exception.getMessage());
    }

    /**
     * 测试创建预约 - 座位已被预约
     */
    @Test
    void createReservation_SeatAlreadyReserved() {
        when(seatMapper.selectById(1L)).thenReturn(testSeat);
        when(reservationMapper.selectCount(any())).thenReturn(1L);

        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.createReservation(1L, 1L,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(3)));

        assertEquals("该时段座位已被预约", exception.getMessage());
    }

    /**
     * 测试获取预约详情
     */
    @Test
    void getReservationById_Success() {
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);

        Reservation result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    /**
     * 测试删除预约 - 成功
     */
    @Test
    void deleteReservation_Success() {
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);

        assertDoesNotThrow(() -> reservationService.deleteReservation(1L, 1L));

        verify(reservationMapper).updateById(any(Reservation.class));
    }

    /**
     * 测试删除预约 - 无权操作
     */
    @Test
    void deleteReservation_NoPermission() {
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);

        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.deleteReservation(1L, 2L)); // 不同的userId

        assertEquals("预约不存在或无权操作", exception.getMessage());
    }

    /**
     * 测试取消预约 - 成功
     */
    @Test
    void cancelReservation_Success() {
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);
        // 设置结束时间在未来，避免触发扣分
        testReservation.setEndTime(LocalDateTime.now().plusHours(3));

        assertDoesNotThrow(() -> reservationService.cancelReservation(1L, 1L));

        verify(reservationMapper).updateById(any(Reservation.class));
    }

    /**
     * 测试取消预约 - 当前状态不可取消
     */
    @Test
    void cancelReservation_InvalidStatus() {
        testReservation.setStatus(0); // 已取消
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);

        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.cancelReservation(1L, 1L));

        assertEquals("当前状态不可取消", exception.getMessage());
    }

    /**
     * 测试签到 - 成功
     */
    @Test
    void checkIn_Success() {
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);

        assertDoesNotThrow(() -> reservationService.checkIn(1L, 1L));

        verify(reservationMapper).updateById(any(Reservation.class));
        verify(userService).addCreditScore(1L, 5);
    }

    /**
     * 测试签到 - 当前状态不可签到
     */
    @Test
    void checkIn_InvalidStatus() {
        testReservation.setStatus(2); // 已签到
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);

        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.checkIn(1L, 1L));

        assertEquals("当前状态不可签到", exception.getMessage());
    }

    /**
     * 测试结束学习 - 成功
     */
    @Test
    void endStudy_Success() {
        testReservation.setStatus(2); // 使用中
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);
        when(seatMapper.selectById(1L)).thenReturn(testSeat);

        assertDoesNotThrow(() -> reservationService.endStudy(1L, 1L));

        verify(reservationMapper).updateById(any(Reservation.class));
        verify(seatMapper).updateById(any(Seat.class));
    }

    /**
     * 测试结束学习 - 当前状态不可结束
     */
    @Test
    void endStudy_InvalidStatus() {
        testReservation.setStatus(1); // 待使用
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);

        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.endStudy(1L, 1L));

        assertEquals("当前状态不可结束", exception.getMessage());
    }

    /**
     * 测试延长预约 - 成功
     */
    @Test
    void extendReservation_Success() {
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);
        when(reservationMapper.selectCount(any())).thenReturn(0L);

        assertDoesNotThrow(() -> reservationService.extendReservation(1L, 1L));

        verify(reservationMapper).updateById(any(Reservation.class));
    }

    /**
     * 测试延长预约 - 已延后过
     */
    @Test
    void extendReservation_AlreadyExtended() {
        testReservation.setExtended(1);
        when(reservationMapper.selectById(1L)).thenReturn(testReservation);

        BaseException exception = assertThrows(BaseException.class,
                () -> reservationService.extendReservation(1L, 1L));

        assertEquals("已延后过一次，无法再次延后", exception.getMessage());
    }
}
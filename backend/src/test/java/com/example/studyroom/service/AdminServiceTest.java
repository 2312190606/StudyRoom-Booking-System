package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.mapper.*;
import com.example.studyroom.model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private StudyRoomMapper studyRoomMapper;

    @Mock
    private SeatMapper seatMapper;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private AnnouncementMapper announcementMapper;

    @Mock
    private CarouselMapper carouselMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AdminService adminService;

    private StudyRoom testRoom;
    private Seat testSeat;
    private User testUser;

    @BeforeEach
    void setUp() {
        testRoom = new StudyRoom();
        testRoom.setId(1L);
        testRoom.setName("Test Room");
        testRoom.setTotalSeats(50);
        testRoom.setSeatRows(5);
        testRoom.setCols(10);

        testSeat = new Seat();
        testSeat.setId(1L);
        testSeat.setSeatNumber("A-01");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setStatus(1);
    }

    @Test
    void getDashboardStats_ReturnsCorrectStats() {
        when(reservationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);
        when(userMapper.selectCount(null)).thenReturn(100L);
        when(studyRoomMapper.selectCount(null)).thenReturn(5L);
        when(seatMapper.selectCount(null)).thenReturn(200L);

        Map<String, Object> stats = adminService.getDashboardStats();

        assertEquals(10L, stats.get("todayReservations"));
        assertEquals(100L, stats.get("totalUsers"));
        assertEquals(5L, stats.get("totalRooms"));
        assertEquals(200L, stats.get("totalSeats"));
    }

    @Test
    void getAllRooms_ReturnsListOfRooms() {
        when(studyRoomMapper.selectList(null)).thenReturn(Arrays.asList(testRoom));

        List<StudyRoom> rooms = adminService.getAllRooms();

        assertEquals(1, rooms.size());
        assertEquals("Test Room", rooms.get(0).getName());
    }

    @Test
    void addRoom_Success() {
        adminService.addRoom(testRoom);

        verify(studyRoomMapper, times(1)).insert(testRoom);
    }

    @Test
    void updateRoom_Success() {
        when(studyRoomMapper.selectById(1L)).thenReturn(testRoom);

        testRoom.setName("Updated Room");

        adminService.updateRoom(testRoom);

        verify(studyRoomMapper, times(1)).updateById(testRoom);
    }

    @Test
    void deleteRoom_Success() {
        adminService.deleteRoom(1L);

        verify(studyRoomMapper, times(1)).deleteById(1L);
    }

    @Test
    void batchAddSeats_Success() {
        Seat seat1 = new Seat();
        seat1.setSeatNumber("A-01");
        Seat seat2 = new Seat();
        seat2.setSeatNumber("A-02");

        adminService.batchAddSeats(1L, Arrays.asList(seat1, seat2));

        verify(seatMapper, times(2)).insert(any(Seat.class));
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        when(userMapper.selectList(null)).thenReturn(Arrays.asList(testUser));
        when(loginAttemptService.isLocked(null, "testuser")).thenReturn(false);

        List<User> users = adminService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        assertNull(users.get(0).getPassword());
    }

    @Test
    void getAllUsers_LockedUser_StatusSetToZero() {
        when(userMapper.selectList(null)).thenReturn(Arrays.asList(testUser));
        when(loginAttemptService.isLocked(null, "testuser")).thenReturn(true);

        List<User> users = adminService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(0, users.get(0).getStatus());
    }

    @Test
    void createUser_Success() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        adminService.createUser("newuser", "password");

        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        adminService.deleteUser(1L);

        verify(userMapper, times(1)).deleteById(1L);
    }

    @Test
    void updateUserStatus_CannotDisableAdmin() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(1);
        admin.setStatus(1);
        when(userMapper.selectById(1L)).thenReturn(admin);

        assertThrows(com.example.studyroom.common.BaseException.class,
                () -> adminService.updateUserStatus(1L, 0));
    }

    @Test
    void updateUser_CannotDisableAdmin() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(1);
        admin.setStatus(1);
        when(userMapper.selectById(1L)).thenReturn(admin);

        assertThrows(com.example.studyroom.common.BaseException.class,
                () -> adminService.updateUser(1L, null, null, null, 0));
    }

    @Test
    void getConfig_ReturnsConfig() {
        Map<String, Object> config = adminService.getConfig();

        assertNotNull(config);
        assertEquals(60, config.get("minDuration"));
    }

    @Test
    void updateConfig_Success() {
        Map<String, Object> newConfig = new java.util.HashMap<>();
        newConfig.put("minDuration", 30);

        adminService.updateConfig(newConfig);

        Map<String, Object> config = adminService.getConfig();
        assertEquals(30, config.get("minDuration"));
    }

    @Test
    void getDashboardUtilization_Success() {
        StudyRoom room = new StudyRoom();
        room.setId(1L);
        room.setName("Test Room");
        room.setSeatRows(2);
        room.setCols(2);

        when(studyRoomMapper.selectList(null)).thenReturn(Arrays.asList(room));
        when(seatMapper.selectCount(any())).thenReturn(4L);
        when(reservationMapper.selectCount(any())).thenReturn(2L);

        Map<String, Object> utilization = adminService.getDashboardUtilization();

        assertEquals(1, utilization.size());
        assertNotNull(utilization.get("Test Room"));
    }

    @Test
    void getDashboardUsersTrend_Success() {
        when(userMapper.selectCount(any())).thenReturn(10L);

        List<Map<String, Object>> trend = adminService.getDashboardUsersTrend();

        assertEquals(7, trend.size());
    }

    @Test
    void updateMaintenanceSeats_Success() {
        StudyRoom room = new StudyRoom();
        room.setId(1L);
        room.setSeatRows(5);
        room.setCols(10);

        when(studyRoomMapper.selectById(1L)).thenReturn(room);

        adminService.updateMaintenanceSeats(1L, "[\"1-1\",\"1-2\"]");

        verify(studyRoomMapper, times(1)).updateById(any(StudyRoom.class));
    }

    @Test
    void adminCancelReservation_Success() {
        adminService.adminCancelReservation(1L);

        verify(reservationMapper, times(1)).updateById(any(Reservation.class));
    }

    @Test
    void updateSeat_Success() {
        adminService.updateSeat(testSeat);

        verify(seatMapper, times(1)).updateById(testSeat);
    }

    @Test
    void batchDeleteSeats_Success() {
        List<Long> ids = Arrays.asList(1L, 2L);

        adminService.batchDeleteSeats(ids);

        verify(seatMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    void updateAnnouncement_Success() {
        Announcement announcement = new Announcement();
        announcement.setId(1L);

        adminService.updateAnnouncement(announcement);

        verify(announcementMapper, times(1)).updateById(announcement);
    }

    @Test
    void deleteAnnouncement_Success() {
        adminService.deleteAnnouncement(1L);

        verify(announcementMapper, times(1)).deleteById(1L);
    }

    @Test
    void addCarousel_Success() {
        Carousel carousel = new Carousel();
        carousel.setImageUrl("http://example.com/1.jpg");

        adminService.addCarousel(carousel);

        verify(carouselMapper, times(1)).insert(carousel);
    }

    @Test
    void deleteCarousel_Success() {
        adminService.deleteCarousel(1L);

        verify(carouselMapper, times(1)).deleteById(1L);
    }

    @Test
    void updateUserStatus_Success() {
        adminService.updateUserStatus(1L, 0);

        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void updateUser_WithUsername_Success() {
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedpassword");

        adminService.updateUser(1L, "newusername", "newpassword", null, null);

        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void getAllAnnouncements_ReturnsList() {
        Announcement announcement = new Announcement();
        announcement.setId(1L);
        announcement.setTitle("Test Announcement");

        when(announcementMapper.selectList(null)).thenReturn(Arrays.asList(announcement));

        List<Announcement> announcements = adminService.getAllAnnouncements();

        assertEquals(1, announcements.size());
    }

    @Test
    void addAnnouncement_Success() {
        Announcement announcement = new Announcement();
        announcement.setTitle("New Announcement");

        adminService.addAnnouncement(announcement);

        verify(announcementMapper, times(1)).insert(announcement);
    }

    @Test
    void getAllCarousels_ReturnsList() {
        Carousel carousel = new Carousel();
        carousel.setId(1L);
        carousel.setImageUrl("http://example.com/1.jpg");

        when(carouselMapper.selectList(null)).thenReturn(Arrays.asList(carousel));

        List<Carousel> carousels = adminService.getAllCarousels();

        assertEquals(1, carousels.size());
    }

    @Test
    void batchUpdateSeatStatus_Success() {
        List<Long> seatIds = Arrays.asList(1L, 2L);

        adminService.batchUpdateSeatStatus(seatIds, 1);

        verify(seatMapper, times(2)).updateById(any(Seat.class));
    }
}

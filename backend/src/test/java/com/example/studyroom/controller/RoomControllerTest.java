package com.example.studyroom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import com.example.studyroom.security.JwtAuthenticationFilter;
import com.example.studyroom.service.LoginAttemptService;
import com.example.studyroom.service.RoomService;
import com.example.studyroom.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @Test
    void getRooms_Success() throws Exception {
        StudyRoom room = new StudyRoom();
        room.setId(1L);
        room.setName("Test Room");

        Page<StudyRoom> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(room));

        when(roomService.getRooms(anyInt(), anyInt(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/rooms")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].name").value("Test Room"));
    }

    @Test
    void getRooms_WithLocationFilter() throws Exception {
        StudyRoom room = new StudyRoom();
        room.setId(2L);
        room.setName("Library Room");
        room.setLocation("Library");

        Page<StudyRoom> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(room));

        when(roomService.getRooms(eq(1), eq(10), eq("Library"), any())).thenReturn(page);

        mockMvc.perform(get("/api/rooms")
                        .param("page", "1")
                        .param("size", "10")
                        .param("location", "Library"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].location").value("Library"));
    }

    @Test
    void getRoom_Success() throws Exception {
        StudyRoom room = new StudyRoom();
        room.setId(1L);
        room.setName("Test Room");
        room.setTotalSeats(50);

        when(roomService.getRoomById(1L)).thenReturn(room);

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Test Room"))
                .andExpect(jsonPath("$.data.totalSeats").value(50));
    }

    @Test
    void getRoom_NotFound() throws Exception {
        when(roomService.getRoomById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/rooms/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getSeats_Success() throws Exception {
        Seat seat1 = new Seat();
        seat1.setId(1L);
        seat1.setSeatNumber("A-01");

        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setSeatNumber("A-02");

        List<Seat> seats = Arrays.asList(seat1, seat2);

        when(roomService.getSeatsByRoomId(1L)).thenReturn(seats);

        mockMvc.perform(get("/api/rooms/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].seatNumber").value("A-01"))
                .andExpect(jsonPath("$.data[1].seatNumber").value("A-02"));
    }

    @Test
    void getSeats_EmptyRoom() throws Exception {
        when(roomService.getSeatsByRoomId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/rooms/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}

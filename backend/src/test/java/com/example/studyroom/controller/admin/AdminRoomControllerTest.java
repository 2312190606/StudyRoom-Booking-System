package com.example.studyroom.controller.admin;

import com.example.studyroom.common.BaseException;
import com.example.studyroom.model.entity.Seat;
import com.example.studyroom.model.entity.StudyRoom;
import com.example.studyroom.security.JwtAuthenticationFilter;
import com.example.studyroom.service.AdminService;
import com.example.studyroom.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getRooms_Success() throws Exception {
        StudyRoom room = new StudyRoom();
        room.setId(1L);
        room.setName("Test Room");

        when(adminService.getAllRooms()).thenReturn(Arrays.asList(room));

        mockMvc.perform(get("/api/admin/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("Test Room"));
    }

    @Test
    void addRoom_Success() throws Exception {
        String requestBody = "{\"name\":\"New Room\",\"location\":\"Building A\",\"totalSeats\":100}";

        mockMvc.perform(post("/api/admin/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateRoom_Success() throws Exception {
        String requestBody = "{\"name\":\"Updated Room\"}";

        mockMvc.perform(put("/api/admin/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateMaintenanceSeats_Success() throws Exception {
        String requestBody = "{\"maintenanceSeats\":\"5\"}";

        mockMvc.perform(put("/api/admin/rooms/1/maintenance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteRoom_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void batchAddSeats_Success() throws Exception {
        String requestBody = "[{\"seatNumber\":\"A-01\"},{\"seatNumber\":\"A-02\"}]";

        mockMvc.perform(post("/api/admin/rooms/1/seats/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateSeat_Success() throws Exception {
        String requestBody = "{\"seatNumber\":\"B-01\"}";

        mockMvc.perform(put("/api/admin/seats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void batchDeleteSeats_Success() throws Exception {
        String requestBody = "[1, 2, 3]";

        mockMvc.perform(delete("/api/admin/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void batchUpdateSeatStatus_Success() throws Exception {
        String requestBody = "{\"ids\":[1,2],\"status\":1}";

        mockMvc.perform(put("/api/admin/seats/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}

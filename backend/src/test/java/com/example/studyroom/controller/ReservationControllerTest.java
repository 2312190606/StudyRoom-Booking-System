package com.example.studyroom.controller;

import com.example.studyroom.common.BaseException;
import com.example.studyroom.service.ReservationService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createReservation_Success() throws Exception {
        String requestBody = "{\"seatId\":1,\"startTime\":\"2026-04-27T10:00:00\",\"endTime\":\"2026-04-27T12:00:00\"}";

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void createReservation_SeatAlreadyReserved() throws Exception {
        String requestBody = "{\"seatId\":1,\"startTime\":\"2026-04-27T10:00:00\",\"endTime\":\"2026-04-27T12:00:00\"}";

        doThrow(new BaseException("seat already reserved"))
                .when(reservationService).createReservation(anyLong(), anyLong(), any(), any());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("seat already reserved"));
    }

    @Test
    void createReservation_SeatUnavailable() throws Exception {
        String requestBody = "{\"seatId\":1,\"startTime\":\"2026-04-27T10:00:00\",\"endTime\":\"2026-04-27T12:00:00\"}";

        doThrow(new BaseException("seat unavailable"))
                .when(reservationService).createReservation(anyLong(), anyLong(), any(), any());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("seat unavailable"));
    }

    @Test
    void cancelReservation_Success() throws Exception {
        mockMvc.perform(put("/api/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void cancelReservation_InvalidStatus() throws Exception {
        doThrow(new BaseException("cannot cancel"))
                .when(reservationService).cancelReservation(anyLong(), anyLong());

        mockMvc.perform(put("/api/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("cannot cancel"));
    }

    @Test
    void checkIn_Success() throws Exception {
        mockMvc.perform(post("/api/reservations/1/check-in")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void checkIn_InvalidStatus() throws Exception {
        doThrow(new BaseException("cannot check in"))
                .when(reservationService).checkIn(anyLong(), anyLong());

        mockMvc.perform(post("/api/reservations/1/check-in")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("cannot check in"));
    }

    @Test
    void extendReservation_Success() throws Exception {
        mockMvc.perform(put("/api/reservations/1/extend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void extendReservation_AlreadyExtended() throws Exception {
        doThrow(new BaseException("already extended"))
                .when(reservationService).extendReservation(anyLong(), anyLong());

        mockMvc.perform(put("/api/reservations/1/extend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("already extended"));
    }

    @Test
    void endStudy_Success() throws Exception {
        mockMvc.perform(put("/api/reservations/1/end"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void endStudy_InvalidStatus() throws Exception {
        doThrow(new BaseException("cannot end study"))
                .when(reservationService).endStudy(anyLong(), anyLong());

        mockMvc.perform(put("/api/reservations/1/end"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("cannot end study"));
    }
}

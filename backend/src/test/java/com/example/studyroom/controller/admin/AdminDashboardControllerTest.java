package com.example.studyroom.controller.admin;

import com.example.studyroom.security.JwtAuthenticationFilter;
import com.example.studyroom.service.AdminService;
import com.example.studyroom.service.LoginAttemptService;
import com.example.studyroom.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getStats_Success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayReservations", 10L);
        stats.put("totalUsers", 100L);

        when(adminService.getDashboardStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.todayReservations").value(10));
    }

    @Test
    void getUtilization_Success() throws Exception {
        Map<String, Object> utilization = new HashMap<>();
        utilization.put("Room1", Map.of("utilizationRate", 75.0));

        when(adminService.getDashboardUtilization()).thenReturn(utilization);

        mockMvc.perform(get("/api/admin/dashboard/utilization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getUsersTrend_Success() throws Exception {
        List<Map<String, Object>> trend = new ArrayList<>();
        trend.add(Map.of("date", "2026-04-29", "count", 5L));

        when(adminService.getDashboardUsersTrend()).thenReturn(trend);

        mockMvc.perform(get("/api/admin/dashboard/users-trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].count").value(5));
    }
}
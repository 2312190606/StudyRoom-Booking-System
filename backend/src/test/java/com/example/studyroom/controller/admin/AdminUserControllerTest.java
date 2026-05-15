package com.example.studyroom.controller.admin;

import com.example.studyroom.model.entity.User;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminUserControllerTest {

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
    void getAllUsers_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(adminService.getAllUsers()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].username").value("testuser"));
    }

    @Test
    void createUser_Success() throws Exception {
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).createUser(eq("newuser"), eq("password123"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).deleteUser(eq(1L));
    }

    @Test
    void updateUserStatus_Success() throws Exception {
        mockMvc.perform(put("/api/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).updateUserStatus(eq(1L), eq(0));
    }

    @Test
    void updateUser_Success() throws Exception {
        mockMvc.perform(put("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updated\",\"role\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).updateUser(eq(1L), eq("updated"), isNull(), eq(1), isNull());
    }
}
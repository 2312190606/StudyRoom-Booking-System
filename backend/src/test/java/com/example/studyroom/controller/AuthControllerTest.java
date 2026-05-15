package com.example.studyroom.controller;

import com.example.studyroom.common.BaseException;
import com.example.studyroom.security.JwtAuthenticationFilter;
import com.example.studyroom.service.AuthService;
import com.example.studyroom.service.LoginAttemptService;
import com.example.studyroom.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @Test
    void register_Success() throws Exception {
        String requestBody = "{\"username\":\"newuser\",\"password\":\"password123\"}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void register_UsernameExists() throws Exception {
        String requestBody = "{\"username\":\"existinguser\",\"password\":\"password123\"}";

        doThrow(new BaseException("username exists"))
                .when(authService).register(any(), any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void login_Success() throws Exception {
        String requestBody = "{\"username\":\"testuser\",\"password\":\"password123\"}";

        when(authService.login("testuser", "password123")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("jwt-token"));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        String requestBody = "{\"username\":\"testuser\",\"password\":\"wrongpassword\"}";

        when(authService.login("testuser", "wrongpassword"))
                .thenThrow(new BaseException("invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void login_AccountDisabled() throws Exception {
        String requestBody = "{\"username\":\"disableduser\",\"password\":\"password123\"}";

        when(authService.login("disableduser", "password123"))
                .thenThrow(new BaseException("account disabled"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void refreshToken_Success() throws Exception {
        when(authService.refreshToken("old-token")).thenReturn("new-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer old-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("new-token"));
    }

    @Test
    void refreshToken_InvalidToken() throws Exception {
        when(authService.refreshToken("invalid-token"))
                .thenThrow(new BaseException("invalid token"));

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}

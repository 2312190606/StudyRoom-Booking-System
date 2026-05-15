package com.example.studyroom.controller.admin;

import com.example.studyroom.model.entity.Announcement;
import com.example.studyroom.model.entity.Carousel;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminContentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminContentControllerTest {

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

    // --- 公告管理 ---

    @Test
    void getAnnouncements_Success() throws Exception {
        Announcement announcement = new Announcement();
        announcement.setId(1L);
        announcement.setTitle("Test");

        when(adminService.getAllAnnouncements()).thenReturn(Arrays.asList(announcement));

        mockMvc.perform(get("/api/admin/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("Test"));
    }

    @Test
    void addAnnouncement_Success() throws Exception {
        mockMvc.perform(post("/api/admin/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Announcement\",\"content\":\"Content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).addAnnouncement(any(Announcement.class));
    }

    @Test
    void updateAnnouncement_Success() throws Exception {
        mockMvc.perform(put("/api/admin/announcements/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).updateAnnouncement(any(Announcement.class));
    }

    @Test
    void deleteAnnouncement_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/announcements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).deleteAnnouncement(eq(1L));
    }

    // --- 轮播图管理 ---

    @Test
    void getCarousels_Success() throws Exception {
        Carousel carousel = new Carousel();
        carousel.setId(1L);
        carousel.setImageUrl("http://example.com/1.jpg");

        when(adminService.getAllCarousels()).thenReturn(Arrays.asList(carousel));

        mockMvc.perform(get("/api/admin/carousels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].imageUrl").value("http://example.com/1.jpg"));
    }

    @Test
    void addCarousel_Success() throws Exception {
        mockMvc.perform(post("/api/admin/carousels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imageUrl\":\"http://example.com/1.jpg\",\"status\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).addCarousel(any(Carousel.class));
    }

    @Test
    void deleteCarousel_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/carousels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).deleteCarousel(eq(1L));
    }
}
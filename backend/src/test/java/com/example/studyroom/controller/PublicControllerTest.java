package com.example.studyroom.controller;

import com.example.studyroom.model.entity.Announcement;
import com.example.studyroom.model.entity.Carousel;
import com.example.studyroom.service.PublicService;
import com.example.studyroom.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicService publicService;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void getCarousels_Success() throws Exception {
        Carousel carousel = new Carousel();
        carousel.setId(1L);
        carousel.setImageUrl("http://example.com/1.jpg");

        when(publicService.getCarousels()).thenReturn(Arrays.asList(carousel));

        mockMvc.perform(get("/api/public/carousels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].imageUrl").value("http://example.com/1.jpg"));
    }

    @Test
    void getAnnouncements_Success() throws Exception {
        Announcement announcement = new Announcement();
        announcement.setId(1L);
        announcement.setTitle("Test Announcement");

        when(publicService.getAnnouncements()).thenReturn(Arrays.asList(announcement));

        mockMvc.perform(get("/api/public/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("Test Announcement"));
    }

    @Test
    void getAnnouncement_Success() throws Exception {
        Announcement announcement = new Announcement();
        announcement.setId(1L);
        announcement.setTitle("Test");

        when(publicService.getAnnouncementById(1L)).thenReturn(announcement);

        mockMvc.perform(get("/api/public/announcements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("Test"));
    }

    @Test
    void getAnnouncement_NotFound() throws Exception {
        when(publicService.getAnnouncementById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/public/announcements/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
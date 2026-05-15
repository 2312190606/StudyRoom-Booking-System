package com.example.studyroom.controller;

import com.example.studyroom.model.entity.Favorite;
import com.example.studyroom.security.JwtAuthenticationFilter;
import com.example.studyroom.service.FavoriteService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getFavorites_Success() throws Exception {
        Favorite favorite = new Favorite();
        favorite.setId(1L);
        favorite.setSeatId(10L);

        when(favoriteService.getMyFavorites(1L)).thenReturn(Arrays.asList(favorite));

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void addFavorite_Success() throws Exception {
        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatId\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(favoriteService).addFavorite(eq(1L), eq(10L));
    }

    @Test
    void deleteFavorite_Success() throws Exception {
        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(favoriteService).deleteFavorite(eq(1L), eq(1L));
    }

    @Test
    void quickReserve_Success() throws Exception {
        mockMvc.perform(post("/api/favorites/1/quick-reserve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(favoriteService).quickReserve(eq(1L), eq(1L));
    }
}
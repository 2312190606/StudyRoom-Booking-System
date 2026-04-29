package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.common.BaseException;
import com.example.studyroom.mapper.FavoriteMapper;
import com.example.studyroom.model.entity.Favorite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FavoriteService 单元测试
 */
@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private FavoriteService favoriteService;

    private Favorite testFavorite;

    @BeforeEach
    void setUp() {
        testFavorite = new Favorite();
        testFavorite.setId(1L);
        testFavorite.setUserId(1L);
        testFavorite.setSeatId(10L);
    }

    /**
     * 测试获取常用座位列表 - 成功
     */
    @Test
    void getMyFavorites_Success() {
        List<Favorite> favorites = Arrays.asList(testFavorite);
        when(favoriteMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(favorites);

        List<Favorite> result = favoriteService.getMyFavorites(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    /**
     * 测试获取常用座位列表 - 空列表
     */
    @Test
    void getMyFavorites_Empty() {
        when(favoriteMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        List<Favorite> result = favoriteService.getMyFavorites(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * 测试添加常用座位 - 成功
     */
    @Test
    void addFavorite_Success() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        assertDoesNotThrow(() -> favoriteService.addFavorite(1L, 10L));

        verify(favoriteMapper).insert(any(Favorite.class));
    }

    /**
     * 测试添加常用座位 - 已在收藏列表中
     */
    @Test
    void addFavorite_AlreadyExists() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BaseException exception = assertThrows(BaseException.class,
                () -> favoriteService.addFavorite(1L, 10L));

        assertEquals("该座位已在收藏列表中", exception.getMessage());
        verify(favoriteMapper, never()).insert(any(Favorite.class));
    }

    /**
     * 测试取消收藏 - 成功
     */
    @Test
    void deleteFavorite_Success() {
        assertDoesNotThrow(() -> favoriteService.deleteFavorite(1L, 1L));

        verify(favoriteMapper).delete(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试一键预约 - 成功
     */
    @Test
    void quickReserve_Success() {
        when(favoriteMapper.selectById(1L)).thenReturn(testFavorite);

        assertDoesNotThrow(() -> favoriteService.quickReserve(1L, 1L));

        verify(reservationService).createReservation(eq(1L), eq(10L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试一键预约 - 收藏记录不存在
     */
    @Test
    void quickReserve_NotFound() {
        when(favoriteMapper.selectById(1L)).thenReturn(null);

        BaseException exception = assertThrows(BaseException.class,
                () -> favoriteService.quickReserve(1L, 1L));

        assertEquals("收藏记录不存在", exception.getMessage());
    }

    /**
     * 测试一键预约 - 无权操作
     */
    @Test
    void quickReserve_NoPermission() {
        testFavorite.setUserId(2L); // 不同的用户
        when(favoriteMapper.selectById(1L)).thenReturn(testFavorite);

        BaseException exception = assertThrows(BaseException.class,
                () -> favoriteService.quickReserve(1L, 1L));

        assertEquals("收藏记录不存在", exception.getMessage());
    }
}
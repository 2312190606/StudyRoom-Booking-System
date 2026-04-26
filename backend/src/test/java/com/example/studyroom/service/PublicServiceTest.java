package com.example.studyroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studyroom.mapper.AnnouncementMapper;
import com.example.studyroom.mapper.CarouselMapper;
import com.example.studyroom.model.entity.Announcement;
import com.example.studyroom.model.entity.Carousel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PublicServiceTest {

    @Mock
    private CarouselMapper carouselMapper;

    @Mock
    private AnnouncementMapper announcementMapper;

    @InjectMocks
    private PublicService publicService;

    @Test
    void getCarousels_ReturnsActiveCarousels() {
        Carousel carousel1 = new Carousel();
        carousel1.setId(1L);
        carousel1.setImageUrl("http://example.com/1.jpg");
        carousel1.setStatus(1);

        Carousel carousel2 = new Carousel();
        carousel2.setId(2L);
        carousel2.setImageUrl("http://example.com/2.jpg");
        carousel2.setStatus(1);

        when(carouselMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(carousel1, carousel2));

        List<Carousel> carousels = publicService.getCarousels();

        assertEquals(2, carousels.size());
    }

    @Test
    void getAnnouncements_ReturnsPublishedAnnouncements() {
        Announcement announcement1 = new Announcement();
        announcement1.setId(1L);
        announcement1.setTitle("Announcement 1");
        announcement1.setStatus(1);

        when(announcementMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(announcement1));

        List<Announcement> announcements = publicService.getAnnouncements();

        assertEquals(1, announcements.size());
        assertEquals("Announcement 1", announcements.get(0).getTitle());
    }

    @Test
    void getAnnouncementById_ReturnsAnnouncement() {
        Announcement announcement = new Announcement();
        announcement.setId(1L);
        announcement.setTitle("Test Announcement");

        when(announcementMapper.selectById(1L)).thenReturn(announcement);

        Announcement result = publicService.getAnnouncementById(1L);

        assertNotNull(result);
        assertEquals("Test Announcement", result.getTitle());
    }

    @Test
    void getAnnouncementById_ReturnsNull_WhenNotFound() {
        when(announcementMapper.selectById(999L)).thenReturn(null);

        Announcement result = publicService.getAnnouncementById(999L);

        assertNull(result);
    }
}

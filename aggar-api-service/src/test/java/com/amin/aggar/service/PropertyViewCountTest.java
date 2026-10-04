package com.amin.aggar.service;

import com.amin.aggar.domain.entity.Property;
import com.amin.aggar.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PropertyViewCountTest {

    private final PropertyRepository propertyRepository = mock(PropertyRepository.class);
    private final PropertyService propertyService = new PropertyService(
            propertyRepository,
            mock(StateRepository.class),
            mock(CityRepository.class),
            mock(NeighborhoodRepository.class),
            mock(UserRepository.class),
            mock(AmenityRepository.class),
            mock(PropertyImageRepository.class),
            mock(PriceHistoryRepository.class),
            mock(EntityManager.class));

    @Test
    void recordsViewBySlugAndReturnsUpdatedCount() {
        Property property = new Property();
        property.setId(12L);
        property.setViewCount(4L);
        when(propertyRepository.incrementViewCountBySlug("downtown-home")).thenAnswer(invocation -> {
            property.setViewCount(property.getViewCount() + 1);
            return 1;
        });
        when(propertyRepository.findBySlug("downtown-home")).thenReturn(Optional.of(property));

        var result = propertyService.recordViewBySlug("downtown-home");

        assertTrue(result.isPresent());
        assertEquals(5L, result.get().viewCount());
        verify(propertyRepository).incrementViewCountBySlug("downtown-home");
    }

    @Test
    void returnsEmptyWhenViewedPropertyDoesNotExist() {
        when(propertyRepository.incrementViewCountById(99L)).thenReturn(0);

        var result = propertyService.recordViewById(99L);

        assertTrue(result.isEmpty());
    }
}

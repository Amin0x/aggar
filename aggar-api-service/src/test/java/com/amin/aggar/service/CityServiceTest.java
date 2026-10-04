package com.amin.aggar.service;

import com.amin.aggar.api.dto.CityDto;
import com.amin.aggar.domain.entity.City;
import com.amin.aggar.domain.entity.State;
import com.amin.aggar.repository.CityRepository;
import com.amin.aggar.repository.StateRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CityServiceTest {

    private final CityRepository cityRepository = mock(CityRepository.class);
    private final StateRepository stateRepository = mock(StateRepository.class);
    private final CityService cityService = new CityService(cityRepository, stateRepository);

    @Test
    void createsCityWithoutState() {
        CityDto dto = new CityDto(
                null,
                null,
                "Unassigned City",
                null,
                null
        );
        dto.name();
        when(cityRepository.save(any(City.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CityDto created = cityService.create(dto);

        assertNull(created.stateId());
    }

    @Test
    void clearsStateWhenUpdatingCityWithoutOne() {
        City city = new City();
        city.setState(new State());
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(cityRepository.save(city)).thenReturn(city);
        CityDto dto = new CityDto(
                null,
                null,
                "Unassigned City",
                null,
                null
        );

        Optional<CityDto> updated = cityService.update(1, dto);

        assertTrue(updated.isPresent());
        assertNull(city.getState());
        verify(cityRepository).save(city);
    }
}

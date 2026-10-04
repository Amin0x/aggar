package com.amin.aggar.service;

import com.amin.aggar.api.dto.StateDto;
import com.amin.aggar.domain.entity.State;
import com.amin.aggar.repository.StateRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StateServiceTest {

    @Test
    void mapsArabicStateNameToApiDto() {
        StateRepository repository = mock(StateRepository.class);
        State state = new State();
        state.setName("California");
        state.setNameAr("كاليفورنيا");
        when(repository.findAll()).thenReturn(List.of(state));

        StateDto dto = new StateService(repository).list().get(0);

        assertEquals("California", dto.name());
        assertEquals("كاليفورنيا", dto.nameAr());
    }

    @Test
    void persistsArabicStateNameFromApiDto() {
        StateRepository repository = mock(StateRepository.class);
        when(repository.save(any(State.class))).thenAnswer(invocation -> invocation.getArgument(0));
        StateDto dto = new StateDto(
                100,
                "California",
                "كاليفورنيا",
                "CA"
        );

        StateDto created = new StateService(repository).create(dto);

        assertEquals("كاليفورنيا", created.nameAr());
        verify(repository).save(any(State.class));
    }
}

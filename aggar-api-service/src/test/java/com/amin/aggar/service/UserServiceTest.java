package com.amin.aggar.service;

import com.amin.aggar.api.dto.AuthenticatedUserDto;
import com.amin.aggar.api.dto.UserDto;
import com.amin.aggar.domain.entity.User;
import com.amin.aggar.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);

    @Test
    void authenticatesByUsername() {
        User user = user("jane", "jane@example.com", "secret");
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(user));

        Optional<AuthenticatedUserDto> result = userService.authenticate("jane", "secret");

        assertTrue(result.isPresent());
        assertEquals("jane", result.get().username());
        verify(userRepository).findByUsername("jane");
    }

    @Test
    void authenticatesByCaseInsensitiveEmail() {
        User user = user("jane", "jane@example.com", "secret");
        when(userRepository.findByUsername("JANE@EXAMPLE.COM")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("JANE@EXAMPLE.COM")).thenReturn(Optional.of(user));

        Optional<AuthenticatedUserDto> result = userService.authenticate("JANE@EXAMPLE.COM", "secret");

        assertTrue(result.isPresent());
        assertEquals("jane", result.get().username());
        verify(userRepository).findByEmailIgnoreCase("JANE@EXAMPLE.COM");
    }

    @Test
    void rejectsInvalidPassword() {
        when(userRepository.findByUsername("jane"))
                .thenReturn(Optional.of(user("jane", "jane@example.com", "secret")));

        assertTrue(userService.authenticate("jane", "wrong").isEmpty());
    }

    @Test
    void doesNotExposePasswordOnUserResponses() throws NoSuchFieldException {
        JsonProperty passwordAccess = UserDto.class.getDeclaredField("password")
                .getAnnotation(JsonProperty.class);

        assertEquals(JsonProperty.Access.WRITE_ONLY, passwordAccess.access());
        assertFalse(java.util.Arrays.stream(AuthenticatedUserDto.class.getDeclaredFields())
                .anyMatch(field -> field.getName().equals("password")));
    }

    private User user(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}

package com.amin.aggar.service;

import com.amin.aggar.domain.entity.Property;
import com.amin.aggar.domain.entity.PropertyComment;
import com.amin.aggar.domain.entity.User;
import com.amin.aggar.repository.PropertyCommentRepository;
import com.amin.aggar.repository.PropertyRepository;
import com.amin.aggar.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PropertyCommentServiceTest {

    private final PropertyCommentRepository commentRepository = mock(PropertyCommentRepository.class);
    private final PropertyRepository propertyRepository = mock(PropertyRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PropertyCommentService service =
            new PropertyCommentService(commentRepository, propertyRepository, userRepository);

    @Test
    void createsTrimmedCommentForPropertyAndUser() {
        Property property = new Property();
        property.setId(4L);
        User user = new User();
        user.setId(7L);
        user.setName("Jane");
        when(propertyRepository.findById(4L)).thenReturn(Optional.of(property));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(PropertyComment.class))).thenAnswer(invocation -> {
            PropertyComment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        var result = service.create(4L, 7L, "  Nice home  ");

        assertEquals("Nice home", result.getContent());
        assertEquals(4L, result.getPropertyId());
        assertEquals(7L, result.getAuthorId());
        assertEquals("Jane", result.getAuthorName());
    }

    @Test
    void rejectsBlankComments() {
        assertThrows(ResponseStatusException.class, () -> service.create(4L, 7L, "  "));
    }
}

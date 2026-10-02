package com.amin.aggar.service;

import com.amin.aggar.api.dto.PropertyCommentDto;
import com.amin.aggar.domain.entity.PropertyComment;
import com.amin.aggar.repository.PropertyCommentRepository;
import com.amin.aggar.repository.PropertyRepository;
import com.amin.aggar.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PropertyCommentService {

    private static final int MAX_CONTENT_LENGTH = 2000;

    private final PropertyCommentRepository commentRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyCommentService(PropertyCommentRepository commentRepository,
                                  PropertyRepository propertyRepository,
                                  UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    public Page<PropertyCommentDto> findByProperty(Long propertyId, Pageable pageable) {
        return commentRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId, pageable)
                .map(this::toDto);
    }

    @Transactional
    public PropertyCommentDto create(Long propertyId, String authorUsername, String content) {
        if (content == null || content.isBlank() || content.length() > MAX_CONTENT_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment must contain 1 to 2000 characters");
        }

        PropertyComment comment = new PropertyComment();
        comment.setProperty(propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found")));
        comment.setAuthor(userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found")));
        comment.setContent(content.trim());
        return toDto(commentRepository.save(comment));
    }

    private PropertyCommentDto toDto(PropertyComment comment) {
        PropertyCommentDto dto = new PropertyCommentDto();
        dto.setId(comment.getId());
        dto.setPropertyId(comment.getProperty().getId());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}

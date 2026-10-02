package com.amin.aggar.repository;

import com.amin.aggar.domain.entity.PropertyComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyCommentRepository extends JpaRepository<PropertyComment, Long> {
    @EntityGraph(attributePaths = "author")
    Page<PropertyComment> findByPropertyIdOrderByCreatedAtDesc(Long propertyId, Pageable pageable);
}

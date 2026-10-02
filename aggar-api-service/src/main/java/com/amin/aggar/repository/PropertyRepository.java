package com.amin.aggar.repository;

import com.amin.aggar.domain.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Override
    @EntityGraph(attributePaths = {"state", "city", "neighborhood", "owner", "agent"})
    Page<Property> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"state", "city", "neighborhood", "owner", "agent"})
    Optional<Property> findById(Long id);

    @EntityGraph(attributePaths = {"state", "city", "neighborhood", "owner", "agent"})
    Optional<Property> findBySlug(String slug);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Property p set p.viewCount = coalesce(p.viewCount, 0) + 1 where p.id = :id")
    int incrementViewCountById(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Property p set p.viewCount = coalesce(p.viewCount, 0) + 1 where p.slug = :slug")
    int incrementViewCountBySlug(@Param("slug") String slug);

    @EntityGraph(attributePaths = {"state", "city", "neighborhood", "owner", "agent"})
    Page<Property> findByStatus(String status, Pageable pageable);
}

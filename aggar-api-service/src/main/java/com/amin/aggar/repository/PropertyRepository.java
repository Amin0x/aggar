package com.amin.aggar.repository;

import com.amin.aggar.domain.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
}


package com.amin.aggar.repository;

import com.amin.aggar.domain.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    List<PropertyImage> findByPropertyId(Long propertyId);
    long countByPropertyId(Long propertyId);
    void deleteByPropertyIdAndUrl(Long propertyId, String url);
}

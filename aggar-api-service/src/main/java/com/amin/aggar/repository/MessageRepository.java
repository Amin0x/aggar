package com.amin.aggar.repository;

import com.amin.aggar.domain.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByPropertyId(Long propertyId, Pageable pageable);

    Page<Message> findBySenderId(Integer senderId, Pageable pageable);

    Page<Message> findByIsReadFalse(Pageable pageable);

    long countByPropertyIdAndIsReadFalse(Long propertyId);
}

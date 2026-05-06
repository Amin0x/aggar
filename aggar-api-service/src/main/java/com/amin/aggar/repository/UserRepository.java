package com.amin.aggar.repository;

import com.amin.aggar.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    java.util.Optional<User> findByUsername(String username);
}


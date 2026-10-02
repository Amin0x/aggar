package com.amin.aggar.config;

import com.amin.aggar.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component("userAuthorization")
public class UserAuthorization {

    private final UserRepository userRepository;

    public UserAuthorization(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isSelf(Long userId, String username) {
        if (userId == null || username == null) {
            return false;
        }
        return userRepository.findById(userId)
                .map(user -> username.equals(user.getUsername()))
                .orElse(false);
    }
}

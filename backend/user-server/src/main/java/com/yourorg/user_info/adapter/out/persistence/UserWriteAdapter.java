package com.yourorg.user_info.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.yourorg.user_info.adapter.out.repository.UserJPARepository;
import com.yourorg.user_info.domain.entity.User;
import com.yourorg.user_info.port.out.persistence.UserWritePort;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class UserWriteAdapter implements UserWritePort {
    
    private final UserJPARepository userJPARepository;
    // Example method to save a friend entity
    @Override
    public User saveUser(User user) {
        // Use the repository to save the friend entity
        return userJPARepository.save(user);
    }
}
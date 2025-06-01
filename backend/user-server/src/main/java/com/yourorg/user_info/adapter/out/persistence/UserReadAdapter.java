package com.yourorg.user_info.adapter.out.persistence;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import com.yourorg.user_info.domain.entity.User;
import com.yourorg.user_info.port.out.persistence.UserReadPort;
import com.yourorg.user_info.adapter.out.repository.UserJPARepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserReadAdapter implements UserReadPort {

    private final UserJPARepository userRepository;

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }
}

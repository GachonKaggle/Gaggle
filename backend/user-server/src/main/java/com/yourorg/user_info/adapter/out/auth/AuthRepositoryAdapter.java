package com.yourorg.user_info.adapter.out.auth;

import com.yourorg.user_info.domain.entity.User;
import com.yourorg.user_info.port.out.auth.AuthRepositoryPort;
import org.springframework.stereotype.Repository;
import com.yourorg.user_info.adapter.out.repository.AuthJPARepository;

@Repository
public class AuthRepositoryAdapter implements AuthRepositoryPort {

    private final AuthJPARepository authJpaRepository;

    public AuthRepositoryAdapter(AuthJPARepository authJpaRepository) {
        this.authJpaRepository = authJpaRepository;
    }

    @Override
    public User save(User user) {
        return authJpaRepository.save(user);
    }

    @Override
    public User findByLoginId(String loginId) {
         return authJpaRepository.findByLoginId(loginId);
    }
}

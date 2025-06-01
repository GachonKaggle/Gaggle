package com.yourorg.user_info.port.out.auth;
import com.yourorg.user_info.domain.entity.User;

public interface AuthRepositoryPort {
    User save(User user);
    User findByLoginId(String loginId);
}
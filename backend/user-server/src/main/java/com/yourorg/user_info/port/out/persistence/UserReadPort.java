package com.yourorg.user_info.port.out.persistence;
// Import User entity

import com.yourorg.user_info.domain.entity.User;
import java.util.Optional;

public interface UserReadPort {
    Optional<User> findByLoginId(String loginId); // "load" → "find"로 변경
}
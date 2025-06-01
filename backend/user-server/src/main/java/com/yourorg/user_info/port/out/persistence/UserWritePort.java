package com.yourorg.user_info.port.out.persistence;
import com.yourorg.user_info.domain.entity.User;

public interface UserWritePort {
    User saveUser(User user);
}
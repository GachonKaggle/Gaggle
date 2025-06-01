package com.yourorg.user_info.port.in.auth;

import com.yourorg.user_info.domain.entity.User;
import com.yourorg.user_info.adapter.in.dto.LoginResponsedto;

public interface AuthPort {
    User signup(String loginId, String password, String role);
    LoginResponsedto login(String loginId, String password);
}

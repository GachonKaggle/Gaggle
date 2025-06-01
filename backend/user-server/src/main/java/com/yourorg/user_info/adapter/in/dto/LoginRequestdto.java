package com.yourorg.user_info.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestdto {
    private String loginId;
    private String password;
}

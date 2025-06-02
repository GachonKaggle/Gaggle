package com.yourorg.user_info.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestdto {
    private String loginId;
    private String password;
    private String role;
}


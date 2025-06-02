package com.yourorg.user_info;

import com.yourorg.user_info.domain.entity.User;
import com.yourorg.user_info.port.in.auth.AuthPort;
import com.yourorg.user_info.adapter.in.dto.LoginRequestdto;
import com.yourorg.user_info.adapter.in.dto.LoginResponsedto;
import com.yourorg.user_info.adapter.in.dto.SignupRequestdto;
import com.yourorg.user_info.adapter.in.dto.OurApiResponse;
import com.yourorg.user_info.util.JwtProvider;
import com.yourorg.user_info.adapter.in.auth.AuthAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthAdapterTest {

    @Mock
    private AuthPort authPort;

    @InjectMocks
    private AuthAdapter authAdapter;

    @BeforeEach
    void setup() {
        // No setup needed for now
    }

    @Test
    void signup_success() throws Exception {
        SignupRequestdto req = new SignupRequestdto();
        req.setLoginId("testUser");
        req.setPassword("testPass");
        req.setRole("USER");

        User mockUser = new User();
        mockUser.setLoginId("testUser");

        when(authPort.signup(anyString(), anyString(), anyString())).thenReturn(mockUser);

        ResponseEntity<OurApiResponse<User>> response = authAdapter.signup(req);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        OurApiResponse<User> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo("success");
        assertThat(body.getData().getLoginId()).isEqualTo("testUser");
        assertThat(body.getMessage()).isEqualTo("Signup successful");

        verify(authPort, times(1)).signup("testUser", "testPass", "USER");
    }

    @Test
    void signup_fail() throws Exception {
        SignupRequestdto req = new SignupRequestdto();
        req.setLoginId("testUser");
        req.setPassword("testPass");
        req.setRole("USER");

        when(authPort.signup(anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("ID already exists."));

        ResponseEntity<OurApiResponse<User>> response = authAdapter.signup(req);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        OurApiResponse<User> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo("fail");
        assertThat(body.getData()).isNull();
        assertThat(body.getMessage()).isEqualTo("ID already exists.");

        verify(authPort, times(1)).signup("testUser", "testPass", "USER");
    }

    @Test
    void login_success() throws Exception {
        LoginRequestdto req = new LoginRequestdto();
        req.setLoginId("testUser");
        req.setPassword("testPass");

        LoginResponsedto mockResponse = new LoginResponsedto();
        mockResponse.setToken("token123");

        when(authPort.login(anyString(), anyString())).thenReturn(mockResponse);

        ResponseEntity<OurApiResponse<LoginResponsedto>> response = authAdapter.login(req);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        OurApiResponse<LoginResponsedto> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo("success");
        assertThat(body.getData().getToken()).isEqualTo("token123");
        assertThat(body.getMessage()).isEqualTo("Login successful");

        verify(authPort, times(1)).login("testUser", "testPass");
    }

    @Test
    void login_fail() throws Exception {
        LoginRequestdto req = new LoginRequestdto();
        req.setLoginId("testUser");
        req.setPassword("wrongPass");

        when(authPort.login(anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Login information does not match."));

        ResponseEntity<OurApiResponse<LoginResponsedto>> response = authAdapter.login(req);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        OurApiResponse<LoginResponsedto> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo("fail");
        assertThat(body.getData()).isNull();
        assertThat(body.getMessage()).isEqualTo("Login information does not match.");

        verify(authPort, times(1)).login("testUser", "wrongPass");
    }
}

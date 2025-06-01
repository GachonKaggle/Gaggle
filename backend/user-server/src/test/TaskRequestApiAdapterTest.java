package com.yourorg.user_info.adapter.in.auth; // Package declaration

// Import necessary classes and annotations
import com.yourorg.user_info.domain.entity.User;
import com.yourorg.user_info.port.in.auth.AuthPort;
import com.yourorg.user_info.adapter.in.dto.LoginRequestdto;
import com.yourorg.user_info.adapter.in.dto.LoginResponsedto;
import com.yourorg.user_info.adapter.in.dto.SignupRequestdto;
import com.yourorg.user_info.adapter.in.dto.OurApiResponse;

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

@ExtendWith(MockitoExtension.class) // Enables Mockito extension for JUnit 5
class AuthAdapterTest {

    @Mock // Creates a mock instance of AuthPort
    private AuthPort authPort;

    @InjectMocks // Injects mocks into the AuthAdapter instance
    private AuthAdapter authAdapter;

    @BeforeEach
    void setup() {
        // Setup method, runs before each test
    }

    @Test
    void signup_성공() throws Exception { // Test successful signup
        SignupRequestdto req = new SignupRequestdto(); // Create request DTO
        req.setLoginId("testUser"); // Set loginId
        req.setPassword("testPass"); // Set password
        req.setRole("USER"); // Set role

        User mockUser = new User(); // Create mock user
        mockUser.setLoginId("testUser"); // Set loginId for mock user

        // Mock behavior for authPort.signup() to return mockUser
        when(authPort.signup(anyString(), anyString(), anyString())).thenReturn(mockUser);

        // Call the method to be tested
        ResponseEntity<OurApiResponse<User>> response = authAdapter.signup(req);

        // Verify the response
        assertThat(response.getStatusCodeValue()).isEqualTo(200); // Expect HTTP 200
        OurApiResponse<User> body = response.getBody(); // Extract body
        assertThat(body).isNotNull(); // Body should not be null
        assertThat(body.getStatus()).isEqualTo("success"); // Status should be success
        assertThat(body.getData().getLoginId()).isEqualTo("testUser"); // Check returned loginId
        assertThat(body.getMessage()).isEqualTo("회원가입 성공"); // Check success message

        // Verify that signup() was called once with specific parameters
        verify(authPort, times(1)).signup("testUser", "testPass", "USER");
    }

    @Test
    void signup_실패() throws Exception { // Test signup failure
        SignupRequestdto req = new SignupRequestdto(); // Create request DTO
        req.setLoginId("testUser"); // Set loginId
        req.setPassword("testPass"); // Set password
        req.setRole("USER"); // Set role

        // Mock behavior to throw an exception
        when(authPort.signup(anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("이미 존재하는 아이디입니다."));

        // Call the method to be tested
        ResponseEntity<OurApiResponse<User>> response = authAdapter.signup(req);

        // Verify the response
        assertThat(response.getStatusCodeValue()).isEqualTo(400); // Expect HTTP 400
        OurApiResponse<User> body = response.getBody(); // Extract body
        assertThat(body).isNotNull(); // Body should not be null
        assertThat(body.getStatus()).isEqualTo("fail"); // Status should be fail
        assertThat(body.getData()).isNull(); // Data should be null
        assertThat(body.getMessage()).isEqualTo("이미 존재하는 아이디입니다."); // Check error message

        // Verify that signup() was called once
        verify(authPort, times(1)).signup("testUser", "testPass", "USER");
    }

    @Test
    void login_성공() throws Exception { // Test successful login
        LoginRequestdto req = new LoginRequestdto(); // Create login request DTO
        req.setLoginId("testUser"); // Set loginId
        req.setPassword("testPass"); // Set password

        LoginResponsedto mockResponse = new LoginResponsedto(); // Create mock login response
        mockResponse.setAccessToken("token123"); // Set mock token

        // Mock login to return the mockResponse
        when(authPort.login(anyString(), anyString())).thenReturn(mockResponse);

        // Call the login method
        ResponseEntity<OurApiResponse<LoginResponsedto>> response = authAdapter.login(req);

        // Verify the response
        assertThat(response.getStatusCodeValue()).isEqualTo(200); // Expect HTTP 200
        OurApiResponse<LoginResponsedto> body = response.getBody(); // Extract response body
        assertThat(body).isNotNull(); // Body should not be null
        assertThat(body.getStatus()).isEqualTo("success"); // Status should be success
        assertThat(body.getData().getAccessToken()).isEqualTo("token123"); // Check access token
        assertThat(body.getMessage()).isEqualTo("로그인 성공"); // Check success message

        // Verify login method call
        verify(authPort, times(1)).login("testUser", "testPass");
    }

    @Test
    void login_실패() throws Exception { // Test login failure
        LoginRequestdto req = new LoginRequestdto(); // Create login request
        req.setLoginId("testUser"); // Set loginId
        req.setPassword("wrongPass"); // Set incorrect password

        // Mock login to throw exception
        when(authPort.login(anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("로그인 정보가 일치하지 않습니다."));

        // Call login method
        ResponseEntity<OurApiResponse<LoginResponsedto>> response = authAdapter.login(req);

        // Verify the response
        assertThat(response.getStatusCodeValue()).isEqualTo(400); // Expect HTTP 400
        OurApiResponse<LoginResponsedto> body = response.getBody(); // Get body
        assertThat(body).isNotNull(); // Body should not be null
        assertThat(body.getStatus()).isEqualTo("fail"); // Status should be fail
        assertThat(body.getData()).isNull(); // Data should be null
        assertThat(body.getMessage()).isEqualTo("로그인 정보가 일치하지 않습니다."); // Check error message

        // Verify login method call
        verify(authPort, times(1)).login("testUser", "wrongPass");
    }
}

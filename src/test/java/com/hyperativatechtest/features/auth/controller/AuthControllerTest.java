package com.hyperativatechtest.features.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperativatechtest.features.auth.dto.AuthRequest;
import com.hyperativatechtest.features.auth.service.UserService;
import com.hyperativatechtest.features.common.entity.Role;
import com.hyperativatechtest.features.common.entity.RoleType;
import com.hyperativatechtest.features.common.entity.User;
import com.hyperativatechtest.features.common.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    private User testUser;
    private String validTestToken;

    @BeforeEach
    void setUp() {
        Role testRole = Role.builder()
                .id(1L)
                .name(RoleType.USER.getName())
                .enabled(true)
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("EncodedPassword123!")
                .role(testRole)
                .enabled(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .build();

        validTestToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
    }

    private String authorizationHeader(String token) {
        return "Bearer " + token;
    }

    @Nested
    @DisplayName("Login User Tests")
    class LoginUserTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void testLoginSuccess() throws Exception {
            AuthRequest request = new AuthRequest("testuser", "Admin123!");
            String testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "testuser",
                    "Admin123!",
                    testUser.getAuthorities()
            );

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);
            when(tokenProvider.generateToken(any(Authentication.class)))
                    .thenReturn(testToken);
            when(tokenProvider.getExpirationTime())
                    .thenReturn(86400000L);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", equalTo(testToken)))
                    .andExpect(jsonPath("$.username", equalTo("testuser")))
                    .andExpect(jsonPath("$.type", equalTo("Bearer")))
                    .andExpect(jsonPath("$.expiresIn", equalTo(86400000)));

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider).generateToken(any(Authentication.class));
        }

        @Test
        @DisplayName("Should return error on invalid credentials")
        void testLoginInvalidCredentials() throws Exception {
            AuthRequest request = new AuthRequest("testuser", "Admin123!");
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider, never()).generateToken(any());
        }

        @Test
        @DisplayName("Should reject malformed JSON")
        void testLoginMalformedJson() throws Exception {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json}"))
                    .andExpect(status().is4xxClientError());

            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("Should reject request with missing fields")
        void testLoginMissingFields() throws Exception {
            String requestBody = "{\"username\":\"testuser\"}";

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().is4xxClientError());

            verify(authenticationManager, never()).authenticate(any());
        }
    }
}


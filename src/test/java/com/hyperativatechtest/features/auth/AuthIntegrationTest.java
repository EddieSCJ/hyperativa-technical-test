package com.hyperativatechtest.features.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperativatechtest.features.auth.dto.AuthRequest;
import com.hyperativatechtest.features.auth.dto.AuthResponse;
import com.hyperativatechtest.features.auth.dto.UserRegistrationRequest;
import com.hyperativatechtest.features.auth.repository.UserRepository;
import com.hyperativatechtest.features.common.entity.User;
import com.hyperativatechtest.features.common.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Auth Integration Tests")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {}

    @Nested
    @DisplayName("User Registration and Login Flow")
    class RegistrationAndLoginFlow {

        @Test
        @DisplayName("Should successfully register a user and then login with correct credentials")
        void testCompleteAuthenticationFlow() throws Exception {
            // Step 1: Register a new user
            UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                    "integrationtestuser",
                    "SecurePassword123!",
                    "USER"
            );

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isCreated());

            // Verify user was created in the database
            User registeredUser = userRepository.findByUsername("integrationtestuser").orElse(null);
            assertNotNull(registeredUser, "User should be created in the database");
            assertEquals("integrationtestuser", registeredUser.getUsername());
            assertTrue(registeredUser.isEnabled());

            // Step 2: Login with the registered user
            AuthRequest loginRequest = new AuthRequest(
                    "integrationtestuser",
                    "SecurePassword123!"
            );

            MvcResult loginResult = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", notNullValue()))
                    .andReturn();

            // Extract the token from login response
            String loginResponseContent = loginResult.getResponse().getContentAsString();
            AuthResponse authResponse = objectMapper.readValue(loginResponseContent, AuthResponse.class);

            assertNotNull(authResponse.token(), "JWT token should be present in login response");
            assertTrue(authResponse.token().length() > 0, "JWT token should not be empty");
        }

        @Test
        @DisplayName("Should fail login with incorrect password")
        void testLoginWithIncorrectPassword() throws Exception {
            // Register a user first
            UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                    "testuser123",
                    "CorrectPassword123!",
                    "USER"
            );

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isCreated());

            // Try to login with wrong password
            AuthRequest wrongPasswordRequest = new AuthRequest(
                    "testuser123",
                    "WrongPassword123!"
            );

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should fail login with non-existent user")
        void testLoginWithNonExistentUser() throws Exception {
            AuthRequest loginRequest = new AuthRequest(
                    "nonexistentuser",
                    "SomePassword123!"
            );

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should prevent duplicate user registration")
        void testDuplicateUserRegistration() throws Exception {
            UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                    "duplicateuser",
                    "Password123!",
                    "USER"
            );

            // First registration should succeed
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isCreated());

            // Second registration with same username should fail
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Registration Validation Tests")
    class RegistrationValidation {

        @Test
        @DisplayName("Should reject registration with weak password")
        void testRegistrationWithWeakPassword() throws Exception {
            UserRegistrationRequest weakPasswordRequest = new UserRegistrationRequest(
                    "weakpassworduser",
                    "weak",  // Too weak
                    "USER"
            );

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(weakPasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", notNullValue()));
        }

        @Test
        @DisplayName("Should reject registration with invalid username format")
        void testRegistrationWithInvalidUsername() throws Exception {
            UserRegistrationRequest invalidUsernameRequest = new UserRegistrationRequest(
                    "",  // Empty username
                    "ValidPassword123!",
                    "USER"
            );

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidUsernameRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject registration with null fields")
        void testRegistrationWithNullFields() throws Exception {
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":null,\"password\":null,\"roleName\":null}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Multiple User Management")
    class MultipleUserManagement {

        @Test
        @DisplayName("Should handle multiple users independently")
        void testMultipleUsersIndependence() throws Exception {
            // Register first user
            UserRegistrationRequest user1Request = new UserRegistrationRequest(
                    "user1",
                    "User1Password123!",
                    "USER"
            );
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user1Request)))
                    .andExpect(status().isCreated());

            // Register second user
            UserRegistrationRequest user2Request = new UserRegistrationRequest(
                    "user2",
                    "User2Password123!",
                    "USER"
            );
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user2Request)))
                    .andExpect(status().isCreated());

            // User 1 should be able to login with their password
            AuthRequest user1Login = new AuthRequest("user1", "User1Password123!");
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user1Login)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", notNullValue()));

            // User 2 should be able to login with their password
            AuthRequest user2Login = new AuthRequest("user2", "User2Password123!");
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user2Login)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", notNullValue()));

            // User 1 should not be able to login with User 2's password
            AuthRequest user1WithUser2Password = new AuthRequest("user1", "User2Password123!");
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user1WithUser2Password)))
                    .andExpect(status().isUnauthorized());

            assertTrue(userRepository.findByUsername("user1").isPresent());
            assertTrue(userRepository.findByUsername("user2").isPresent());
        }
    }
}


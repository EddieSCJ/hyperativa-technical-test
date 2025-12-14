package com.hyperativatechtest.features.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperativatechtest.config.TestAwsConfig;
import com.hyperativatechtest.config.TestRabbitMqConfig;
import com.hyperativatechtest.features.auth.dto.AuthRequest;
import com.hyperativatechtest.features.auth.dto.UserRegistrationRequest;
import com.hyperativatechtest.features.auth.repository.UserRepository;
import com.hyperativatechtest.features.common.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestRabbitMqConfig.class, TestAwsConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    void setUp() {
        userRepository.deleteAll();

        if (roleRepository.findByName("USER").isEmpty()) {
            com.hyperativatechtest.features.common.entity.Role userRole = new com.hyperativatechtest.features.common.entity.Role();
            userRole.setName("USER");
            userRole.setDescription("Regular user with standard permissions");
            userRole.setEnabled(true);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName("ADMIN").isEmpty()) {
            com.hyperativatechtest.features.common.entity.Role adminRole = new com.hyperativatechtest.features.common.entity.Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator with full system access");
            adminRole.setEnabled(true);
            roleRepository.save(adminRole);
        }
    }

    @Test
    @DisplayName("Should verify roles exist before tests")
    void testRolesExist() {
        assertTrue(roleRepository.findByName("USER").isPresent(), "USER role should exist");
        assertTrue(roleRepository.findByName("ADMIN").isPresent(), "ADMIN role should exist");
    }

    @Test
    @DisplayName("Should reject registration without authentication")
    void testRegisterWithoutAuthentication() throws Exception {
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                "testuser",
                "SecurePassword123!",
                "USER"
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isForbidden());
    }

    @Nested
    @DisplayName("Login Flow Tests")
    class LoginFlowTests {

        @Test
        @DisplayName("Should fail login with incorrect password")
        void testLoginWithIncorrectPassword() throws Exception {
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
    }
}


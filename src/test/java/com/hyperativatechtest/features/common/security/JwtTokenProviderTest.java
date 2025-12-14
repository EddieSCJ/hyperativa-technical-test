package com.hyperativatechtest.features.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private Authentication testAuth;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "test-secret-key-that-is-long-enough-for-jwt-signing");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86400000L);

        testAuth = mock(Authentication.class, org.mockito.Mockito.withSettings().lenient());
        when(testAuth.getName()).thenReturn("testuser");
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        when(testAuth.getAuthorities()).thenReturn((Collection) authorities);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        String token = jwtTokenProvider.generateToken(testAuth);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        String token = jwtTokenProvider.generateToken(testAuth);

        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        String token = jwtTokenProvider.generateToken(testAuth);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject invalid token")
    void shouldRejectInvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", -1L);

        String expiredToken = jwtTokenProvider.generateToken(testAuth);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertFalse(isValid);
    }


    @Test
    @DisplayName("Should handle empty token")
    void shouldHandleEmptyToken() {
        assertFalse(jwtTokenProvider.validateToken(""));
        assertFalse(jwtTokenProvider.validateToken(null));
    }
}

package com.hyperativatechtest.features.auth.dto;

public record AuthResponse(
    String token,
    String type,
    String username,
    long expiresIn
) {
    public static final String DEFAULT_AUTH_TYPE = "Bearer";

    public static AuthResponse of(String token, String username, long expiresIn) {
        return new AuthResponse(token, DEFAULT_AUTH_TYPE, username, expiresIn);
    }
}

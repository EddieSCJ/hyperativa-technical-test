package com.hyperativatechtest.features.auth.dto;

import com.hyperativatechtest.features.auth.dto.validations.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,

    @StrongPassword
    @NotBlank(message = "Password is required")
    String password
) {}


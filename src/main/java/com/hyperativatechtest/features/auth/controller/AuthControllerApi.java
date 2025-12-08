package com.hyperativatechtest.features.auth.controller;

import com.hyperativatechtest.features.auth.controller.swagger.examples.AuthErrorExample;
import com.hyperativatechtest.features.auth.controller.swagger.examples.LoginExample;
import com.hyperativatechtest.features.auth.controller.swagger.examples.RegisterExample;
import com.hyperativatechtest.features.auth.dto.UserRegistrationRequest;
import com.hyperativatechtest.features.auth.dto.AuthRequest;
import com.hyperativatechtest.features.auth.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
public interface AuthControllerApi {

    @PostMapping("/register")
    @Operation(summary = "Register a new user (ADMIN only)")
    @RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserRegistrationRequest.class),
            examples = @ExampleObject(value = RegisterExample.REQUEST)
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Register Response",
                    value = RegisterExample.RESPONSE_201
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = AuthErrorExample.VALIDATION_ERROR_400)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = AuthErrorExample.USERNAME_EXISTS_409)
            )
        )
    })
    ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest);

    @PostMapping("/login")
    @Operation(summary = "Login user and get JWT token")
    @RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = AuthRequest.class),
            examples = @ExampleObject(value = LoginExample.REQUEST)
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Login Response",
                    value = LoginExample.RESPONSE_200
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = AuthErrorExample.INVALID_CREDENTIALS_401)
            )
        )
    })
    ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest);
}


package com.hyperativatechtest.features.auth.controller;

import com.hyperativatechtest.features.auth.dto.UserRegistrationRequest;
import com.hyperativatechtest.features.auth.dto.AuthRequest;
import com.hyperativatechtest.features.auth.dto.AuthResponse;
import com.hyperativatechtest.features.common.entity.User;
import com.hyperativatechtest.features.common.security.JwtTokenProvider;
import com.hyperativatechtest.features.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthControllerApi {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        log.debug("Registering new user: {}", userRegistrationRequest.username());
        User user = userService.registerUser(userRegistrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AuthResponse(null, "Bearer", user.getUsername(), tokenProvider.getExpirationTime())
        );
    }

    @Override
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.debug("Login attempt for user: {}", authRequest.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        String token = tokenProvider.generateToken(authentication);
        log.debug("User logged in successfully: {}", authRequest.username());

        return ResponseEntity.ok(AuthResponse.of(
                token,
                authRequest.username(),
                tokenProvider.getExpirationTime()
        ));
    }
}

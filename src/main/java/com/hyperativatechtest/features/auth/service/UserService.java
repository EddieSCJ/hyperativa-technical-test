package com.hyperativatechtest.features.auth.service;

import com.hyperativatechtest.features.auth.dto.UserRegistrationRequest;
import com.hyperativatechtest.features.common.entity.Role;
import com.hyperativatechtest.features.common.entity.User;
import com.hyperativatechtest.features.common.entity.RoleType;
import com.hyperativatechtest.features.common.repository.RoleRepository;
import com.hyperativatechtest.features.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User createUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        RoleType roleType = parseRoleType(request.roleName());
        Role role = findRoleByType(roleType);

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        log.info("Registering new user: {} with role: {}", request.username(), roleType.getName());
        return userRepository.save(user);
    }

    private RoleType parseRoleType(String roleName) {
        try {
            return RoleType.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            String available = String.join(", ", Arrays.stream(RoleType.values()).map(Enum::name).toList());
            throw new IllegalArgumentException("Invalid role: " + roleName + ". Available: " + available);
        }
    }

    private Role findRoleByType(RoleType roleType) {
        return roleRepository.findByName(roleType.getName())
                .orElseThrow(() -> new RuntimeException(
                        "Role '" + roleType.getName() + "' not found in database"));
    }
}

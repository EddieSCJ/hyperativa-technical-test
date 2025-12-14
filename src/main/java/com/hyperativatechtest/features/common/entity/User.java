package com.hyperativatechtest.features.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Column(name = "account_non_expired", nullable = false)
    @Builder.Default
    private Boolean accountNonExpired = true;

    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (Objects.isNull(enabled)) enabled = true;
        if (Objects.isNull(accountNonLocked)) accountNonLocked = true;
        if (Objects.isNull(credentialsNonExpired)) credentialsNonExpired = true;
        if (Objects.isNull(accountNonExpired)) accountNonExpired = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName().toUpperCase()));
    }

    @Override
    public boolean isAccountNonLocked() {
        if (Objects.isNull(accountNonLocked) || !accountNonLocked) {
            return false;
        }

        if (Objects.isNull(lockedUntil)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isAccountNonExpired() {
        return Objects.nonNull(accountNonExpired) && accountNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Objects.nonNull(credentialsNonExpired) && credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return Objects.nonNull(enabled) && enabled;
    }
}


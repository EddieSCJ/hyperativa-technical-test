package com.hyperativatechtest.features.common.logging;

import com.hyperativatechtest.features.common.entity.AuditLog;
import com.hyperativatechtest.features.common.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.OffsetDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private final AuditLogRepository auditLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(AuditConstants.REQUEST_START_TIME_ATTR, System.currentTimeMillis());

        String username = getUsername();
        log.debug("API Request: {} {} by user: {}", request.getMethod(), request.getRequestURI(), username);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                          Object handler, ModelAndView modelAndView) {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(AuditConstants.REQUEST_START_TIME_ATTR);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        String username = getUsername();
        String action = buildAction(request);
        int status = response.getStatus();

        String details = String.format("Status: %d, Duration: %dms", status, duration);
        if (ex != null) {
            details += ", Error: " + ex.getMessage();
        }

        log.debug("API Response: {} {} - Status: {}, Duration: {}ms",
                request.getMethod(), request.getRequestURI(), status, duration);

        try {
            if (shouldAuditLog(request.getRequestURI())) {
                AuditLog auditLog = AuditLog.builder()
                        .username(username)
                        .action(action)
                        .entityType(EntityType.fromUri(request.getRequestURI()).getValue())
                        .details(maskSensitiveData(details))
                        .createdAt(OffsetDateTime.now())
                        .build();

                auditLogRepository.save(auditLog);
            }
        } catch (Exception e) {
            log.warn("Failed to save audit log: {}", e.getMessage());
        }
    }

    private String buildAction(HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI();
    }

    private boolean shouldAuditLog(String uri) {
        return Arrays.stream(AuditConstants.EXCLUDED_URI_PATTERNS)
                .noneMatch(uri::contains);
    }

    private String getUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() &&
                !AuditConstants.SECURITY_PRINCIPAL_ANONYMOUS.equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception e) {
            log.warn("Failed to get username from security context: {}", e.getMessage());
        }
        return AuditConstants.ANONYMOUS_USER;
    }

    private String maskSensitiveData(String data) {
        if (data == null) {
            return null;
        }

        String cardNumberPattern = String.format("\\b\\d{%d,%d}\\b",
            AuditConstants.CARD_NUMBER_MIN_DIGITS,
            AuditConstants.CARD_NUMBER_MAX_DIGITS);
        return data.replaceAll(cardNumberPattern, AuditConstants.CARD_NUMBER_MASK);
    }
}


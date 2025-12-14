package com.hyperativatechtest.features.common.logging;

public class AuditConstants {

    public static final String ANONYMOUS_USER = "anonymous";
    public static final String SECURITY_PRINCIPAL_ANONYMOUS = "anonymousUser";

    public static final String REQUEST_START_TIME_ATTR = "requestStartTime";

    public static final String[] EXCLUDED_URI_PATTERNS = {
        "/h2-console",
        "/swagger",
        "/v3/api-docs"
    };

    public static final String CARD_NUMBER_MASK = "****MASKED****";
    public static final int CARD_NUMBER_MIN_DIGITS = 13;
    public static final int CARD_NUMBER_MAX_DIGITS = 19;

    private AuditConstants() {}
}


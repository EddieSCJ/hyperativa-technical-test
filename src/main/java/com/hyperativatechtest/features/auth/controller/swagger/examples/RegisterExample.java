package com.hyperativatechtest.features.auth.controller.swagger.examples;

public class RegisterExample {

    public static final String REQUEST = """
        {
            "username": "alice",
            "password": "securePassword123"
        }
        """;

    public static final String RESPONSE_201 = """
        {
            "username": "alice",
            "type": "Bearer",
            "expiresIn": 86400000
        }
        """;
}


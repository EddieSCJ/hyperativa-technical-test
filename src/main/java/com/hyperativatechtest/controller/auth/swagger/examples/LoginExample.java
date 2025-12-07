package com.hyperativatechtest.controller.auth.swagger.examples;

public class LoginExample {

    public static final String REQUEST = """
        {
            "username": "alice",
            "password": "securePassword123"
        }
        """;

    public static final String RESPONSE_200 = """
        {
            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbGljZSIsImlhdCI6MTczNTEwMDAwMCwiZXhwIjoxNzM1MTg2NDAwfQ.signature...",
            "type": "Bearer",
            "username": "alice",
            "expiresIn": 86400000
        }
        """;
}


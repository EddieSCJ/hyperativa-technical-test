package com.hyperativatechtest.features.auth.controller.swagger.examples;

public class LoginExample {

    public static final String REQUEST = """
        {
            "username": "admin",
            "password": "Admin123!"
        }
        """;

    public static final String RESPONSE_200 = """
        {
            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczNTEwMDAwMCwiZXhwIjoxNzM1MTg2NDAwfQ.signature...",
            "type": "Bearer",
            "username": "admin",
            "expiresIn": 86400000
        }
        """;
}


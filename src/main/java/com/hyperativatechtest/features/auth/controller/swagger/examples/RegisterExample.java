package com.hyperativatechtest.features.auth.controller.swagger.examples;

public class RegisterExample {

    public static final String REQUEST = """
        {
            "username": "admin",
            "password": "Admin123!"
        }
        """;

    public static final String RESPONSE_201 = """
        {
            "username": "admin",
            "type": "Bearer",
            "expiresIn": 86400000
        }
        """;
}


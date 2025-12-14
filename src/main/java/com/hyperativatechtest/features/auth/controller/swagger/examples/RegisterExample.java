package com.hyperativatechtest.features.auth.controller.swagger.examples;

public class RegisterExample {

    public static final String REQUEST = """
        {
            "username": "user1",
            "password": "Password123!",
            "roleName": "USER"
        }
        """;

    public static final String RESPONSE_201 = """
        {
            "username": "user1",
            "type": "Bearer",
            "expiresIn": 86400000
        }
        """;
}


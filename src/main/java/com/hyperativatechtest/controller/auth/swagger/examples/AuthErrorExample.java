package com.hyperativatechtest.controller.auth.swagger.examples;

public class AuthErrorExample {

    public static final String VALIDATION_ERROR_400 = """
        {
            "status": 400,
            "error": "Bad Request",
            "message": "Validation Error",
            "path": "/auth/register",
            "details": ["Username must be between 8 and 50 characters", "Password must contain: at least 8 characters, 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character (!@#$%^&*)"]
        }
        """;

    public static final String USERNAME_EXISTS_409 = """
        {
            "status": 409,
            "error": "Conflict",
            "message": "Username already exists",
            "path": "/auth/register"
        }
        """;

    public static final String INVALID_CREDENTIALS_401 = """
        {
            "status": 401,
            "error": "Unauthorized",
            "message": "Invalid username or password",
            "path": "/auth/login"
        }
        """;
}


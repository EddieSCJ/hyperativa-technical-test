package com.hyperativatechtest.features.auth.dto.validations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Strong Password Validator Tests")
class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
        validator.initialize(null);
    }

    @Nested
    @DisplayName("Valid Passwords")
    class ValidPasswordTests {

        @Test
        @DisplayName("Should accept password: Admin123!")
        void testValidPassword1() {
            assertTrue(validator.isValid("Admin123!", null));
        }

        @Test
        @DisplayName("Should accept password: MyPass@2024")
        void testValidPassword2() {
            assertTrue(validator.isValid("MyPass@2024", null));
        }

        @Test
        @DisplayName("Should accept password: Pass@123")
        void testValidPassword3() {
            assertTrue(validator.isValid("Pass@123", null));
        }

        @Test
        @DisplayName("Should accept password with multiple special chars: Pass!@#123")
        void testValidPasswordMultipleSpecial() {
            assertTrue(validator.isValid("Pass!@#123", null));
        }

        @Test
        @DisplayName("Should accept long strong password")
        void testValidLongPassword() {
            assertTrue(validator.isValid("MySecure@2024Password!Extra123", null));
        }
    }

    @Nested
    @DisplayName("Invalid Passwords")
    class InvalidPasswordTests {

        @Test
        @DisplayName("Should reject password without uppercase: admin123!")
        void testNoUppercase() {
            assertFalse(validator.isValid("admin123!", null));
        }

        @Test
        @DisplayName("Should reject password without lowercase: ADMIN123!")
        void testNoLowercase() {
            assertFalse(validator.isValid("ADMIN123!", null));
        }

        @Test
        @DisplayName("Should reject password without digit: AdminPass!")
        void testNoDigit() {
            assertFalse(validator.isValid("AdminPass!", null));
        }

        @Test
        @DisplayName("Should reject password without special char: Admin123")
        void testNoSpecialChar() {
            assertFalse(validator.isValid("Admin123", null));
        }

        @Test
        @DisplayName("Should reject password too short: Pass@12 (7 chars)")
        void testTooShort() {
            assertFalse(validator.isValid("Pass@12", null));
        }

        @Test
        @DisplayName("Should reject password with invalid special char: Admin123-")
        void testInvalidSpecialChar() {
            assertFalse(validator.isValid("Admin123-", null));
        }

        @Test
        @DisplayName("Should reject very weak password: pass")
        void testVeryWeak() {
            assertFalse(validator.isValid("pass", null));
        }
    }

    @Nested
    @DisplayName("Null Password")
    class NullPasswordTests {

        @Test
        @DisplayName("Should return false for null password")
        void testNullPassword() {
            assertFalse(validator.isValid(null, null));
        }
    }
}


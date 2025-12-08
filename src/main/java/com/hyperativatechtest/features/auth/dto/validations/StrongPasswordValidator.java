package com.hyperativatechtest.features.auth.dto.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Password strength requirements enforced:
 * <ul>
 *   <li><strong>At least 8 characters:</strong> Passwords shorter than 8 characters have significantly lower entropy.
 *       Even with SHA-256 hashing, short passwords are vulnerable to:
 *       <ul>
 *         <li>Rainbow table attacks: Online databases (e.g., <a href="https://10015.io/tools/sha256-encrypt-decrypt">Encryption & Decryption Playground</a>)
 *             contain pre-computed hashes for common short passwords, making them instantly crackable</li>
 *         <li>Dictionary attacks: Common words/patterns with fewer characters are trivial to crack</li>
 *       </ul>
 *   </li>
 *   <li><strong>At least 1 uppercase letter:</strong> Increases character set from 26 to 52 (a-z + A-Z),
 *       doubling the base entropy and making rainbow table attacks less effective</li>
 *   <li><strong>At least 1 lowercase letter:</strong> Ensures mix of case, preventing simple substitution patterns</li>
 *   <li><strong>At least 1 number (0-9):</strong> Adds numeric characters to the mix, expanding the keyspace
 *       and defeating simple pattern attacks</li>
 *   <li><strong>At least 1 special character (!@#$%^&*):</strong> Non-alphanumeric characters significantly
 *       reduce the effectiveness of dictionary attacks and increase overall entropy by expanding the character set</li>
 * </ul>
 *
 * <strong>Why this matters:</strong>
 * A password like "pass" (4 chars) can be cracked in milliseconds using online SHA-256 rainbow tables.
 * A password like "Password123!" (12 chars with mixed case, number, special) would require:
 * <ul>
 *   <li>~620 trillion possible combinations to brute force</li>
 *   <li>Years of computation even at 1 billion attempts/second</li>
 *   <li>Not found in pre-computed rainbow tables due to complexity</li>
 * </ul>
 *
 * Reference: NIST SP 800-63B (Digital Identity Guidelines)
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])"
            + "(?=.*[a-z])"
            + "(?=.*\\d)"
            + "(?=.*[!@#$%^&*])"
            + ".{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public void initialize(StrongPassword constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        boolean isValid = pattern.matcher(password).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Password must contain: at least 8 characters, 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character (!@#$%^&*)"
            ).addConstraintViolation();
        }

        return isValid;
    }
}


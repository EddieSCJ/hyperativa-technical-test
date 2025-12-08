package com.hyperativatechtest.features.auth.dto.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * <strong>Why we need this:</strong>
 * <p>
 * Even with bcrypt, a strong password matters. Here's how fast weak passwords get cracked:
 * </p>
 * <table border="1" cellpadding="8">
 *   <tr>
 *     <th>Password</th>
 *     <th>Combinations</th>
 *     <th>Crack Time (GPU Brute Force)</th>
 *   </tr>
 *   <tr>
 *     <td><code>pass</code> (4 chars, lowercase only)</td>
 *     <td>456,976</td>
 *     <td>0.005 seconds</td>
 *   </tr>
 *   <tr>
 *     <td><code>pass123</code> (7 chars, lowercase + digits)</td>
 *     <td>62,523,502,209</td>
 *     <td>~3 minutes</td>
 *   </tr>
 *   <tr>
 *     <td><code>Pass123</code> (7 chars, mixed case + digits)</td>
 *     <td>213,814,916,720,276</td>
 *     <td>~11 days</td>
 *   </tr>
 *   <tr>
 *     <td><code>Pass123!</code> (8 chars, all requirements)</td>
 *     <td>1,625,702,400,900,000,000</td>
 *     <td>~52,000 years</td>
 *   </tr>
 *   <tr>
 *     <td><code>MySecure@2024!</code> (14 chars, all requirements)</td>
 *     <td>79,496,847,203,390,844,000,000,000,000</td>
 *     <td>~2 billion years</td>
 *   </tr>
 * </table>
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    /**
     * Regex pattern that validates password strength.
     * <p>
     * <strong>Pattern breakdown:</strong>
     * <ul>
     *   <li><code>(?=.*[A-Z])</code> - Must contain at least 1 uppercase letter (A-Z)</li>
     *   <li><code>(?=.*[a-z])</code> - Must contain at least 1 lowercase letter (a-z)</li>
     *   <li><code>(?=.*\\d)</code> - Must contain at least 1 digit (0-9)</li>
     *   <li><code>(?=.*[!@#$%^&*])</code> - Must contain at least 1 special character (!@#$%^&*)</li>
     *   <li><code>.{8,}</code> - Must be at least 8 characters long</li>
     * </ul>
     */
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


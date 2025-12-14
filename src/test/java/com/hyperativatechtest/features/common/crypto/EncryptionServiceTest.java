package com.hyperativatechtest.features.common.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EncryptionService Tests")
class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() throws Exception {
        encryptionService = new EncryptionService();

        // Set the encryption secret through reflection
        ReflectionTestUtils.setField(encryptionService, "encryptionSecret", "test-secret-key-that-is-long-enough-for-aes-256");

        // Call init to initialize the secretKey
        encryptionService.init();
    }

    @Test
    @DisplayName("Should encrypt and decrypt text successfully")
    void shouldEncryptAndDecryptTextSuccessfully() {
        String originalText = "4456897999999999";

        String encrypted = encryptionService.encrypt(originalText);
        String decrypted = encryptionService.decrypt(encrypted);

        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should generate consistent hash for same input")
    void shouldGenerateConsistentHashForSameInput() {
        String input = "4456897999999999";

        String hash1 = encryptionService.hash(input);
        String hash2 = encryptionService.hash(input);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(hash1, hash2);
        assertNotEquals(input, hash1);
    }

    @Test
    @DisplayName("Should generate different hashes for different inputs")
    void shouldGenerateDifferentHashesForDifferentInputs() {
        String input1 = "4456897999999999";
        String input2 = "4456897922969999";

        String hash1 = encryptionService.hash(input1);
        String hash2 = encryptionService.hash(input2);

        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Should handle null input for encryption")
    void shouldHandleNullInputForEncryption() {
        assertThrows(RuntimeException.class, () -> encryptionService.encrypt(null));
    }

    @Test
    @DisplayName("Should handle null input for hashing")
    void shouldHandleNullInputForHashing() {
        assertThrows(RuntimeException.class, () -> encryptionService.hash(null));
    }

    @Test
    @DisplayName("Should handle empty string")
    void shouldHandleEmptyString() {
        String encrypted = encryptionService.encrypt("");
        String decrypted = encryptionService.decrypt(encrypted);
        String hash = encryptionService.hash("");

        assertNotNull(encrypted);
        assertEquals("", decrypted);
        assertNotNull(hash);
    }

    @Test
    @DisplayName("Should handle long text")
    void shouldHandleLongText() {
        String longText = "4456897999999999".repeat(100);

        String encrypted = encryptionService.encrypt(longText);
        String decrypted = encryptionService.decrypt(encrypted);
        String hash = encryptionService.hash(longText);

        assertNotNull(encrypted);
        assertEquals(longText, decrypted);
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }
}


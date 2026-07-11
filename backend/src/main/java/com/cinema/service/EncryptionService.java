package com.cinema.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/*
 * Reversible encryption for sensitive fields (payment card numbers)
 * using only the Java standard library (AES-GCM). No external dependency.
 *
 * Stored format:  enc$<base64( iv[12] + ciphertext )>
 *
 * The key is derived (SHA-256) from an environment variable
 * CARD_ENCRYPTION_KEY when present, otherwise a default demo key.
 * Card numbers must be encrypted before storage and are only ever
 * returned to clients in masked form (last 4 digits).
 */
@Service
public class EncryptionService {

    private static final String PREFIX = "enc$";
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;

    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public EncryptionService() {
        String secret = System.getenv().getOrDefault(
                "CARD_ENCRYPTION_KEY", "cinja-default-demo-encryption-key");
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            this.key = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize encryption key", e);
        }
    }

    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(PREFIX);
    }

    // Encrypt a plaintext value into the storable "enc$..." format.
    public String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_BYTES];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
            return PREFIX + Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to encrypt value", e);
        }
    }

    // Decrypt an "enc$..." value back to plaintext. Plain (legacy) values pass through.
    public String decrypt(String stored) {
        if (stored == null) {
            return null;
        }
        if (!isEncrypted(stored)) {
            return stored;
        }
        try {
            byte[] combined = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
            byte[] iv = new byte[IV_BYTES];
            byte[] cipherText = new byte[combined.length - IV_BYTES];
            System.arraycopy(combined, 0, iv, 0, IV_BYTES);
            System.arraycopy(combined, IV_BYTES, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to decrypt value", e);
        }
    }

    // Show only the last 4 digits, e.g. "**** **** **** 1111".
    public String mask(String storedOrPlain) {
        String plain = decrypt(storedOrPlain);
        if (plain == null || plain.length() < 4) {
            return "****";
        }
        return "**** **** **** " + plain.substring(plain.length() - 4);
    }
}

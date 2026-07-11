package com.cinema.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/*
 * Stateless, self-contained tokens for email verification and password reset,
 * built with an HMAC signature (Java standard library only).
 *
 * Because the token carries its own signed payload, we do NOT need a database
 * table or in-memory store to validate it. A token cannot be forged without the
 * secret, and reset tokens carry an expiry timestamp.
 *
 * Token format:  base64url(payload) + "." + base64url(hmac(payload))
 * Verify payload: "<type>:<userId>:<issuedOrExpiry>"
 */
@Service
public class TokenService {

    private static final long RESET_VALIDITY_MS = 60 * 60 * 1000L; // 1 hour

    private final byte[] secret;

    public TokenService() {
        String s = System.getenv().getOrDefault(
                "TOKEN_SECRET", "cinja-default-token-secret-change-me");
        this.secret = s.getBytes(StandardCharsets.UTF_8);
    }

    // ---- Email verification tokens ----

    public String generateVerificationToken(String userId) {
        String payload = "verify:" + userId + ":" + System.currentTimeMillis();
        return sign(payload);
    }

    // Returns the userId if valid, otherwise null.
    public String validateVerificationToken(String token) {
        String payload = verify(token);
        if (payload == null) {
            return null;
        }
        String[] parts = payload.split(":", 3);
        if (parts.length != 3 || !parts[0].equals("verify")) {
            return null;
        }
        return parts[1];
    }

    // ---- Password reset tokens (with expiry) ----

    public String generateResetToken(String userId) {
        long expiry = System.currentTimeMillis() + RESET_VALIDITY_MS;
        String payload = "reset:" + userId + ":" + expiry;
        return sign(payload);
    }

    // Returns the userId if valid and not expired, otherwise null.
    public String validateResetToken(String token) {
        String payload = verify(token);
        if (payload == null) {
            return null;
        }
        String[] parts = payload.split(":", 3);
        if (parts.length != 3 || !parts[0].equals("reset")) {
            return null;
        }
        long expiry = Long.parseLong(parts[2]);
        if (System.currentTimeMillis() > expiry) {
            return null;
        }
        return parts[1];
    }

    // ---- Internal signing / verification ----

    private String sign(String payload) {
        String encodedPayload = base64Url(payload.getBytes(StandardCharsets.UTF_8));
        String signature = base64Url(hmac(payload));
        return encodedPayload + "." + signature;
    }

    private String verify(String token) {
        if (token == null || !token.contains(".")) {
            return null;
        }
        String[] parts = token.split("\\.", 2);
        byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[0]);
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);
        String expectedSig = base64Url(hmac(payload));
        if (!constantTimeEquals(expectedSig, parts[1])) {
            return null;
        }
        return payload;
    }

    private byte[] hmac(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to sign token", e);
        }
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}

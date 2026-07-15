package com.cinema.controller;

import com.cinema.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/*
 * Authentication endpoints: registration, email verification, login, logout,
 * and password reset. Delegates all logic to UserService.
 *
 * Base URL: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "null"},
        allowCredentials = "true"
)
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/auth/register  -> create an (unverified) account + send confirmation email
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        boolean promotions = request.promotions() != null && request.promotions();
        return userService.register(request.firstName(), request.lastName(),
                request.email(), request.password(), promotions);
    }

    // GET /api/auth/verify?token=...  -> clicked from the confirmation email
    @GetMapping(value = "/verify", produces = MediaType.TEXT_HTML_VALUE)
    public String verify(@RequestParam String token) {
        Object message = userService.verifyAccount(token).get("message");
        return "<html><body style=\"font-family:sans-serif;text-align:center;margin-top:60px;\">"
                + "<h2>" + message + "</h2>"
                + "<p>You may now return to Cinja Box Office and log in.</p>"
                + "</body></html>";
    }

    // POST /api/auth/login  -> validate credentials, start a session, return role
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request, HttpSession session) {
        Map<String, Object> result = userService.login(request.email(), request.password());
        session.setAttribute("userId", result.get("userId"));
        session.setAttribute("role", result.get("role"));
        return result;
    }

    // POST /api/auth/logout  -> end the session
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "You have been logged out.");
    }

    // POST /api/auth/forgot-password  -> email a reset link (if the account exists)
    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody EmailRequest request) {
        return userService.forgotPassword(request.email());
    }

    // POST /api/auth/reset-password  -> set a new password using the reset token
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request.token(), request.newPassword());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleAuthError(ResponseStatusException exception) {
        String message = exception.getReason() != null
                ? exception.getReason()
                : "Authentication request failed";

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(Map.of(
                        "message", message,
                        "status", exception.getStatusCode().value()
                ));
    }

    // ---- Request bodies ----
    public record RegisterRequest(String firstName, String lastName, String email,
                                  String password, Boolean promotions) {
    }

    public record LoginRequest(String email, String password) {
    }

    public record EmailRequest(String email) {
    }

    public record ResetPasswordRequest(String token, String newPassword) {
    }
}

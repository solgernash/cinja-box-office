package com.cinema.service;

import com.cinema.model.AccountState;
import com.cinema.model.Address;
import com.cinema.model.Admin;
import com.cinema.model.Customer;
import com.cinema.model.User;
import com.cinema.repository.AddressRepository;
import com.cinema.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/*
 * Core account business logic: registration, verification, login/logout,
 * password reset/change, and profile editing.
 *
 * Security notes:
 *  - Passwords are stored hashed (PasswordService).
 *  - New accounts start INACTIVE and become ACTIVE only after email verification.
 *  - Email verification / password reset use stateless signed tokens (TokenService).
 *  - Profile changes trigger a notification email (EmailService).
 */
@Service
public class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final PaymentCardService paymentCardService;
    private final FavoriteService favoriteService;

    public UserService(UserRepository userRepository,
                       AddressRepository addressRepository,
                       PasswordService passwordService,
                       TokenService tokenService,
                       EmailService emailService,
                       PaymentCardService paymentCardService,
                       FavoriteService favoriteService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.paymentCardService = paymentCardService;
        this.favoriteService = favoriteService;
    }

    // ---------------- Registration ----------------

    /*
     * Creates a new customer with status INACTIVE and emails a verification link.
     * NOTE: promotionsOptIn cannot be persisted yet because the Customer model has
     * no field for it (owned by a teammate); it is accepted but ignored for now.
     */
    public Map<String, Object> register(String firstName, String lastName,
                                        String email, String rawPassword,
                                        boolean promotionsOptIn) {
        requireText(firstName, "First name is required");
        requireText(lastName, "Last name is required");
        requireText(email, "Email is required");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email format is invalid");
        }
        validatePassword(rawPassword);

        if (userRepository.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }

        Customer customer = new Customer(null, firstName, lastName, email,
                passwordService.hash(rawPassword), AccountState.INACTIVE);
        Customer saved = userRepository.save(customer);

        String token = tokenService.generateVerificationToken(saved.getUser_ID());
        emailService.sendVerificationEmail(email, token);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Account created. Please check your email to verify your account.");
        result.put("userId", saved.getUser_ID());
        result.put("status", AccountState.INACTIVE);
        result.put("loggedIn", false);
        return result;
    }

    // ---------------- Verification ----------------

    public Map<String, Object> verifyAccount(String token) {
        String userId = tokenService.validateVerificationToken(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired verification link");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (user instanceof Customer customer) {
            customer.setAccountState(AccountState.ACTIVE);
            userRepository.save(customer);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Account verified successfully. You can now log in.");
        result.put("status", AccountState.ACTIVE);
        return result;
    }

    // ---------------- Login / Logout ----------------

    /*
     * Validates credentials and, on success, records the user in the session.
     * Returns the user's role so the frontend can navigate to the right home page.
     */
    public Map<String, Object> login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordService.matches(rawPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        // Customers must be verified (ACTIVE) before they can log in.
        if (user instanceof Customer customer && customer.getAccountState() != AccountState.ACTIVE) {
            if (customer.getAccountState() == AccountState.SUSPENDED) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been suspended.");
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account is not verified. Please check your email to verify your account.");
        }
        String role = (user instanceof Admin) ? "ADMIN" : "CUSTOMER";

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", user.getUser_ID());
        result.put("firstName", user.getFirstName());
        result.put("lastName", user.getLastName());
        result.put("email", user.getEmail());
        result.put("role", role);
        return result;
    }

    // ---------------- Password reset (forgot) ----------------

    // Always reports success so we never reveal whether an email is registered.
    public Map<String, Object> forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String token = tokenService.generateResetToken(user.getUser_ID());
            emailService.sendPasswordResetEmail(email, token);
        }
        return Map.of("message", "If an account exists for that email, a reset link has been sent.");
    }

    public Map<String, Object> resetPassword(String token, String newPassword) {
        String userId = tokenService.validateResetToken(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset link");
        }
        validatePassword(newPassword);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        user.setPasswordHash(passwordService.hash(newPassword));
        userRepository.save(user);
        emailService.sendProfileChangedEmail(user.getEmail());
        return Map.of("message", "Password has been reset. You can now log in.");
    }

    // ---------------- Change password (logged in) ----------------

    public Map<String, Object> changePassword(String userId, String currentPassword, String newPassword) {
        User user = requireUser(userId);
        if (!passwordService.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        validatePassword(newPassword);
        user.setPasswordHash(passwordService.hash(newPassword));
        userRepository.save(user);
        emailService.sendProfileChangedEmail(user.getEmail());
        return Map.of("message", "Password changed successfully.");
    }

    // ---------------- Profile ----------------

    // Full profile for the edit-profile page: core info + address + masked cards + favorites.
    public Map<String, Object> getProfile(String userId) {
        User user = requireUser(userId);
        Map<String, Object> profile = sanitizeUser(user);
        profile.put("address", addressRepository.findByCustomerUserId(userId));
        profile.put("paymentCards", paymentCardService.listCards(userId));
        profile.put("favorites", favoriteService.listFavorites(userId));
        return profile;
    }

    /*
     * Updates editable profile fields. The email address is intentionally
     * NOT editable (rubric requirement), so any email in the request is ignored.
     */
    public Map<String, Object> updateProfile(String userId, String firstName, String lastName) {
        User user = requireUser(userId);
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);
        }
        userRepository.save(user);
        emailService.sendProfileChangedEmail(user.getEmail());
        return sanitizeUser(user);
    }

    // Save or replace the customer's single address (max one per customer).
    public Address saveAddress(String userId, String street, String city, String state, String zipCode) {
        requireCustomer(userId);
        Address existing = addressRepository.findByCustomerUserId(userId);
        Address address = (existing != null) ? existing : new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        address.setCustomer(requireCustomer(userId));
        Address saved = addressRepository.save(address);
        emailService.sendProfileChangedEmail(requireUser(userId).getEmail());
        return saved;
    }

    // ---------------- Admin ----------------

    // All users (customers + admins), sanitized (no password hashes).
    public List<Map<String, Object>> listAllUsers() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            list.add(sanitizeUser(user));
        }
        return list;
    }

    // Admin action: change a customer's account state (e.g. SUSPENDED / ACTIVE).
    public Map<String, Object> setCustomerState(String userId, AccountState state) {
        Customer customer = requireCustomer(userId);
        customer.setAccountState(state);
        userRepository.save(customer);
        return sanitizeUser(customer);
    }

    // ---------------- Helpers ----------------

    private User requireUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Customer requireCustomer(String userId) {
        User user = requireUser(userId);
        if (!(user instanceof Customer customer)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This action is only available to customers");
        }
        return customer;
    }

    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userId", user.getUser_ID());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("email", user.getEmail());
        map.put("role", (user instanceof Admin) ? "ADMIN" : "CUSTOMER");
        if (user instanceof Customer customer) {
            map.put("accountState", customer.getAccountState());
        }
        return map;
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    // Minimum policy: at least 8 characters, containing a letter and a digit.
    private void validatePassword(String password) {
        if (password == null || password.length() < 8
                || !password.matches(".*[A-Za-z].*")
                || !password.matches(".*\\d.*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters and include a letter and a number");
        }
    }
}

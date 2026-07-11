package com.cinema.controller;

import com.cinema.model.Address;
import com.cinema.model.Movie;
import com.cinema.service.FavoriteService;
import com.cinema.service.PaymentCardService;
import com.cinema.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
 * User self-service endpoints: view/edit profile, change password, manage the
 * single address, manage payment cards (max 3), and manage favorite movies.
 *
 * Base URL: /api/users/{id}
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final PaymentCardService paymentCardService;
    private final FavoriteService favoriteService;

    public UserController(UserService userService,
                          PaymentCardService paymentCardService,
                          FavoriteService favoriteService) {
        this.userService = userService;
        this.paymentCardService = paymentCardService;
        this.favoriteService = favoriteService;
    }

    // ---- Profile ----

    // GET /api/users/{id}  -> full profile (info + address + masked cards + favorites)
    @GetMapping("/{id}")
    public Map<String, Object> getProfile(@PathVariable String id) {
        return userService.getProfile(id);
    }

    // PUT /api/users/{id}  -> update editable fields (email is NOT editable)
    @PutMapping("/{id}")
    public Map<String, Object> updateProfile(@PathVariable String id,
                                             @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(id, request.firstName(), request.lastName());
    }

    // PUT /api/users/{id}/password  -> change password (requires current password)
    @PutMapping("/{id}/password")
    public Map<String, Object> changePassword(@PathVariable String id,
                                              @RequestBody ChangePasswordRequest request) {
        return userService.changePassword(id, request.currentPassword(), request.newPassword());
    }

    // ---- Address (max one per customer) ----

    // PUT /api/users/{id}/address  -> create or replace the customer's address
    @PutMapping("/{id}/address")
    public Address saveAddress(@PathVariable String id, @RequestBody AddressRequest request) {
        return userService.saveAddress(id, request.street(), request.city(),
                request.state(), request.zipCode());
    }

    // ---- Payment cards (max three per customer) ----

    @GetMapping("/{id}/cards")
    public List<Map<String, Object>> listCards(@PathVariable String id) {
        return paymentCardService.listCards(id);
    }

    @PostMapping("/{id}/cards")
    public Map<String, Object> addCard(@PathVariable String id, @RequestBody CardRequest request) {
        return paymentCardService.addCard(id, request.cardNumber(),
                request.cardholderName(), request.expirationDate());
    }

    @PutMapping("/{id}/cards/{cardId}")
    public Map<String, Object> updateCard(@PathVariable String id, @PathVariable String cardId,
                                          @RequestBody CardRequest request) {
        return paymentCardService.updateCard(id, cardId, request.cardNumber(),
                request.cardholderName(), request.expirationDate());
    }

    @DeleteMapping("/{id}/cards/{cardId}")
    public Map<String, Object> deleteCard(@PathVariable String id, @PathVariable String cardId) {
        paymentCardService.deleteCard(id, cardId);
        return Map.of("message", "Payment card removed.");
    }

    // ---- Favorite movies ----

    @GetMapping("/{id}/favorites")
    public List<Movie> listFavorites(@PathVariable String id) {
        return favoriteService.listFavorites(id);
    }

    @PostMapping("/{id}/favorites")
    public Map<String, Object> addFavorite(@PathVariable String id, @RequestBody FavoriteRequest request) {
        favoriteService.addFavorite(id, request.movieId());
        return Map.of("message", "Movie added to favorites.");
    }

    @DeleteMapping("/{id}/favorites/{movieId}")
    public Map<String, Object> removeFavorite(@PathVariable String id, @PathVariable String movieId) {
        favoriteService.removeFavorite(id, movieId);
        return Map.of("message", "Movie removed from favorites.");
    }

    // ---- Request bodies ----
    public record UpdateProfileRequest(String firstName, String lastName) {
    }

    public record ChangePasswordRequest(String currentPassword, String newPassword) {
    }

    public record AddressRequest(String street, String city, String state, String zipCode) {
    }

    public record CardRequest(String cardNumber, String cardholderName, String expirationDate) {
    }

    public record FavoriteRequest(String movieId) {
    }
}

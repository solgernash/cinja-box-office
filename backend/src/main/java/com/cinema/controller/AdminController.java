package com.cinema.controller;

import com.cinema.model.AccountState;
import com.cinema.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
 * Admin data endpoints used by the admin portal.
 *
 * NOTE: The admin portal HOME PAGE itself (the menu with Manage Movies,
 * Promotions, Users, Showtimes) is a UI/frontend prototype and is not part of
 * the controller/service layer. These endpoints provide the data that portal
 * would use (e.g. listing/suspending users).
 *
 * Base URL: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/admin/users  -> list all users (no password hashes)
    @GetMapping("/users")
    public List<Map<String, Object>> listUsers() {
        return userService.listAllUsers();
    }

    // PUT /api/admin/users/{id}/suspend  -> suspend a customer account
    @PutMapping("/users/{id}/suspend")
    public Map<String, Object> suspend(@PathVariable String id) {
        return userService.setCustomerState(id, AccountState.SUSPENDED);
    }

    // PUT /api/admin/users/{id}/activate  -> re-activate a customer account
    @PutMapping("/users/{id}/activate")
    public Map<String, Object> activate(@PathVariable String id) {
        return userService.setCustomerState(id, AccountState.ACTIVE);
    }
}

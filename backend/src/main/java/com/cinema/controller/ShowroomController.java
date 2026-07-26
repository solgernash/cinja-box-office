package com.cinema.controller;

import com.cinema.service.ShowroomService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
 * Showrooms (used by the admin scheduling workflow to pick a room).
 * Base URL: /api/showrooms
 */
@RestController
@RequestMapping("/api/showrooms")
@CrossOrigin(origins = "*")
public class ShowroomController {

    private final ShowroomService showroomService;

    public ShowroomController(ShowroomService showroomService) {
        this.showroomService = showroomService;
    }

    // GET /api/showrooms  -> all showrooms
    @GetMapping
    public List<Map<String, Object>> listShowrooms() {
        return showroomService.listShowrooms();
    }
}

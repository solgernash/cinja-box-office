package com.cinema.controller;

import com.cinema.service.ShowService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
 * Showtimes: admin scheduling and user-facing showtime visibility.
 * Base URL: /api/shows
 */
@RestController
@RequestMapping("/api/shows")
@CrossOrigin(origins = "*")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    // POST /api/shows  -> admin schedules a showtime (with conflict prevention)
    @PostMapping
    public Map<String, Object> schedule(@RequestBody ScheduleShowRequest request) {
        return showService.scheduleShow(request.movieId(), request.showroomNumber(),
                request.date(), request.time(), request.duration());
    }

    // GET /api/shows/movie/{movieId}  -> showtimes for a movie (user portal)
    @GetMapping("/movie/{movieId}")
    public List<Map<String, Object>> getShowsForMovie(@PathVariable String movieId) {
        return showService.getShowsForMovie(movieId);
    }

    // GET /api/shows/{showId}  -> a single show's details
    @GetMapping("/{showId}")
    public Map<String, Object> getShow(@PathVariable String showId) {
        return showService.getShowDto(showId);
    }

    public record ScheduleShowRequest(String movieId, int showroomNumber,
                                      String date, String time, int duration) {
    }
}

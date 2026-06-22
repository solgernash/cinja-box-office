package com.cinema.controller;

import com.cinema.model.Movie;
import com.cinema.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    // Spring injects the service at startup.
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // GET /api/movies -> Home Page
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    // GET /api/movies/{id} -> one movie's full details;
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable String id) {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/movies/search?title=... -> search by title
    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam(required = false) String title) {
        return movieService.searchByTitle(title);
    }

    // GET /api/movies/filter?genre=... -> filter by genre
    @GetMapping("/filter")
    public List<Movie> filterMovies(@RequestParam String genre) {
        return movieService.filterByGenre(genre);
    }

    // GET /api/movies/status/{status} -> movies by status: COMING SOON
    @GetMapping("/status/{status}")
    public List<Movie> getMoviesByStatus(@PathVariable String status) {
        return movieService.getMoviesByStatus(status);
    }
}

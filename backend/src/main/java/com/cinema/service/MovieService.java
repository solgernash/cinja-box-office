package com.cinema.service;

import com.cinema.model.Movie;
import com.cinema.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    // Spring hands us the repository
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Home Page
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Movie Details
    public Optional<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    // Search: (case insensitive).
    // A blank/empty query returns everything so the home page stays populated.
    public List<Movie> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            return movieRepository.findAll();
        }
        return movieRepository.findByTitleContainingIgnoreCase(title.trim());
    }

    // Filter: (case insensitive).
    public List<Movie> filterByGenre(String genre) {
        return movieRepository.findByGenreIgnoreCase(genre);
    }

    // Home Page categories: COMING SOON
    public List<Movie> getMoviesByStatus(String status) {
        return movieRepository.findByStatusIgnoreCase(status);
    }

    // Admin: add a new movie. Validates the required fields, then stores it.
    public Movie createMovie(Movie movie) {
        if (movie == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie data is required");
        }
        if (movie.getTitle() == null || movie.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (movie.getGenre() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre is required");
        }
        if (movie.getStatus() == null || movie.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Status is required (e.g. RUNNING or COMING_SOON)");
        }
        movie.setId(null); // ensure a new document is created
        return movieRepository.save(movie);
    }
}

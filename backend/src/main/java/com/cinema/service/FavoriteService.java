package com.cinema.service;

import com.cinema.model.Customer;
import com.cinema.model.Favorite;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.repository.FavoriteRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Manages a customer's favorite movies.
 * A customer adds favorites while browsing; favorites persist in the database.
 */
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           MovieRepository movieRepository,
                           UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public Favorite addFavorite(String userId, String movieId) {
        Customer customer = requireCustomer(userId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        // Avoid duplicate favorites for the same movie.
        for (Favorite existing : favoriteRepository.findByCustomerUser_ID(userId)) {
            if (existing.getMovie() != null && movieId.equals(existing.getMovie().getId())) {
                return existing;
            }
        }

        Favorite favorite = new Favorite(new Date());
        favorite.setCustomer(customer);
        favorite.setMovie(movie);
        return favoriteRepository.save(favorite);
    }

    // Returns the favorited movies for display.
    public List<Movie> listFavorites(String userId) {
        List<Movie> movies = new ArrayList<>();
        for (Favorite favorite : favoriteRepository.findByCustomerUser_ID(userId)) {
            if (favorite.getMovie() != null) {
                movies.add(favorite.getMovie());
            }
        }
        return movies;
    }

    public void removeFavorite(String userId, String movieId) {
        for (Favorite favorite : favoriteRepository.findByCustomerUser_ID(userId)) {
            if (favorite.getMovie() != null && movieId.equals(favorite.getMovie().getId())) {
                favoriteRepository.delete(favorite);
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie is not in your favorites");
    }

    private Customer requireCustomer(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!(user instanceof Customer customer)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only customers can have favorites");
        }
        return customer;
    }
}

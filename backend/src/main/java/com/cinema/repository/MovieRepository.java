package com.cinema.repository;

import com.cinema.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;    //marks this interface as a spring repository component

import java.util.List;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    /* MongoRepository will automatically provide the implementations at runtime */

    //  Search movies by title
    List<Movie> findByTitleContainingIgnoreCase(String title);

    // Filter by genre
    List<Movie> findByGenreIgnoreCase(String genre);

    // Filter by status ( whether a movie is RUNNING / COMING_SOON)
    List<Movie> findByStatusIgnoreCase(String status);


} //end interface

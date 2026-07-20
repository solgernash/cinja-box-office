package com.cinema.repository;

import com.cinema.model.Movie;
import com.cinema.model.Show;
import com.cinema.model.Showroom;

import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from Spring Data MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a Spring repository component

import java.sql.Time;
import java.util.Date;
import java.util.List;


@Repository
public interface ShowRepository extends MongoRepository<Show, String> {


    //Retrieves all showtimes associated with a specific movie
    //Used for user portal showtime visibility
    List<Show> findByMovie(Movie movie);



    //Checks whether a showroom already has a show scheduled
    //at the same date and time to prevent scheduling conflicts
    List<Show> findByShowroomAndShowDateAndShowTime(
            Showroom showroom,
            Date showDate,
            Time showTime
    );

}

package com.cinema.repository;

import com.cinema.model.Seat;
import com.cinema.model.Showroom;

import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from Spring Data MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a Spring repository component

import java.util.List;


@Repository
public interface SeatRepository extends MongoRepository<Seat, String> {


    //Retrieves all seats inside a specific showroom
    //Used for displaying the seat map during booking
    List<Seat> findByShowroom(Showroom showroom);


}
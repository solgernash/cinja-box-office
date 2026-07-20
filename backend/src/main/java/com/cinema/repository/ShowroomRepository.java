package com.cinema.repository;

import com.cinema.model.Showroom;
import com.cinema.model.Theatre;

import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from Spring Data MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a Spring repository component

import java.util.List;


@Repository
public interface ShowroomRepository extends MongoRepository<Showroom, String> {


    //Retrieves all showrooms belonging to a specific theatre
    List<Showroom> findByTheatre(Theatre theatre);



    //Finds a showroom using its showroom number
    //Used by admin when scheduling a movie
    Showroom findByShowroomNumber(int showroomNumber);

}
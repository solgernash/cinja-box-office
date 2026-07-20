package com.cinema.repository;

import com.cinema.model.Theatre;

import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from Spring Data MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a Spring repository component


@Repository
public interface TheatreRepository extends MongoRepository<Theatre, String> {


    //Finds a theatre using its name
    //Useful when selecting a theatre for showroom management
    Theatre findByTheatreName(String theatreName);



    //Finds a theatre using its location
    //Useful if multiple theatre locations are supported in the future
    Theatre findByTheatreLocation(String theatreLocation);


}
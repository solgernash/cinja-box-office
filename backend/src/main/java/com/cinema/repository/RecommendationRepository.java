package com.cinema.repository;

import com.cinema.model.Recommendation;
import com.cinema.model.Customer;

import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from Spring Data MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a Spring repository component

import java.util.List;


@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {


    //Retrieves all recommendations generated for a specific customer
    //Matches UML relationship: Customer receives Recommendation
    List<Recommendation> findByCustomer(Customer customer);


}

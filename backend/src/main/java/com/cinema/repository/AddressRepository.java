package com.cinema.repository;

import com.cinema.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from SpringData MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a spring repository component

import org.springframework.data.mongodb.repository.Query;


@Repository
public interface AddressRepository extends MongoRepository<Address, String> {

   @Query("{ 'customer.user_ID': ?0 }")
   Address findByCustomerUserId(String userId);

}
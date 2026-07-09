package com.cinema.repository;

import com.cinema.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from SpringData MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a spring repository component



@Repository
public interface AddressRepository extends MongoRepository<Address, String> {
   Address findByCustomerUser_ID(String user_ID);

}
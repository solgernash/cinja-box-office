package com.cinema.repository;

import com.cinema.model.PaymentCard;
import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from SpringData MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a spring repository component

import java.util.List;

import org.springframework.data.mongodb.repository.Query;


@Repository
public interface PaymentCardRepository extends MongoRepository<PaymentCard, String> {

    @Query("{ 'customer.user_ID': ?0 }")
    List<PaymentCard> findByCustomerUserId(String userId);

}

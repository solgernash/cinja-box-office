package com.cinema.repository;

import com.cinema.model.Promotion;

import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from Spring Data MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a Spring repository component


@Repository
public interface PromotionRepository extends MongoRepository<Promotion, String> {


    //Finds a promotion using its promo code
    //Used when applying discounts during checkout
    Promotion findByPromoCode(String promoCode);


}

package com.cinema.repository;

import com.cinema.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from SpringData MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a spring repository component

import java.util.List;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    List<Favorite> findByCustomerUser_ID(String user_ID);

}

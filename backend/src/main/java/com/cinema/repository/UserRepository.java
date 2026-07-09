package com.cinema.repository;

import com.cinema.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from SpringData MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a spring repository component


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);
}


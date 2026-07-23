package com.cinema.repository;

import com.cinema.model.ShowSeat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowSeatRepository extends MongoRepository<ShowSeat, String> {

}
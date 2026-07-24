package com.cinema.repository;

import com.cinema.model.Show;
import com.cinema.model.ShowSeat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShowSeatRepository extends MongoRepository<ShowSeat, String> {
    List<ShowSeat> findByShow(Show show);
}
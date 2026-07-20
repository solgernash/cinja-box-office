package com.cinema.repository;

import com.cinema.model.Booking;
import com.cinema.model.Customer;
import com.cinema.model.Show;

import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from Spring Data MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a Spring repository component

import java.util.List;


@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {


    //Retrieves all bookings made by a customer
    //Used for viewing purchase history
    List<Booking> findByCustomer(Customer customer);



    //Retrieves bookings associated with a specific show
    //Useful for checking booked seats and preventing duplicate seat selection
    List<Booking> findByShow(Show show);


}

package com.cinema.repository;

import com.cinema.model.Booking;
import com.cinema.model.Seat;
import com.cinema.model.Ticket;
import com.cinema.model.Show;

import org.springframework.data.mongodb.repository.MongoRepository;   //imports the MongoDB interface (MongoRepository) from Spring Data MongoDB
import org.springframework.stereotype.Repository;    //marks this interface as a Spring repository component

import java.util.List;


@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {


    //Retrieves all tickets belonging to a booking
    //Used for checkout order summary
    List<Ticket> findByBooking(Booking booking);



    //Retrieves tickets assigned to a specific show
    //Used to determine booked seats
    List<Ticket> findByShow(Show show);



    //Finds whether a seat already has a ticket assigned
    //Used to prevent selecting unavailable seats
    Ticket findBySeat(Seat seat);


}

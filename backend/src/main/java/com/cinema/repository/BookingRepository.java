package com.cinema.repository;

import com.cinema.model.Booking;
import com.cinema.model.Show;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    /*
     * Customer is embedded in a booking document. Query its id rather than
     * comparing the complete embedded object, since the other customer fields
     * may have changed since the booking was created.
     */
    @Query(value = "{ 'customer.user_ID': ?0 }", sort = "{ 'bookingDate': -1 }")
    List<Booking> findOrderHistoryByCustomerId(String customerId);

    //Retrieves bookings associated with a specific show
    //Useful for checking booked seats and preventing duplicate seat selection
    List<Booking> findByShow(Show show);
}

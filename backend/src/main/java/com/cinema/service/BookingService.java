package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.Customer;
import com.cinema.model.Show;
import com.cinema.pattern.BookingBuilder;
import com.cinema.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

/*
 * Creates bookings using the BookingBuilder (Builder design pattern) instead of
 * constructing Booking objects with many setters.
 *
 * For the Sprint 3 minimum the checkout stops at the payment mockup, so this
 * persists a pending booking (no payment reference, not cancelled). Only minimal
 * customer/show references are attached to keep the stored document small and
 * free of the two-way object cycles present in the model.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(String customerId, String showId, double totalBeforeTax) {
        Customer customerRef = new Customer();
        customerRef.setUser_ID(customerId);

        Show showRef = new Show();
        showRef.setShowId(showId);

        Booking booking = new BookingBuilder()
                .setBookingNumber(null)          // MongoDB generates the id
                .setBookingDate(new Date())
                .setTotalOrderPrice(totalBeforeTax)
                .setTax(0.0)
                .setBookingFee(0.0)
                .setPaymentReference(null)       // no payment yet (mockup)
                .setCancelled(false)
                .setCustomer(customerRef)
                .setShow(showRef)
                .build();

        return bookingRepository.save(booking);
    }
}

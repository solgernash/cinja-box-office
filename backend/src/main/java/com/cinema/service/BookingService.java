package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.Customer;
import com.cinema.model.Show;
import com.cinema.pattern.BookingBuilder;
import com.cinema.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

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
                .setBookingNumber(null) // MongoDB generates the id
                .setBookingDate(new Date())
                .setTotalOrderPrice(totalBeforeTax)
                .setTax(0.0)
                .setBookingFee(0.0)
                .setPaymentReference(null) // no payment yet (mockup)
                .setCancelled(false)
                .setCustomer(customerRef)
                .setShow(showRef)
                .build();

        return bookingRepository.save(booking);
    }

    /*
     * Overload used by the CheckoutFacade (final-demo flow). Builds a booking for
     * a customer via the Builder pattern; show/tickets/totals are attached by the
     * facade flow. Kept minimal so the facade compiles and runs when uncommented.
     */
    public Booking createBooking(Customer customer) {
        Customer customerRef = new Customer();
        customerRef.setUser_ID(customer != null ? customer.getUser_ID() : null);

        Booking booking = new BookingBuilder()
                .setBookingNumber(null)
                .setBookingDate(new Date())
                .setTotalOrderPrice(0.0)
                .setTax(0.0)
                .setBookingFee(0.0)
                .setPaymentReference(null)
                .setCancelled(false)
                .setCustomer(customerRef)
                .build();

        return bookingRepository.save(booking);
    }
}

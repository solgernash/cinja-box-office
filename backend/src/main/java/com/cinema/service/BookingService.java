package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.Customer;
import com.cinema.model.OrderHistoryItem;
import com.cinema.model.Show;
import com.cinema.pattern.BookingBuilder;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.ShowRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;

    public BookingService(BookingRepository bookingRepository, ShowRepository showRepository) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
    }

    public Booking createBooking(String customerId, String showId, double totalBeforeTax) {
        return createBooking(customerId, showId, totalBeforeTax, 0);
    }

    public Booking createBooking(String customerId, String showId,
            double totalBeforeTax, int ticketCount) {
        Customer customerRef = new Customer();
        customerRef.setUser_ID(customerId);

        Show showRef = new Show();
        showRef.setShowId(showId);

        Booking booking = new BookingBuilder()
                .setBookingNumber(nextBookingNumber())
                .setBookingDate(new Date())
                .setTotalOrderPrice(totalBeforeTax)
                .setTax(0.0)
                .setBookingFee(0.0)
                .setPaymentReference(null) // no payment yet (mockup)
                .setCancelled(false)
                .setCustomer(customerRef)
                .setShow(showRef)
                .build();

        booking.setTicketCount(Math.max(0, ticketCount));
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

    public List<OrderHistoryItem> getOrderHistory(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A customer id is required to retrieve order history");
        }
        return bookingRepository.findOrderHistoryByCustomerId(customerId)
                .stream()
                .map(this::toOrderHistoryItem)
                .toList();
    }

    private OrderHistoryItem toOrderHistoryItem(Booking booking) {
        Show show = booking.getShow();
        if (show != null && show.getShowId() != null) {
            show = showRepository.findById(show.getShowId()).orElse(show);
        }

        return new OrderHistoryItem(
                booking.getBookingNumber(),
                booking.getBookingDate(),
                amount(booking.getTotalOrderPrice()),
                amount(booking.getTax()),
                amount(booking.getBookingFee()),
                booking.isCancelled(),
                show != null ? show.getShowId() : null,
                show != null && show.getMovie() != null ? show.getMovie().getTitle() : null,
                show != null ? show.getShowDate() : null,
                show != null && show.getShowTime() != null ? show.getShowTime().toString() : null,
                show != null && show.getShowroom() != null
                        ? show.getShowroom().getShowroomNumber()
                        : null,
                booking.getTicketCount() > 0
                        ? booking.getTicketCount()
                        : booking.getTickets() != null ? booking.getTickets().size() : 0);
    }

    private double amount(Double value) {
        return value != null ? value : 0.0;
    }

    private String nextBookingNumber() {
        long sequence = bookingRepository.count() + 1;
        String bookingNumber;

        do {
            bookingNumber = String.format("BOOKING %04d", sequence++);
        } while (bookingRepository.existsById(bookingNumber));

        return bookingNumber;
    }
}

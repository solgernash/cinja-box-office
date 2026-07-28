package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.OrderHistoryItem;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.ShowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final ShowRepository showRepository = mock(ShowRepository.class);
    private final BookingService bookingService = new BookingService(bookingRepository, showRepository);

    @Test
    void getsCustomerOrderHistoryFromRepository() {
        Booking first = new Booking();
        first.setBookingNumber("booking-1");
        Booking second = new Booking();
        second.setBookingNumber("booking-2");
        when(bookingRepository.findOrderHistoryByCustomerId("customer-1"))
                .thenReturn(List.of(first, second));

        List<OrderHistoryItem> actual = bookingService.getOrderHistory("customer-1");

        assertEquals(List.of("booking-1", "booking-2"),
                actual.stream().map(OrderHistoryItem::bookingNumber).toList());
        verify(bookingRepository).findOrderHistoryByCustomerId("customer-1");
    }

    @Test
    void rejectsBlankCustomerId() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.getOrderHistory(" "));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createsDisplayBookingNumberAndStoresTicketCount() {
        when(bookingRepository.count()).thenReturn(1L);
        when(bookingRepository.existsById("BOOKING 0002")).thenReturn(false);
        when(bookingRepository.save(org.mockito.ArgumentMatchers.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.createBooking(
                "customer-1", "show-1", 35.0, 2);

        assertEquals("BOOKING 0002", booking.getBookingNumber());
        assertEquals(2, booking.getTicketCount());
        assertEquals(35.0, booking.getTotalOrderPrice());
    }
}

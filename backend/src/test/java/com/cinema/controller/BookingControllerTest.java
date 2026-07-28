package com.cinema.controller;

import com.cinema.model.OrderHistoryItem;
import com.cinema.service.BookingService;
import com.cinema.service.ShowService;
import jakarta.servlet.http.HttpSession;
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

class BookingControllerTest {

    private final ShowService showService = mock(ShowService.class);
    private final BookingService bookingService = mock(BookingService.class);
    private final HttpSession session = mock(HttpSession.class);
    private final BookingController controller = new BookingController(showService, bookingService);

    @Test
    void getsHistoryForLoggedInCustomer() {
        List<OrderHistoryItem> expected = List.of(new OrderHistoryItem(
                "booking-1", null, 20.0, 0.0, 0.0, false,
                "show-1", "Movie", null, null, null, 1));
        when(session.getAttribute("userId")).thenReturn("customer-1");
        when(bookingService.getOrderHistory("customer-1")).thenReturn(expected);

        List<OrderHistoryItem> actual = controller.getOrderHistory(session);

        assertSame(expected, actual);
        verify(bookingService).getOrderHistory("customer-1");
    }

    @Test
    void requiresLogin() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getOrderHistory(session));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void preventsReadingAnotherCustomersHistory() {
        when(session.getAttribute("userId")).thenReturn("customer-1");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getOrderHistory("customer-2", session));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }
}

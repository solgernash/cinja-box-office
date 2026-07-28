package com.cinema.controller;

import com.cinema.service.ShowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cinema.model.OrderHistoryItem;
import com.cinema.service.BookingService;

/*
 * Starts a booking: the user picks a showtime and enters how many tickets of
 * each age category. The in-progress order is stored in the HTTP session (the
 * user does not need to be logged in yet). Base URL: /api/bookings
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(
        originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "null"},
        allowCredentials = "true"
)
public class BookingController {

    private final ShowService showService;
    private final BookingService bookingService;

    public BookingController(ShowService showService, BookingService bookingService) {
        this.showService = showService;
        this.bookingService = bookingService;
    }

    // GET /api/bookings/history -> orders for the currently logged-in customer
    @GetMapping("/history")
    public List<OrderHistoryItem> getOrderHistory(HttpSession session) {
        return bookingService.getOrderHistory(requireLogin(session));
    }

    /*
     * Backward-compatible route for clients that already include the customer
     * id. The session check prevents one customer from requesting another
     * customer's history.
     */
    @GetMapping("/history/{customerId}")
    public List<OrderHistoryItem> getOrderHistory(@PathVariable String customerId, HttpSession session) {
        String loggedInCustomerId = requireLogin(session);
        if (!loggedInCustomerId.equals(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only view your own order history");
        }
        return bookingService.getOrderHistory(loggedInCustomerId);
    }

    // POST /api/bookings/start  -> choose show + ticket counts
    @PostMapping("/start")
    public Map<String, Object> start(@RequestBody StartBookingRequest request, HttpSession session) {
        Map<String, Object> show = showService.getShowDto(request.showId()); // 404s if not found

        int adult = Math.max(0, request.adult());
        int senior = Math.max(0, request.senior());
        int child = Math.max(0, request.child());
        int total = adult + senior + child;
        if (total < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select at least one ticket");
        }

        session.setAttribute("bk_showId", request.showId());
        session.setAttribute("bk_adult", adult);
        session.setAttribute("bk_senior", senior);
        session.setAttribute("bk_child", child);
        session.removeAttribute("bk_seatIds"); // new booking clears any prior seat hold

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Booking started. Please select your seats.");
        result.put("show", show);
        result.put("adultTickets", adult);
        result.put("seniorTickets", senior);
        result.put("childTickets", child);
        result.put("totalTickets", total);
        return result;
    }

    // GET /api/bookings/current  -> the in-progress selection held in the session
    @GetMapping("/current")
    public Map<String, Object> current(HttpSession session) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("showId", session.getAttribute("bk_showId"));
        result.put("adultTickets", intAttr(session, "bk_adult"));
        result.put("seniorTickets", intAttr(session, "bk_senior"));
        result.put("childTickets", intAttr(session, "bk_child"));
        result.put("selectedSeatIds", session.getAttribute("bk_seatIds"));
        return result;
    }

    private int intAttr(HttpSession session, String key) {
        Object value = session.getAttribute(key);
        return value instanceof Integer ? (Integer) value : 0;
    }

    private String requireLogin(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in to view your order history");
        }
        return userId.toString();
    }

    public record StartBookingRequest(String showId, int adult, int senior, int child) {
    }
}

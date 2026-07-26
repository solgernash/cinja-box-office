package com.cinema.controller;

import com.cinema.service.ShowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public BookingController(ShowService showService) {
        this.showService = showService;
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

    public record StartBookingRequest(String showId, int adult, int senior, int child) {
    }
}

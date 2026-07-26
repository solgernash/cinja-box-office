package com.cinema.controller;

import com.cinema.service.SeatService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Seat map display and seat selection for a show.
 * Seats can be selected without logging in; the in-progress selection is held
 * in the HTTP session so it survives the login step at checkout.
 * Base URL: /api/shows/{showId}
 */
@RestController
@RequestMapping("/api/shows")
@CrossOrigin(
        originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "null"},
        allowCredentials = "true"
)
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    // GET /api/shows/{showId}/seatmap  -> seat layout with availability
    @GetMapping("/{showId}/seatmap")
    public List<Map<String, Object>> seatMap(@PathVariable String showId) {
        return seatService.getSeatMap(showId);
    }

    // POST /api/shows/{showId}/select-seats  -> validate + reserve selected seats
    @PostMapping("/{showId}/select-seats")
    public Map<String, Object> selectSeats(@PathVariable String showId,
                                           @RequestBody SelectSeatsRequest request,
                                           HttpSession session) {
        String bookingShowId = (String) session.getAttribute("bk_showId");
        if (bookingShowId == null || !bookingShowId.equals(showId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start a booking for this show (choose tickets) before selecting seats");
        }

        int requiredCount = intAttr(session, "bk_adult") + intAttr(session, "bk_senior")
                + intAttr(session, "bk_child");

        @SuppressWarnings("unchecked")
        List<String> previouslyHeld = (List<String>) session.getAttribute("bk_seatIds");

        List<Map<String, Object>> reserved =
                seatService.reserveSeats(showId, request.showSeatIds(), requiredCount, previouslyHeld);

        session.setAttribute("bk_seatIds", new ArrayList<>(request.showSeatIds()));

        return Map.of(
                "message", "Seats reserved for your session.",
                "reservedSeats", reserved);
    }

    private int intAttr(HttpSession session, String key) {
        Object value = session.getAttribute(key);
        return value instanceof Integer ? (Integer) value : 0;
    }

    public record SelectSeatsRequest(List<String> showSeatIds) {
    }
}

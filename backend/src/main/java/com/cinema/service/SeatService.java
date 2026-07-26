package com.cinema.service;

import com.cinema.model.Seat;
import com.cinema.model.ShowSeat;
import com.cinema.repository.ShowSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Seat map display and seat selection/reservation for a show.
 *
 * Availability is tracked per show via ShowSeat.available. Selecting seats marks
 * them unavailable (session-level reservation); real-time locking is a later
 * deliverable. Lookups use findAll() + filter for reliability (see ShowService).
 */
@Service
public class SeatService {

    private final ShowSeatRepository showSeatRepository;

    public SeatService(ShowSeatRepository showSeatRepository) {
        this.showSeatRepository = showSeatRepository;
    }

    // All seats for a show with their availability, for rendering the seat map.
    public List<Map<String, Object>> getSeatMap(String showId) {
        List<ShowSeat> showSeats = findByShowId(showId);
        if (showSeats.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No seats found for this show");
        }
        showSeats.sort(Comparator
                .comparing((ShowSeat ss) -> ss.getSeat() != null ? ss.getSeat().getRowNumber() : "")
                .thenComparingInt(ss -> ss.getSeat() != null ? ss.getSeat().getSeatNumber() : 0));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ShowSeat showSeat : showSeats) {
            result.add(toDto(showSeat));
        }
        return result;
    }

    /*
     * Reserve the given show-seats for a show:
     *  - releases any previously held seats (so the user can change selection)
     *  - validates the requested seats exist, belong to the show and are free
     *  - validates the count matches the number of tickets
     *  - marks the seats unavailable
     * Returns the reserved seat DTOs.
     */
    public List<Map<String, Object>> reserveSeats(String showId, List<String> requestedShowSeatIds,
                                                  int requiredCount, List<String> previouslyHeld) {
        if (requestedShowSeatIds == null || requestedShowSeatIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No seats selected");
        }
        if (requestedShowSeatIds.size() != requiredCount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You selected " + requestedShowSeatIds.size() + " seat(s) but ordered "
                            + requiredCount + " ticket(s)");
        }

        List<ShowSeat> showSeats = findByShowId(showId);

        // Release previously held seats for this show (re-selection support).
        if (previouslyHeld != null && !previouslyHeld.isEmpty()) {
            for (ShowSeat showSeat : showSeats) {
                if (previouslyHeld.contains(showSeat.getShowSeatId())) {
                    showSeat.setAvailable(true);
                    showSeatRepository.save(showSeat);
                }
            }
        }

        // Validate and reserve the requested seats.
        List<Map<String, Object>> reserved = new ArrayList<>();
        for (String requestedId : requestedShowSeatIds) {
            ShowSeat target = showSeats.stream()
                    .filter(ss -> requestedId.equals(ss.getShowSeatId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Seat " + requestedId + " does not belong to this show"));
            if (!target.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Seat " + seatLabel(target) + " is already taken");
            }
            target.setAvailable(false);
            showSeatRepository.save(target);
            reserved.add(toDto(target));
        }
        return reserved;
    }

    // ---------------- Helpers ----------------

    private List<ShowSeat> findByShowId(String showId) {
        List<ShowSeat> result = new ArrayList<>();
        for (ShowSeat showSeat : showSeatRepository.findAll()) {
            if (showSeat.getShow() != null && showId.equals(showSeat.getShow().getShowId())) {
                result.add(showSeat);
            }
        }
        return result;
    }

    private Map<String, Object> toDto(ShowSeat showSeat) {
        Seat seat = showSeat.getSeat();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("showSeatId", showSeat.getShowSeatId());
        map.put("seatId", seat != null ? seat.getSeatId() : null);
        map.put("row", seat != null ? seat.getRowNumber() : null);
        map.put("number", seat != null ? seat.getSeatNumber() : null);
        map.put("seatType", seat != null ? seat.getSeatType() : null);
        map.put("label", seatLabel(showSeat));
        map.put("available", showSeat.isAvailable());
        return map;
    }

    private String seatLabel(ShowSeat showSeat) {
        Seat seat = showSeat.getSeat();
        if (seat == null) {
            return "?";
        }
        return seat.getRowNumber() + seat.getSeatNumber();
    }
}

package com.cinema.service;

import com.cinema.model.Movie;
import com.cinema.model.Seat;
import com.cinema.model.Show;
import com.cinema.model.ShowSeat;
import com.cinema.model.Showroom;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.SeatRepository;
import com.cinema.repository.ShowRepository;
import com.cinema.repository.ShowSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Showtime scheduling (admin) and showtime visibility (users).
 *
 * Design notes:
 *  - Lookups use findAll() + in-memory filtering instead of the derived
 *    findByMovie()/findByShowroom() queries, because those match on full
 *    embedded sub-documents which is unreliable with the current model. At
 *    demo scale this is trivial and correct.
 *  - Scheduling prevents conflicts (same showroom + same date + same time).
 *  - When a show is scheduled, ShowSeat records are created for every seat in
 *    the showroom so the new show is immediately bookable.
 */
@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ShowroomService showroomService;
    private final SeatRepository seatRepository;
    private final ShowSeatRepository showSeatRepository;

    public ShowService(ShowRepository showRepository,
                       MovieRepository movieRepository,
                       ShowroomService showroomService,
                       SeatRepository seatRepository,
                       ShowSeatRepository showSeatRepository) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.showroomService = showroomService;
        this.seatRepository = seatRepository;
        this.showSeatRepository = showSeatRepository;
    }

    // ---------------- Admin: schedule a showtime ----------------

    public Map<String, Object> scheduleShow(String movieId, int showroomNumber,
                                             String dateStr, String timeStr, int duration) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        Showroom showroom = showroomService.requireByNumber(showroomNumber);

        Date showDate = parseDate(dateStr);
        Time showTime = parseTime(timeStr);
        if (duration <= 0) {
            duration = 120;
        }

        // Conflict check: same showroom + same date + same time.
        for (Show existing : showRepository.findAll()) {
            if (existing.getShowroom() != null
                    && existing.getShowroom().getShowroomNumber() == showroomNumber
                    && sameDay(existing.getShowDate(), showDate)
                    && sameTime(existing.getShowTime(), showTime)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Showroom " + showroomNumber + " already has a show at that date and time");
            }
        }

        Show show = new Show(null, showDate, showTime, duration);
        show.setMovie(movie);
        show.setShowroom(showroom);
        show.setTickets(new ArrayList<>());
        show.setBookings(new ArrayList<>());
        Show saved = showRepository.save(show);

        createShowSeatsFor(saved, showroom);

        return toDto(saved);
    }

    // Create one ShowSeat per seat in the showroom so the new show is bookable.
    private void createShowSeatsFor(Show show, Showroom showroom) {
        Show showRef = new Show();
        showRef.setShowId(show.getShowId());

        List<ShowSeat> showSeats = new ArrayList<>();
        for (Seat seat : seatRepository.findAll()) {
            if (seat.getShowroom() != null && showroom.getShowroomId() != null
                    && showroom.getShowroomId().equals(seat.getShowroom().getShowroomId())) {
                showSeats.add(new ShowSeat(null, showRef, seat, true));
            }
        }
        if (!showSeats.isEmpty()) {
            showSeatRepository.saveAll(showSeats);
        }
    }

    // ---------------- Users: showtime visibility ----------------

    public List<Map<String, Object>> getShowsForMovie(String movieId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Show show : showRepository.findAll()) {
            if (show.getMovie() != null && movieId.equals(show.getMovie().getId())) {
                result.add(toDto(show));
            }
        }
        return result;
    }

    public Map<String, Object> getShowDto(String showId) {
        return toDto(requireShow(showId));
    }

    public Show requireShow(String showId) {
        return showRepository.findById(showId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Show not found"));
    }

    // ---------------- Helpers ----------------

    private Map<String, Object> toDto(Show show) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("showId", show.getShowId());
        map.put("showDate", show.getShowDate() != null
                ? new SimpleDateFormat("yyyy-MM-dd").format(show.getShowDate()) : null);
        map.put("showTime", show.getShowTime() != null ? formatTime(show.getShowTime()) : null);
        map.put("showDuration", show.getShowDuration());
        if (show.getMovie() != null) {
            map.put("movieId", show.getMovie().getId());
            map.put("movieTitle", show.getMovie().getTitle());
        }
        if (show.getShowroom() != null) {
            map.put("showroomNumber", show.getShowroom().getShowroomNumber());
        }
        return map;
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Show date is required (yyyy-MM-dd)");
        }
        try {
            LocalDate localDate = LocalDate.parse(dateStr.trim());
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format, expected yyyy-MM-dd");
        }
    }

    private Time parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Show time is required (HH:mm)");
        }
        String value = timeStr.trim();
        if (value.length() == 5) {
            value = value + ":00"; // HH:mm -> HH:mm:ss
        }
        try {
            return Time.valueOf(value);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time format, expected HH:mm");
        }
    }

    private boolean sameDay(Date a, Date b) {
        if (a == null || b == null) {
            return false;
        }
        String fmt = "yyyy-MM-dd";
        return new SimpleDateFormat(fmt).format(a).equals(new SimpleDateFormat(fmt).format(b));
    }

    private boolean sameTime(Time a, Time b) {
        if (a == null || b == null) {
            return false;
        }
        return formatTime(a).equals(formatTime(b));
    }

    private String formatTime(Time time) {
        String s = time.toString(); // HH:mm:ss
        return s.length() >= 5 ? s.substring(0, 5) : s;
    }
}

package com.cinema.service;

import com.cinema.model.Showroom;
import com.cinema.repository.ShowroomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Read access to showrooms (used by the admin scheduling workflow and the
 * seat map). Returns DTOs to avoid serializing the two-way Showroom<->Seat links.
 */
@Service
public class ShowroomService {

    private final ShowroomRepository showroomRepository;

    public ShowroomService(ShowroomRepository showroomRepository) {
        this.showroomRepository = showroomRepository;
    }

    public List<Map<String, Object>> listShowrooms() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Showroom showroom : showroomRepository.findAll()) {
            result.add(toDto(showroom));
        }
        result.sort((a, b) -> Integer.compare((int) a.get("showroomNumber"), (int) b.get("showroomNumber")));
        return result;
    }

    // Look up a showroom by its number (used when the admin schedules a show).
    public Showroom requireByNumber(int showroomNumber) {
        Showroom showroom = showroomRepository.findByShowroomNumber(showroomNumber);
        if (showroom == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Showroom " + showroomNumber + " not found");
        }
        return showroom;
    }

    private Map<String, Object> toDto(Showroom showroom) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("showroomId", showroom.getShowroomId());
        map.put("showroomNumber", showroom.getShowroomNumber());
        map.put("totalSeats", showroom.getTotalSeats());
        map.put("theatreName", showroom.getTheatre() != null ? showroom.getTheatre().getTheatreName() : null);
        return map;
    }
}

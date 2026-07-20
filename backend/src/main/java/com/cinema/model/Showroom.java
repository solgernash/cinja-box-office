package com.cinema.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "showrooms")
public class Showroom {

    @Id
    private String showroomId;
    private int showroomNumber;
    private int totalSeats;

    private Theatre theatre; //composition
    private List<Seat> seats;
    private List<Show> shows;

    public Showroom() {

    }

    public Showroom(String showroomId, int showroomNumber, int totalSeats, Theatre theatre) {
        this.showroomId = showroomId;
        this.showroomNumber = showroomNumber;
        this.totalSeats = totalSeats;
        this.theatre = theatre;
    }

    public String getShowroomId() {
        return showroomId;
    }

    public void setShowroomId(String showroomId) {
        this.showroomId = showroomId;
    }

    public int getShowroomNumber() {
        return showroomNumber;
    }

    public void setShowroomNumber(int showroomNumber) {
        this.showroomNumber = showroomNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public void setTheatre(Theatre theatre) {
        this.theatre = theatre;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }
} //end class

package com.cinema.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "showSeats")
public class ShowSeat {

    @Id
    private String showSeatId;

    private Show show;
    private Seat seat;

    private boolean available;


    // Default constructor
    public ShowSeat() {

    }


    public ShowSeat(String showSeatId, Show show, Seat seat, boolean available) {
        this.showSeatId = showSeatId;
        this.show = show;
        this.seat = seat;
        this.available = available;
    }


    public String getShowSeatId() {
        return showSeatId;
    }


    public void setShowSeatId(String showSeatId) {
        this.showSeatId = showSeatId;
    }


    public Show getShow() {
        return show;
    }


    public void setShow(Show show) {
        this.show = show;
    }


    public Seat getSeat() {
        return seat;
    }


    public void setSeat(Seat seat) {
        this.seat = seat;
    }


    public boolean isAvailable() {
        return available;
    }


    public void setAvailable(boolean available) {
        this.available = available;
    }

}
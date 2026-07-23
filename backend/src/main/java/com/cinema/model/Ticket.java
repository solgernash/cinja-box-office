package com.cinema.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tickets")
public class Ticket {
    @Id
    private String ticketId;
    private Double ticketPrice;
    private TicketType ticketType;

    private Booking booking;
    private Seat seat;
    private Show show;
    private ShowSeat showSeat;

    public Ticket() {

    }

    public Ticket(String ticketId, Double ticketPrice, TicketType ticketType, Booking booking) {
        this.ticketId = ticketId;
        this.ticketPrice = ticketPrice;
        this.ticketType= ticketType;
        this.booking = booking;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public ShowSeat getShowSeat() {
        return showSeat;
    }

    public void setShowSeat(ShowSeat showSeat) {
        this.showSeat = showSeat;
    }
} //end class

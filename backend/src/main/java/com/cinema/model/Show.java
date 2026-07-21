package com.cinema.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Document(collection = "shows")
public class Show {
    @Id
    private String showId;

     private Movie movie;
     private List<Booking> bookings;
     private Showroom showroom;
    private List<Ticket> tickets;

     private Date showDate;
     private Time showTime;
     private int showDuration;


    public Show() {
    }




     public Show(String showId, Date showDate, Time showTime, int showDuration) {
         this.showId = showId;
         this.showDate = showDate;
         this.showTime = showTime;
         this.showDuration = showDuration;
     }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }


    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> booking) {
        this.bookings = booking;
    }

    public Showroom getShowroom() {
        return showroom;
    }

    public void setShowroom(Showroom showroom) {
        this.showroom = showroom;
    }


    public Time getShowTime() {
        return showTime;
    }

    public void setShowTime(Time showTime) {
        this.showTime = showTime;
    }

    public int getShowDuration() {
        return showDuration;
    }

    public void setShowDuration(int showDuration) {
        this.showDuration = showDuration;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
} //end class
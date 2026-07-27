package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.Ticket;
import com.cinema.model.TicketType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    public static final double ADULT_PRICE = 17.50;
    public static final double SENIOR_PRICE = 14.00;
    public static final double CHILD_PRICE = 10.00;

    public double priceFor(TicketType type) {
        return switch (type) {
            case ADULT -> ADULT_PRICE;
            case SENIOR -> SENIOR_PRICE;
            case CHILD -> CHILD_PRICE;
        };
    }

    /*
     * Used by the CheckoutFacade (final-demo flow). Minimal placeholder: returns
     * any tickets already attached to the booking (empty otherwise). Full ticket
     * generation/persistence will be completed for the final demo.
     */
    public List<Ticket> generateTickets(Booking booking) {
        if (booking != null && booking.getTickets() != null) {
            return booking.getTickets();
        }
        return new ArrayList<>();
    }
}

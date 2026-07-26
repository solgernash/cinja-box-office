package com.cinema.service;

import com.cinema.model.TicketType;
import org.springframework.stereotype.Service;

/*
 * Ticket pricing by age category. Used by the checkout order summary and booking.
 * (Ticket generation/persistence is part of the final-demo checkout flow and is
 * not required for the Sprint 3 minimum, which stops at the payment mockup.)
 */
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
}

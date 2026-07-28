package com.cinema.model;

import java.util.Date;

public record OrderHistoryItem(
        String bookingNumber,
        Date bookingDate,
        double totalOrderPrice,
        double tax,
        double bookingFee,
        boolean cancelled,
        String showId,
        String movieTitle,
        Date showDate,
        String showTime,
        Integer showroomNumber,
        int ticketCount) {
}

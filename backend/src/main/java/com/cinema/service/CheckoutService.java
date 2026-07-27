package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.model.Show;
import com.cinema.model.ShowSeat;
import com.cinema.model.TicketType;
import com.cinema.repository.ShowSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {

    private final ShowService showService;
    private final ShowSeatRepository showSeatRepository;
    private final TicketService ticketService;
    private final BookingService bookingService;
    private final EmailService emailService;

    public CheckoutService(ShowService showService,
            ShowSeatRepository showSeatRepository,
            TicketService ticketService,
            BookingService bookingService,
            EmailService emailService) {
        this.showService = showService;
        this.showSeatRepository = showSeatRepository;
        this.ticketService = ticketService;
        this.bookingService = bookingService;
        this.emailService = emailService;
    }

    public Map<String, Object> buildSummary(String showId, int adult, int senior, int child,
            List<String> selectedShowSeatIds, String userEmail) {
        Show show = showService.requireShow(showId);

        // Ticket lines (only categories that were ordered).
        List<Map<String, Object>> ticketLines = new ArrayList<>();
        double total = 0.0;
        total += addLine(ticketLines, TicketType.ADULT, adult);
        total += addLine(ticketLines, TicketType.SENIOR, senior);
        total += addLine(ticketLines, TicketType.CHILD, child);

        // Selected seat labels.
        List<String> seatLabels = new ArrayList<>();
        for (ShowSeat showSeat : showSeatRepository.findAll()) {
            if (selectedShowSeatIds != null && selectedShowSeatIds.contains(showSeat.getShowSeatId())
                    && showSeat.getSeat() != null) {
                seatLabels.add(showSeat.getSeat().getRowNumber() + showSeat.getSeat().getSeatNumber());
            }
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("movieTitle", show.getMovie() != null ? show.getMovie().getTitle() : null);
        summary.put("showDate", show.getShowDate() != null
                ? new SimpleDateFormat("yyyy-MM-dd").format(show.getShowDate())
                : null);
        summary.put("showTime", show.getShowTime() != null
                ? show.getShowTime().toString().substring(0, 5)
                : null);
        summary.put("showroomNumber", show.getShowroom() != null ? show.getShowroom().getShowroomNumber() : null);
        summary.put("selectedSeats", seatLabels);
        summary.put("tickets", ticketLines);
        summary.put("totalTickets", adult + senior + child);
        summary.put("totalBeforeTax", round(total));
        summary.put("email", userEmail);
        return summary;
    }

    public Map<String, Object> proceedToPayment(String customerId, String showId,
            Map<String, Object> summary, String email) {
        double totalBeforeTax = ((Number) summary.get("totalBeforeTax")).doubleValue();
        Booking booking = bookingService.createBooking(customerId, showId, totalBeforeTax);

        // Order confirmation email (logged if mail is not configured; never throws).
        emailService.sendCheckoutConfirmationEmail(email, summary, booking.getBookingNumber());

        Map<String, Object> payment = new LinkedHashMap<>();
        payment.put("bookingNumber", booking.getBookingNumber());
        payment.put("email", email);
        payment.put("totalBeforeTax", round(totalBeforeTax));
        payment.put("paymentPage", "MOCK");
        payment.put("message", "Payment page (mockup) - no payment is processed in this sprint. "
                + "A confirmation email has been sent.");
        return payment;
    }

    // ---------------- Helpers ----------------

    private double addLine(List<Map<String, Object>> lines, TicketType type, int count) {
        if (count <= 0) {
            return 0.0;
        }
        double pricePer = ticketService.priceFor(type);
        double subtotal = pricePer * count;
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("type", type);
        line.put("count", count);
        line.put("pricePerTicket", pricePer);
        line.put("subtotal", round(subtotal));
        lines.add(line);
        return subtotal;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Used by controllers to reject an empty/no-selection checkout.
    public void requireSelection(String showId, List<String> selectedShowSeatIds) {
        if (showId == null || selectedShowSeatIds == null || selectedShowSeatIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No booking in progress. Please select a showtime and seats first.");
        }
    }
}

/* package com.cinema.pattern;

import com.cinema.model.*;
import com.cinema.service.*;

public class CheckoutFacade {

    private PaymentCardService paymentCardService;
    private PaymentService paymentService;
    private BookingService bookingService;
    private TicketService ticketService;
    private EmailService emailService;


    public CheckoutFacade(
            PaymentCardService paymentCardService,
            PaymentService paymentService,
            BookingService bookingService,
            TicketService ticketService,
            EmailService emailService ) {
        this.paymentCardService = paymentCardService;
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.ticketService = ticketService;
        this.emailService = emailService;
    }


    public Booking checkout(Customer customer, PaymentCard card) {

        // Retrieve/use saved payment card
        PaymentCard selectedCard = card;

        //  Process payment
        boolean paymentSuccessful =
                paymentService.processPayment(selectedCard);


        if (!paymentSuccessful) {
            throw new RuntimeException("Payment failed");
        }


        // Create booking
        Booking booking = bookingService.createBooking(customer);


        //  Generate tickets
        ticketService.generateTickets(booking);


        //  Send confirmation email
        emailService.sendConfirmationEmail(customer, booking);


        return booking;
    }
}  */
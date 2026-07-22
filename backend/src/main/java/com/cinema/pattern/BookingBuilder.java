package com.cinema.pattern;

import com.cinema.model.*;

import java.util.Date;
import java.util.List;

public class BookingBuilder {

    private String bookingNumber;
    private Date bookingDate;
    private Double totalOrderPrice;
    private Double tax;
    private Double bookingFee;
    private String paymentReference;
    private boolean isCancelled;

    private Customer customer;
    private PaymentCard paymentCard;
    private Promotion promotion;
    private List<Ticket> tickets;
    private Show show;

    public BookingBuilder setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
        return this;
    }

    public BookingBuilder setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
        return this;
    }

    public BookingBuilder setTotalOrderPrice(Double totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
        return this;
    }

    public BookingBuilder setTax(Double tax) {
        this.tax = tax;
        return this;
    }

    public BookingBuilder setBookingFee(Double bookingFee) {
        this.bookingFee = bookingFee;
        return this;
    }

    public BookingBuilder setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
        return this;
    }

    public BookingBuilder setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
        return this;
    }

    public BookingBuilder setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public BookingBuilder setPaymentCard(PaymentCard paymentCard) {
        this.paymentCard = paymentCard;
        return this;
    }

    public BookingBuilder setPromotion(Promotion promotion) {
        this.promotion = promotion;
        return this;
    }

    public BookingBuilder setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        return this;
    }

    public BookingBuilder setShow(Show show) {
        this.show = show;
        return this;
    }

    public Booking build() {

        Booking booking = new Booking(
                bookingNumber,
                bookingDate,
                totalOrderPrice,
                tax,
                bookingFee,
                paymentReference,
                isCancelled
        );

        booking.setCustomer(customer);
        booking.setPaymentCard(paymentCard);
        booking.setPromotion(promotion);
        booking.setTickets(tickets);
        booking.setShow(show);

        return booking;
    }
}

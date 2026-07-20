package com.cinema.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "promotions")
public class Promotion {

    @Id
    private String promoCode;
    private Double discountPercentage;
    private Date expirationDate;
    private boolean isSentOut;

    private List<Booking> bookings;

    public Promotion() {

    }

    public Promotion(String promoCode, Double discountPercentage, Date expirationDate, boolean isSentOut) {
        this.promoCode = promoCode;
        this.discountPercentage = discountPercentage;
        this.expirationDate = expirationDate;
        this.isSentOut = isSentOut;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isSentOut() {
        return isSentOut;
    }

    public void setSentOut(boolean sentOut) {
        isSentOut = sentOut;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
} //end class

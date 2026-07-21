package com.cinema.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "paymentCards")
public class PaymentCard {
    @Id
    private String paymentCardID;

    private String cardNumber,cardholderName,expirationDate;

    private Address address;
    private Customer customer;
    private List<Booking> bookings;

     public PaymentCard() {

     }

     public PaymentCard(String paymentCardID,String cardNumber, String cardholderName, String expirationDate) {
         this.paymentCardID = paymentCardID;
         this.cardNumber = cardNumber;
         this.cardholderName = cardholderName;
         this.expirationDate = expirationDate;
     }


    public String getPaymentCardID() {
        return paymentCardID;
    }

    public void setPaymentCardID(String paymentCardID) {
        this.paymentCardID = paymentCardID;
    }
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
} //end class

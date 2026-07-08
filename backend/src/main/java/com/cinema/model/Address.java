package com.cinema.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.cinema.model.Customer;

@Document(collection = "addresses")
public class Address {

     @Id
     private String address_ID;
     private String street,city,state,zipCode;

     private Customer customer;
     private PaymentCard paymentCard;

     public Address() {

     }

     public Address(String address_ID,String street,String city,String state,String zipCode) {
         this.address_ID = address_ID;
         this.street = street;
         this.city = city;
         this.state = state;
         this.zipCode = zipCode;
     }


    public String getAddress_ID() {
        return address_ID;
    }

    public void setAddress_ID(String address_ID) {
        this.address_ID = address_ID;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public PaymentCard getPaymentCard() {
        return paymentCard;
    }

    public void setPaymentCard(PaymentCard paymentCard) {
        this.paymentCard = paymentCard;
    }
} //end class


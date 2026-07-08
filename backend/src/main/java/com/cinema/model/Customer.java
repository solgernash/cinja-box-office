package com.cinema.model;

import java.util.List;

public class Customer extends User {

    private AccountState accountState;


    private List<PaymentCard> paymentCards;
    private Address address;

    public Customer() {
        super();
    }


    public Customer(String user_ID,
                    String firstName,
                    String lastName,
                    String email,
                    String passwordHash,
                    AccountState accountState) {

        super(user_ID, firstName, lastName, email, passwordHash);
        this.accountState = accountState;
    }


    public AccountState getAccountState() {
        return accountState;
    }


    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public List<PaymentCard> getPaymentCards() {
        return paymentCards;
    }

    public void setPaymentCards(List<PaymentCard> paymentCards) {
        this.paymentCards = paymentCards;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
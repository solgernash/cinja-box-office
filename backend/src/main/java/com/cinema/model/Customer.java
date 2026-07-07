package com.cinema.model;

public class Customer extends User {

    private AccountState accountState;


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
}
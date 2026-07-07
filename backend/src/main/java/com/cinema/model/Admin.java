package com.cinema.model;

public class Admin extends User {


    public Admin() {
        super();
    }


    public Admin(String user_ID, String firstName, String lastName, String email, String passwordHash) {

        super(user_ID, firstName, lastName, email, passwordHash);
    }

}

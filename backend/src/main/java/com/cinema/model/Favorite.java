package com.cinema.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "favorites")
public class Favorite {

    @Id
    private String favorite_ID;

    private Date dateAdded;

    private Movie movie;
    private Customer customer;

    public Favorite() {

    }

    public Favorite(Date dateAdded) {
        this.dateAdded = dateAdded;
    }


    public String getFavorite_ID() {
        return favorite_ID;
    }

    public void setFavorite_ID(String favorite_ID) {
        this.favorite_ID = favorite_ID;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

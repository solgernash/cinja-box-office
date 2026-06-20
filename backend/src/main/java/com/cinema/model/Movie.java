package com.cinema.model;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "movies")














public class Movie {
    @Id
    private String id;
    private String title;
    private String genre;
    private String rating;
    private String description;
    private String posterUrl;
    private String trailerUrl;
    private String status;      // RUNNING or COMING_SOON
    private List<String> showtimes;




    public Movie() {


    } //default constructor


    public Movie(String id, String title, String genre, String rating, String description, String posterUrl,  String trailerUrl, String status, List<String> showtimes) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.description = description;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.status = status;
        this.showtimes = showtimes;


    } //end constructor








    //----------getters and setters----------//




    public String getGenre() {
        return genre;
    }


    public void setGenre(String genre) {
        this.genre = genre;
    }




    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getRating() {
        return rating;
    }


    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getPosterUrl() {
        return posterUrl;
    }


    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }


    public String getTrailerUrl() {
        return trailerUrl;
    }


    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }




    public List<String> getShowtimes() {
        return showtimes;
    }


    public void setShowtimes(List<String> showtimes) {
        this.showtimes = showtimes;
    }






}  //end class

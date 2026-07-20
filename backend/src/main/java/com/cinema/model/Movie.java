package com.cinema.model;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;


@Document(collection = "movies")

public class Movie {
    @Id
    private String id;
    private String title;
    private String runtime;
    private String genre;
    private String rating;
    private String description;

    @JsonProperty("poster")
    private String posterUrl;
    @JsonProperty("trailer")
    private String trailerUrl;


    private String status;      // RUNNING or COMING_SOON
    private List<String> showtime;


    private List<Favorite> favorites;

    public Movie() {

    } //default constructor

    public Movie(String id, String title, String runtime, String genre, String rating, String description, String posterUrl,  String trailerUrl, String status, List<String> showtimes) {
        this.id = id;
        this.title = title;
        this.runtime = runtime;
        this.genre = genre;
        this.rating = rating;
        this.description = description;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.status = status;
        this.showtime = showtimes;


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
        return showtime;
    }


    public void setShowtimes(List<String> showtimes) {
        this.showtime = showtimes;
    }


    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }


    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }
}  //end class

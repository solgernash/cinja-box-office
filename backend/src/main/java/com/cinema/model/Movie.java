package com.cinema.model;
import java.util.Date;
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
    private MovieGenre genre;
    private Double rating;
    private String ageRating;
    private String description;

    @JsonProperty("poster")
    private String posterUrl;
    @JsonProperty("trailer")
    private String trailerUrl;


    private String status;      // RUNNING or COMING_SOON


    private String cast;
    private String director;
    private String producer;
    private String synopsis;
    private Date releaseDate;


    private List<Favorite> favorites;
    private List<Recommendation> recommendations;
    private List<Show> shows;

    public Movie() {

    } //default constructor

    public Movie(String id, String title, String runtime, MovieGenre genre, Double rating, String ageRating, String description, String posterUrl,  String trailerUrl, String status, String cast, String director, String producer, String synopsis, Date releaseDate) {
        this.id = id;
        this.title = title;
        this.runtime = runtime;
        this.genre = genre;
        this.rating = rating;
        this.ageRating = ageRating;
        this.description = description;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.status = status;
        this.cast = cast;
        this.director = director;
        this.producer = producer;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
    } //end constructor








    //----------getters and setters----------//





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


    public Double getRating() {
        return rating;
    }


    public void setRating(Double rating) {
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



    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }


    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }
}  //end class

package com.cinema.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "recommendations")
public class Recommendation {

    @Id
    private String recommendationId;

    private Date recommendationDate;
    private double confidenceScore;

    private Customer customer;
    private List<Movie> movies;


    public Recommendation() {

    }

    public Recommendation(String recommendationId, Date recommendationDate, double confidenceScore) {
        this.recommendationId = recommendationId;
        this.recommendationDate = recommendationDate;
        this.confidenceScore = confidenceScore;
    }

    public String getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(String recommendationId) {
        this.recommendationId = recommendationId;
    }

    public Date getRecommendationDate() {
        return recommendationDate;
    }

    public void setRecommendationDate(Date recommendationDate) {
        this.recommendationDate = recommendationDate;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
} //end class
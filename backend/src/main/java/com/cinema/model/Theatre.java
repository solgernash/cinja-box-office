package com.cinema.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "theatres")
public class Theatre {
    @Id
    private String theatreId;
    private String theatreName;
    private String theatreLocation;

    private List<Showroom> showrooms;


    public Theatre () {

    }

    public Theatre(String theatreId, String theatreName, String theatreLocation) {
        this.theatreId = theatreId;
        this.theatreName = theatreName;
        this.theatreLocation = theatreLocation;

    }

    public String getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(String theatreId) {
        this.theatreId = theatreId;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }

    public String getTheatreLocation() {
        return theatreLocation;
    }

    public void setTheatreLocation(String theatreLocation) {
        this.theatreLocation = theatreLocation;
    }

    public List<Showroom> getShowrooms() {
        return showrooms;
    }

    public void setShowrooms(List<Showroom> showrooms) {
        this.showrooms = showrooms;
    }
} //end class

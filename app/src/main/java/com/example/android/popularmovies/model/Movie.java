package com.example.android.popularmovies.model;

import android.graphics.Bitmap;

/**
 * Created by v.dipaolo on 23/01/2017.
 */

public class Movie {

    private int id;
    private String posterPath;
    private String title;
    private double rating;
    private String release;
    private String plot;

    public Movie(int id, String path){
        this.id = id;
        this.posterPath = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }
}

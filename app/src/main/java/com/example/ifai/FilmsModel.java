package com.example.ifai;

import android.net.Uri;

class FilmsModel {

    String description,film_title,film_uri,poster_uri;

    public FilmsModel() {
    }

    public FilmsModel(String description, String film_title, String film_uri, String poster_uri) {
        this.description = description;
        this.film_title = film_title;
        this.film_uri = film_uri;
        this.poster_uri = poster_uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilm_title() {
        return film_title;
    }

    public void setFilm_title(String film_title) {
        this.film_title = film_title;
    }

    public String getFilm_uri() {
        return film_uri;
    }

    public void setFilm_uri(String film_uri) {
        this.film_uri = film_uri;
    }

    public String getPoster_uri() {
        return poster_uri;
    }

    public void setPoster_uri(String poster_uri) {
        this.poster_uri = poster_uri;
    }
}

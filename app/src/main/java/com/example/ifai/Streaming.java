package com.example.ifai;

public class Streaming {

     private String description,film_title,film_uri,poster_uri,uname,email,uid,views;

    public Streaming() {
    }

    public Streaming(String description, String film_title, String film_uri, String poster_uri, String uname, String email, String uid, String views) {
        this.description = description;
        this.film_title = film_title;
        this.film_uri = film_uri;
        this.poster_uri = poster_uri;
        this.uname = uname;
        this.email = email;
        this.uid = uid;
        this.views = views;
    }

    String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

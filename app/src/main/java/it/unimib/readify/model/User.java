package it.unimib.readify.model;

import java.util.List;

public class User {

    private String biography;

    //private consigliati
    private List<Collection> collections;
    private String email;
    private String genre;
    private Social socialLinks;
    private String username;
    private String visibility;
    private String idToken;

    public User(String biography, List<Collection> collections, String email, String genre, Social socialLinks, String username, String visibility, String idToken) {
        this.biography = biography;
        this.collections = collections;
        this.email = email;
        this.genre = genre;
        this.socialLinks = socialLinks;
        this.username = username;
        this.visibility = visibility;
        this.idToken = idToken;
    }

    public User(String email, String username, String idToken) {
        this.email = email;
        this.username = username;
        this.idToken = idToken;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Social getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(Social socialLinks) {
        this.socialLinks = socialLinks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}

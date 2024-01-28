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
}

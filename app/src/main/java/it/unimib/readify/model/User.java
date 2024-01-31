package it.unimib.readify.model;

import java.util.List;
import java.util.HashMap;

public class User {

    private String biography;
    private List<Collection> collections;
    private HashMap<String, Integer> recommended;
    private String email;
    private String genre;
    //hashmap?
    private HashMap<String, String> socialLinks;
    private String username;
    private String visibility;
    //id token necessario?
    private String idToken;
    private Followers followers;
    private Followers following;

    public User() {}

    public User(String biography, List<Collection> collections, HashMap<String, Integer> recommended, String email, String genre, HashMap<String, String> socialLinks, String username, String visibility, String idToken) {
        this.biography = biography;
        this.collections = collections;
        this.recommended = recommended;
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

    public HashMap<String, Integer> getRecommended() {
        return recommended;
    }

    public void setRecommended(HashMap<String, Integer> recommended) {
        this.recommended = recommended;
    }

    public HashMap<String, String> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(HashMap<String, String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public Followers getFollowers() {
        return followers;
    }

    public void setFollowers(Followers followers) {
        this.followers = followers;
    }

    public Followers getFollowing() {
        return following;
    }

    public void setFollowing(Followers following) {
        this.following = following;
    }

    @Override
    public String toString() {
        return "User{" +
                "biography='" + biography + '\'' +
                ", collections=" + collections +
                ", recommended=" + recommended +
                ", email='" + email + '\'' +
                ", genre='" + genre + '\'' +
                ", socialLinks=" + socialLinks +
                ", username='" + username + '\'' +
                ", visibility='" + visibility + '\'' +
                ", idToken='" + idToken + '\'' +
                '}';
    }
}

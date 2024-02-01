package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.HashMap;

public class User implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.biography);
        dest.writeTypedList(this.collections);
        dest.writeSerializable(this.recommended);
        dest.writeString(this.email);
        dest.writeString(this.genre);
        dest.writeSerializable(this.socialLinks);
        dest.writeString(this.username);
        dest.writeString(this.visibility);
        dest.writeString(this.idToken);
        dest.writeParcelable(this.followers, flags);
        dest.writeParcelable(this.following, flags);
    }

    public void readFromParcel(Parcel source) {
        this.biography = source.readString();
        this.collections = source.createTypedArrayList(Collection.CREATOR);
        this.recommended = (HashMap<String, Integer>) source.readSerializable();
        this.email = source.readString();
        this.genre = source.readString();
        this.socialLinks = (HashMap<String, String>) source.readSerializable();
        this.username = source.readString();
        this.visibility = source.readString();
        this.idToken = source.readString();
        this.followers = source.readParcelable(Followers.class.getClassLoader());
        this.following = source.readParcelable(Followers.class.getClassLoader());
    }

    protected User(Parcel in) {
        this.biography = in.readString();
        this.collections = in.createTypedArrayList(Collection.CREATOR);
        this.recommended = (HashMap<String, Integer>) in.readSerializable();
        this.email = in.readString();
        this.genre = in.readString();
        this.socialLinks = (HashMap<String, String>) in.readSerializable();
        this.username = in.readString();
        this.visibility = in.readString();
        this.idToken = in.readString();
        this.followers = in.readParcelable(Followers.class.getClassLoader());
        this.following = in.readParcelable(Followers.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

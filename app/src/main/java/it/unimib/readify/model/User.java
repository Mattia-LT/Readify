package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class User implements Parcelable {

    private String biography;
    private List<Collection> collections;
    private List<Factor> recommended;
    private String email;
    private String genre;
    private List<Social> socialLinks;
    private String username;
    private String visibility;
    private ExternalGroup followers;
    private ExternalGroup following;
    private String idToken;

    public User() {}

    public User(String email, String idToken) {
        this.email = email;
        this.idToken = idToken;
    }

    public User(String biography, List<Collection> collections, List<Factor> recommended,
                String email, String genre, List<Social> socialLinks, String username,
                String visibility, ExternalGroup followers, ExternalGroup following,
                String idToken) {
        this.biography = biography;
        this.collections = collections;
        this.recommended = recommended;
        this.email = email;
        this.genre = genre;
        this.socialLinks = socialLinks;
        this.username = username;
        this.visibility = visibility;
        this.followers = followers;
        this.following = following;
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

    public List<Factor> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<Factor> recommended) {
        this.recommended = recommended;
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

    public List<Social> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(List<Social> socialLinks) {
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

    public ExternalGroup getFollowers() {
        return followers;
    }

    public void setFollowers(ExternalGroup followers) {
        this.followers = followers;
    }

    public ExternalGroup getFollowing() {
        return following;
    }

    public void setFollowing(ExternalGroup following) {
        this.following = following;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.biography);
        dest.writeTypedList(this.collections);
        dest.writeList(this.recommended);
        dest.writeString(this.email);
        dest.writeString(this.genre);
        dest.writeTypedList(this.socialLinks);
        dest.writeString(this.username);
        dest.writeString(this.visibility);
        dest.writeParcelable(this.followers, flags);
        dest.writeParcelable(this.following, flags);
        dest.writeString(this.idToken);
    }

    public void readFromParcel(Parcel source) {
        this.biography = source.readString();
        this.collections = source.createTypedArrayList(Collection.CREATOR);
        this.recommended = new ArrayList<Factor>();
        source.readList(this.recommended, Factor.class.getClassLoader());
        this.email = source.readString();
        this.genre = source.readString();
        this.socialLinks = source.createTypedArrayList(Social.CREATOR);
        this.username = source.readString();
        this.visibility = source.readString();
        this.followers = source.readParcelable(ExternalGroup.class.getClassLoader());
        this.following = source.readParcelable(ExternalGroup.class.getClassLoader());
        this.idToken = source.readString();
    }

    protected User(Parcel in) {
        this.biography = in.readString();
        this.collections = in.createTypedArrayList(Collection.CREATOR);
        this.recommended = new ArrayList<Factor>();
        in.readList(this.recommended, Factor.class.getClassLoader());
        this.email = in.readString();
        this.genre = in.readString();
        this.socialLinks = in.createTypedArrayList(Social.CREATOR);
        this.username = in.readString();
        this.visibility = in.readString();
        this.followers = in.readParcelable(ExternalGroup.class.getClassLoader());
        this.following = in.readParcelable(ExternalGroup.class.getClassLoader());
        this.idToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
                ", followers=" + followers +
                ", following=" + following +
                ", idToken='" + idToken + '\'' +
                '}';
    }
}

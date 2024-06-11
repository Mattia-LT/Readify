package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Objects;

public class User implements Parcelable {

    private String avatar;
    private String biography;
    private HashMap<String, Integer> recommended = new HashMap<>();
    private String email;
    private String gender;
    private String username;
    private String visibility;
    private FollowGroup followers;
    private FollowGroup following;
    private String idToken;
    private int totalNumberOfBooks;

    public User() {}

    public User(String email, String idToken) {
        this.email = email;
        this.idToken = idToken;
    }

    public User(String email, String idToken, String username, String gender) {
        this.email = email;
        this.idToken = idToken;
        this.username = username;
        this.gender = gender;
    }

    public User(String avatar, String biography, HashMap<String, Integer> recommended, String email, String gender, String username, String visibility, FollowGroup followers, FollowGroup following, String idToken, int totalNumberOfBooks) {
        this.avatar = avatar;
        this.biography = biography;
        this.recommended = recommended;
        this.email = email;
        this.gender = gender;
        this.username = username;
        this.visibility = visibility;
        this.followers = followers;
        this.following = following;
        this.idToken = idToken;
        this.totalNumberOfBooks = totalNumberOfBooks;
    }

    public User (User user) {
        this.avatar = user.getAvatar();
        this.biography = user.getBiography();
        this.recommended = user.getRecommended();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.username = user.getUsername();
        this.visibility = user.getVisibility();
        this.followers = user.getFollowers();
        this.following = user.getFollowing();
        this.idToken = user.getIdToken();
        this.totalNumberOfBooks = user.getTotalNumberOfBooks();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public HashMap<String, Integer> getRecommended() {
        return recommended;
    }

    public void setRecommended(HashMap<String, Integer> recommended) {
        this.recommended = recommended;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public FollowGroup getFollowers() {
        return followers;
    }

    public void setFollowers(FollowGroup followers) {
        this.followers = followers;
    }

    public FollowGroup getFollowing() {
        return following;
    }

    public void setFollowing(FollowGroup following) {
        this.following = following;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public int getTotalNumberOfBooks() {
        return totalNumberOfBooks;
    }

    public void setTotalNumberOfBooks(int totalNumberOfBooks) {
        this.totalNumberOfBooks = totalNumberOfBooks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.biography);
        dest.writeSerializable(this.recommended);
        dest.writeString(this.email);
        dest.writeString(this.gender);
        dest.writeString(this.username);
        dest.writeString(this.visibility);
        dest.writeParcelable(this.followers, flags);
        dest.writeParcelable(this.following, flags);
        dest.writeString(this.idToken);
        dest.writeInt(this.totalNumberOfBooks);
    }

    public void readFromParcel(Parcel source) {
        this.avatar = source.readString();
        this.biography = source.readString();
        this.recommended = (HashMap<String, Integer>) source.readSerializable();
        this.email = source.readString();
        this.gender = source.readString();
        this.username = source.readString();
        this.visibility = source.readString();
        this.followers = source.readParcelable(FollowGroup.class.getClassLoader());
        this.following = source.readParcelable(FollowGroup.class.getClassLoader());
        this.idToken = source.readString();
        this.totalNumberOfBooks = source.readInt();
    }

    protected User(Parcel in) {
        this.avatar = in.readString();
        this.biography = in.readString();
        this.recommended = (HashMap<String, Integer>) in.readSerializable();
        this.email = in.readString();
        this.gender = in.readString();
        this.username = in.readString();
        this.visibility = in.readString();
        this.followers = in.readParcelable(FollowGroup.class.getClassLoader());
        this.following = in.readParcelable(FollowGroup.class.getClassLoader());
        this.idToken = in.readString();
        this.totalNumberOfBooks = in.readInt();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return totalNumberOfBooks == user.totalNumberOfBooks && Objects.equals(avatar, user.avatar) && Objects.equals(biography, user.biography) && Objects.equals(recommended, user.recommended) && Objects.equals(email, user.email) && Objects.equals(gender, user.gender) && Objects.equals(username, user.username) && Objects.equals(visibility, user.visibility) && Objects.equals(followers, user.followers) && Objects.equals(following, user.following) && Objects.equals(idToken, user.idToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avatar, biography, recommended, email, gender, username, visibility, followers, following, idToken, totalNumberOfBooks);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "avatar='" + avatar + '\'' +
                ", biography='" + biography + '\'' +
                ", recommended=" + recommended +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", username='" + username + '\'' +
                ", visibility='" + visibility + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", idToken='" + idToken + '\'' +
                ", totalNumberOfBooks=" + totalNumberOfBooks +
                '}';
    }
}
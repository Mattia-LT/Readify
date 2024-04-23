package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class User implements Parcelable {

    private String avatar;
    private String biography;
    private HashMap<String, Integer> recommended = new HashMap<>();
    private String email;
    private String gender;
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

    public User(String email, String idToken, String username, String gender) {
        this.email = email;
        this.idToken = idToken;
        this.username = username;
        this.gender = gender;
    }

    public User(String avatar, String biography, HashMap<String, Integer> recommended,
                String email, String gender, List<Social> socialLinks, String username,
                String visibility, ExternalGroup followers, ExternalGroup following,
                String idToken) {
        this.avatar = avatar;
        this.biography = biography;
        this.recommended = recommended;
        this.email = email;
        this.gender = gender;
        this.socialLinks = socialLinks;
        this.username = username;
        this.visibility = visibility;
        this.followers = followers;
        this.following = following;
        this.idToken = idToken;
    }

    public User (User user) {
        this.avatar = user.getAvatar();
        this.biography = user.getBiography();
        this.recommended = user.getRecommended();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.socialLinks = user.getSocialLinks();
        this.username = user.getUsername();
        this.visibility = user.getVisibility();
        this.followers = user.getFollowers();
        this.following = user.getFollowing();
        this.idToken = user.getIdToken();
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
        dest.writeString(this.avatar);
        dest.writeString(this.biography);
        dest.writeSerializable(this.recommended);
        dest.writeString(this.email);
        dest.writeString(this.gender);
        dest.writeTypedList(this.socialLinks);
        dest.writeString(this.username);
        dest.writeString(this.visibility);
        dest.writeParcelable(this.followers, flags);
        dest.writeParcelable(this.following, flags);
        dest.writeString(this.idToken);
    }

    public void readFromParcel(Parcel source) {
        this.avatar = source.readString();
        this.biography = source.readString();
        this.recommended = (HashMap<String, Integer>) source.readSerializable();
        this.email = source.readString();
        this.gender = source.readString();
        this.socialLinks = source.createTypedArrayList(Social.CREATOR);
        this.username = source.readString();
        this.visibility = source.readString();
        this.followers = source.readParcelable(ExternalGroup.class.getClassLoader());
        this.following = source.readParcelable(ExternalGroup.class.getClassLoader());
        this.idToken = source.readString();
    }

    protected User(Parcel in) {
        this.avatar = in.readString();
        this.biography = in.readString();
        this.recommended = (HashMap<String, Integer>) in.readSerializable();
        this.email = in.readString();
        this.gender = in.readString();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(avatar, user.avatar) && Objects.equals(biography, user.biography) && Objects.equals(recommended, user.recommended) && Objects.equals(email, user.email) && Objects.equals(gender, user.gender) && Objects.equals(socialLinks, user.socialLinks) && Objects.equals(username, user.username) && Objects.equals(visibility, user.visibility) && Objects.equals(followers, user.followers) && Objects.equals(following, user.following) && Objects.equals(idToken, user.idToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avatar, biography, recommended, email, gender, socialLinks, username, visibility, followers, following, idToken);
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
                ", socialLinks=" + socialLinks +
                ", username='" + username + '\'' +
                ", visibility='" + visibility + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", idToken='" + idToken + '\'' +
                '}';
    }

    public String printReference() {
        return super.toString();
    }
}

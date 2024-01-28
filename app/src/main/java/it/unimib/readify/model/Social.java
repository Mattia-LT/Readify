package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Social implements Parcelable {
    private String facebook;
    private String instagram;
    private String twitter;
    private String tiktok;

    public Social(String facebook, String instagram, String twitter, String tiktok) {
        this.facebook = facebook;
        this.instagram = instagram;
        this.twitter = twitter;
        this.tiktok = tiktok;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getTiktok() {
        return tiktok;
    }

    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Social social = (Social) o;
        return Objects.equals(facebook, social.facebook) && Objects.equals(instagram, social.instagram) && Objects.equals(twitter, social.twitter) && Objects.equals(tiktok, social.tiktok);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facebook, instagram, twitter, tiktok);
    }

    @Override
    public String toString() {
        return "Social{" +
                "facebook='" + facebook + '\'' +
                ", instagram='" + instagram + '\'' +
                ", twitter='" + twitter + '\'' +
                ", tiktok='" + tiktok + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.facebook);
        dest.writeString(this.instagram);
        dest.writeString(this.twitter);
        dest.writeString(this.tiktok);
    }

    public void readFromParcel(Parcel source) {
        this.facebook = source.readString();
        this.instagram = source.readString();
        this.twitter = source.readString();
        this.tiktok = source.readString();
    }

    protected Social(Parcel in) {
        this.facebook = in.readString();
        this.instagram = in.readString();
        this.twitter = in.readString();
        this.tiktok = in.readString();
    }

    public static final Parcelable.Creator<Social> CREATOR = new Parcelable.Creator<Social>() {
        @Override
        public Social createFromParcel(Parcel source) {
            return new Social(source);
        }

        @Override
        public Social[] newArray(int size) {
            return new Social[size];
        }
    };


}

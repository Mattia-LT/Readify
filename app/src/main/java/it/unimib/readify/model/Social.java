package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Social implements Parcelable {
    private String socialPlatform;
    private String link;
    //todo add String username, extracting it from @link

    public Social() {}

    public Social(String socialPlatform, String link) {
        this.socialPlatform = socialPlatform;
        this.link = link;
    }

    public String getSocialPlatform() {
        return socialPlatform;
    }

    public void setSocialPlatform(String socialPlatform) {
        this.socialPlatform = socialPlatform;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.socialPlatform);
        dest.writeString(this.link);
    }

    public void readFromParcel(Parcel source) {
        this.socialPlatform = source.readString();
        this.link = source.readString();
    }

    protected Social(Parcel in) {
        this.socialPlatform = in.readString();
        this.link = in.readString();
    }

    public static final Creator<Social> CREATOR = new Creator<Social>() {
        @Override
        public Social createFromParcel(Parcel source) {
            return new Social(source);
        }

        @Override
        public Social[] newArray(int size) {
            return new Social[size];
        }
    };

    @Override
    public String toString() {
        return "Social{" +
                "socialPlatform='" + socialPlatform + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}

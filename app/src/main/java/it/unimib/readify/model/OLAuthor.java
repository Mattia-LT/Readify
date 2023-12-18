package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OLAuthor implements Parcelable {

    private String fuller_name;
    private String name;
    private String birth_date;
    private String death_date;
    private int[] photos;

    public OLAuthor(String fuller_name, String name, String birth_date, String death_date, int[] photos) {
        this.fuller_name = fuller_name;
        this.name = name;
        this.birth_date = birth_date;
        this.death_date = death_date;
        this.photos = photos;
    }

    public String getFuller_name() {
        return fuller_name;
    }

    public void setFuller_name(String fuller_name) {
        this.fuller_name = fuller_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getDeath_date() {
        return death_date;
    }

    public void setDeath_date(String death_date) {
        this.death_date = death_date;
    }

    public int[] getPhotos() {
        return photos;
    }

    public void setPhotos(int[] photos) {
        this.photos = photos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fuller_name);
        dest.writeString(this.name);
        dest.writeString(this.birth_date);
        dest.writeString(this.death_date);
        dest.writeIntArray(this.photos);
    }

    public void readFromParcel(Parcel source) {
        this.fuller_name = source.readString();
        this.name = source.readString();
        this.birth_date = source.readString();
        this.death_date = source.readString();
        this.photos = source.createIntArray();
    }

    protected OLAuthor(Parcel in) {
        this.fuller_name = in.readString();
        this.name = in.readString();
        this.birth_date = in.readString();
        this.death_date = in.readString();
        this.photos = in.createIntArray();
    }

    public static final Parcelable.Creator<OLAuthor> CREATOR = new Parcelable.Creator<OLAuthor>() {
        @Override
        public OLAuthor createFromParcel(Parcel source) {
            return new OLAuthor(source);
        }

        @Override
        public OLAuthor[] newArray(int size) {
            return new OLAuthor[size];
        }
    };
}

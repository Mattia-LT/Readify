package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class OLAuthorKeys implements Parcelable {

    private OLDocs author;
    private String key;

    public OLAuthorKeys() {};

    public OLAuthorKeys(OLDocs author, String key) {
        this.author = author;
        this.key = key;
    }

    public OLDocs getAuthor() {
        return author;
    }

    public void setAuthor(OLDocs author) {
        this.author = author;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return "OLAuthorKeys{" +
                "author=" + author +
                ", key='" + key + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OLAuthorKeys that = (OLAuthorKeys) o;
        return Objects.equals(author, that.author) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, key);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.author, flags);
        dest.writeString(this.key);
    }

    public void readFromParcel(Parcel source) {
        this.author = source.readParcelable(OLDocs.class.getClassLoader());
        this.key = source.readString();
    }

    protected OLAuthorKeys(Parcel in) {
        this.author = in.readParcelable(OLDocs.class.getClassLoader());
        this.key = in.readString();
    }

    public static final Creator<OLAuthorKeys> CREATOR = new Creator<OLAuthorKeys>() {
        @Override
        public OLAuthorKeys createFromParcel(Parcel source) {
            return new OLAuthorKeys(source);
        }

        @Override
        public OLAuthorKeys[] newArray(int size) {
            return new OLAuthorKeys[size];
        }
    };
}

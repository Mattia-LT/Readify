package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

public class OLAuthorKeys implements Parcelable {

    private OLDocs author;

    public OLAuthorKeys(OLDocs author) {
        this.author = author;
    }

    public OLDocs getAuthor() {
        return author;
    }

    public void setAuthor(OLDocs author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OLAuthorKeys that = (OLAuthorKeys) o;
        return Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author);
    }

    @Override
    public String toString() {
        return "OLAuthorKeys{" +
                "author=" + author +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.author, flags);
    }

    public void readFromParcel(Parcel source) {
        this.author = source.readParcelable(OLDocs.class.getClassLoader());
    }

    protected OLAuthorKeys(Parcel in) {
        this.author = in.readParcelable(OLDocs.class.getClassLoader());
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

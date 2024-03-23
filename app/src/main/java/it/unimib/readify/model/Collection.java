package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// TODO: 09/12/2023 managing thumbnail
public class Collection implements Parcelable {

    private String name;
    private boolean visible;
    private List <String> books;
    private List<OLWorkApiResponse> works = new ArrayList<>();

    public Collection() {}

    public Collection(String name, boolean visible, List<String> books) {
        this.name = name;
        this.visible = visible;
        this.books = books;
        this.works = new ArrayList<>();
    }

    public Collection(String name, boolean visible, List<String> books, List<OLWorkApiResponse> works) {
        this.name = name;
        this.visible = visible;
        this.books = books;
        this.works = works;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<String> getBooks() {
        return books;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }

    public List<OLWorkApiResponse> getWorks() {
        return works;
    }

    public void setWorks(List<OLWorkApiResponse> works) {
        this.works = works;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.books);
        dest.writeTypedList(this.works);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.visible = source.readByte() != 0;
        this.books = source.createStringArrayList();
        this.works = source.createTypedArrayList(OLWorkApiResponse.CREATOR);
    }

    protected Collection(Parcel in) {
        this.name = in.readString();
        this.visible = in.readByte() != 0;
        this.books = in.createStringArrayList();
        this.works = in.createTypedArrayList(OLWorkApiResponse.CREATOR);
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel source) {
            return new Collection(source);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Collection{" +
                "name='" + name + '\'' +
                ", visible=" + visible +
                ", books=" + books +
                ", works=" + works +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return visible == that.visible && Objects.equals(name, that.name) && Objects.equals(books, that.books) && Objects.equals(works, that.works);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, visible, books, works);
    }
}

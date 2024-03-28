package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// TODO: 09/12/2023 managing thumbnail
public class Collection implements Parcelable {

    private String collectionId;
    private String name;
    private boolean visible;
    private List <String> books;
    private int numberOfBooks;
    private List<OLWorkApiResponse> works = new ArrayList<>();

    public Collection() {}

    public Collection(String collectionId, String name, boolean visible, List<String> books) {
        this.collectionId = collectionId;
        this.name = name;
        this.visible = visible;
        this.books = books;
        this.works = new ArrayList<>();
        this.numberOfBooks = 0;
    }

    public Collection(String collectionId, String name, boolean visible, List<String> books, List<OLWorkApiResponse> works) {
        this.collectionId = collectionId;
        this.name = name;
        this.visible = visible;
        this.books = books;
        this.works = works;
        this.numberOfBooks = works.size();
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
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

    public int getNumberOfBooks() {
        return numberOfBooks;
    }

    public void setNumberOfBooks(int numberOfBooks) {
        this.numberOfBooks = numberOfBooks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.collectionId);
        dest.writeString(this.name);
        dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.books);
        dest.writeInt(this.numberOfBooks);
        dest.writeTypedList(this.works);
    }

    public void readFromParcel(Parcel source) {
        this.collectionId = source.readString();
        this.name = source.readString();
        this.visible = source.readByte() != 0;
        this.books = source.createStringArrayList();
        this.numberOfBooks = source.readInt();
        this.works = source.createTypedArrayList(OLWorkApiResponse.CREATOR);
    }

    protected Collection(Parcel in) {
        this.collectionId = in.readString();
        this.name = in.readString();
        this.visible = in.readByte() != 0;
        this.books = in.createStringArrayList();
        this.numberOfBooks = in.readInt();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return visible == that.visible && numberOfBooks == that.numberOfBooks && Objects.equals(collectionId, that.collectionId) && Objects.equals(name, that.name) && Objects.equals(books, that.books) && Objects.equals(works, that.works);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionId, name, visible, books, numberOfBooks, works);
    }

    @Override
    public String toString() {
        return "Collection{" +
                "collectionId='" + collectionId + '\'' +
                ", name='" + name + '\'' +
                ", visible=" + visible +
                ", books=" + books +
                ", numberOfBooks=" + numberOfBooks +
                ", works=" + works +
                '}';
    }
}

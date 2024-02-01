package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


// TODO: 09/12/2023 gestione della thumbnail
public class Collection implements Parcelable {

    private String name;
    private boolean visible;
    private List <String> books;

    public Collection() {}

    public Collection(String name, boolean visible, List<String> books) {
        this.name = name;
        this.visible = visible;
        this.books = books;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.books);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.visible = source.readByte() != 0;
        this.books = source.createStringArrayList();
    }

    protected Collection(Parcel in) {
        this.name = in.readString();
        this.visible = in.readByte() != 0;
        this.books = in.createStringArrayList();
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
    public String toString() {
        return "Collection{" +
                "name='" + name + '\'' +
                ", visible=" + visible +
                ", books=" + books +
                '}';
    }
}

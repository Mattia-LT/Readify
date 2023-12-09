package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Book implements Parcelable {

    private String title;
    private String id;
    private ArrayList<String> topics;
    private String author;

    private String imagePath;

    public Book(String title, String id, ArrayList<String> topics, String author, String imagePath) {
        this.title = title;
        this.id = id;
        this.topics = topics;
        this.author = author;
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<String> topics) {
        this.topics = topics;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.id);
        dest.writeStringList(this.topics);
        dest.writeString(this.author);
        dest.writeString(this.imagePath);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.id = source.readString();
        this.topics = source.createStringArrayList();
        this.author = source.readString();
        this.imagePath = source.readString();
    }

    protected Book(Parcel in) {
        this.title = in.readString();
        this.id = in.readString();
        this.topics = in.createStringArrayList();
        this.author = in.readString();
        this.imagePath = in.readString();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}

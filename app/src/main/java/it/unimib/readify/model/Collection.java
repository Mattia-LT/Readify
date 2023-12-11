package it.unimib.readify.model;

import android.media.Image;

import java.util.ArrayList;
import java.util.Date;

// TODO: 09/12/2023 implement parcelable
// TODO: 09/12/2023 gestione della thumbnail
// TODO: 11/12/2023 gestione attributo data
public class Collection {

    private String name;
    private boolean visibility;
    //private Image thumbnail;
    //private final Date creationDate;
    private ArrayList <Book> books;

    public Collection(String name, boolean visibility, ArrayList<Book> books) {
        this.name = name;
        this.visibility = visibility;
        //this.thumbnail = thumbnail;
        //this.creationDate = creationDate;
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visibility;
    }

    /*
    public Image getThumbnail() {
        return thumbnail;
    }
    */

    public ArrayList<Book> getBooks() {
        return books;
    }
}

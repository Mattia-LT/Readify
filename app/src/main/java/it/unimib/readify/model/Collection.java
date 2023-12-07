package it.unimib.readify.model;

import android.media.Image;

import java.util.ArrayList;
import java.util.Date;

public class Collection {

    private String name;
    private boolean visibility;
    private Image thumbnail;
    private final Date creationDate;
    private ArrayList <Book> books;

    public Collection(String name, boolean visibility, Image thumbnail,
                      Date creationDate, ArrayList<Book> books) {
        this.name = name;
        this.visibility = visibility;
        this.thumbnail = thumbnail;
        this.creationDate = creationDate;
        this.books = books;
    }
}

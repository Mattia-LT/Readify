package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BookResponse implements Parcelable {

    private List<Book> bookList;

    public BookResponse() {}

    public BookResponse(List<Book> bookList){
        this.bookList = bookList;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    //todo forse serve toString

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.bookList);
    }

    public void readFromParcel(Parcel source) {
        this.bookList = source.createTypedArrayList(Book.CREATOR);
    }

    protected BookResponse(Parcel in) {
        this.bookList = in.createTypedArrayList(Book.CREATOR);
    }

    public static final Creator<BookResponse> CREATOR = new Creator<BookResponse>() {
        @Override
        public BookResponse createFromParcel(Parcel source) {
            return new BookResponse(source);
        }

        @Override
        public BookResponse[] newArray(int size) {
            return new BookResponse[size];
        }
    };
}

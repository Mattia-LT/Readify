package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OLResponse implements Parcelable {

    private List<Book> bookList;

    public OLResponse() {}

    public OLResponse(List<Book> bookList){
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

    protected OLResponse(Parcel in) {
        this.bookList = in.createTypedArrayList(Book.CREATOR);
    }

    public static final Creator<OLResponse> CREATOR = new Creator<OLResponse>() {
        @Override
        public OLResponse createFromParcel(Parcel source) {
            return new OLResponse(source);
        }

        @Override
        public OLResponse[] newArray(int size) {
            return new OLResponse[size];
        }
    };
}

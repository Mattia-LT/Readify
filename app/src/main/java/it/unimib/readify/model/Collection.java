package it.unimib.readify.model;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;


// TODO: 09/12/2023 implement parcelable
// TODO: 09/12/2023 gestione della thumbnail
// TODO: 11/12/2023 gestione attributo data
public class Collection implements Parcelable {

    private final String name;
    private final boolean visibility;
    //private Image thumbnail;
    private final ArrayList <OLWorkApiResponse> books;

    public Collection(String name, boolean visibility, ArrayList<OLWorkApiResponse> books) {
        this.name = name;
        this.visibility = visibility;
        this.books = books;
    }

    protected Collection(Parcel in) {
        name = in.readString();
        visibility = in.readByte() != 0;
        books = in.createTypedArrayList(OLWorkApiResponse.CREATOR);
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visibility;
    }

    public ArrayList<OLWorkApiResponse> getBooks() {
        return books;
    }

    public OLWorkApiResponse getBook(int position) {
        return books.get(position);
    }

    public int getNumberOfBooks() {
        if(books != null)
            return books.size();
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (visibility ? 1 : 0));
        dest.writeTypedList(books);
    }
}

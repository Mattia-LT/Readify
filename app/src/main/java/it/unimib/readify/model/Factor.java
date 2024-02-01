package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

//todo rivedere classe usata per i recommended (fatta un po a caso)
public class Factor implements Parcelable {

    private String genreName;
    private int value;

    public Factor() {}

    public Factor(String genreName, int value) {
        this.genreName = genreName;
        this.value = value;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.genreName);
        dest.writeInt(this.value);
    }

    public void readFromParcel(Parcel source) {
        this.genreName = source.readString();
        this.value = source.readInt();
    }

    protected Factor(Parcel in) {
        this.genreName = in.readString();
        this.value = in.readInt();
    }

    public static final Creator<Factor> CREATOR = new Creator<Factor>() {
        @Override
        public Factor createFromParcel(Parcel source) {
            return new Factor(source);
        }

        @Override
        public Factor[] newArray(int size) {
            return new Factor[size];
        }
    };

    @Override
    public String toString() {
        return "Factor{" +
                "genreName='" + genreName + '\'' +
                ", value=" + value +
                '}';
    }
}

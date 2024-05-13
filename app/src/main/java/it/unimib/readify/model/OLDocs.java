package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OLDocs implements Parcelable {

    private String key;

    public OLDocs() {}

    public OLDocs(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
    }

    public void readFromParcel(Parcel source) {
        this.key = source.readString();
    }

    protected OLDocs(Parcel in) {
        this.key = in.readString();
    }

    public static final Parcelable.Creator<OLDocs> CREATOR = new Parcelable.Creator<OLDocs>() {
        @Override
        public OLDocs createFromParcel(Parcel source) {
            return new OLDocs(source);
        }

        @Override
        public OLDocs[] newArray(int size) {
            return new OLDocs[size];
        }
    };
}

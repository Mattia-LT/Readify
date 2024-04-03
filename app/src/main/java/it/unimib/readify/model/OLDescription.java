package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class OLDescription implements Parcelable {

    private String type;
    private String value;

    public OLDescription() {};

    public OLDescription(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OLDescription that = (OLDescription) o;
        return Objects.equals(type, that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "OLDescription{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.value);
    }

    public void readFromParcel(Parcel source) {
        this.type = source.readString();
        this.value = source.readString();
    }

    protected OLDescription(Parcel in) {
        this.type = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<OLDescription> CREATOR = new Parcelable.Creator<OLDescription>() {
        @Override
        public OLDescription createFromParcel(Parcel source) {
            return new OLDescription(source);
        }

        @Override
        public OLDescription[] newArray(int size) {
            return new OLDescription[size];
        }
    };
}

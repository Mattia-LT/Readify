package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class OLTrendingApiResponse implements Parcelable {
    private List<OLDocs> works;

    public OLTrendingApiResponse() {}

    public List<OLDocs> getWorks() {
        return works;
    }

    public void setWorks(List<OLDocs> works) {
        this.works = works;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.works);
    }

    public void readFromParcel(Parcel source) {
        this.works = source.createTypedArrayList(OLDocs.CREATOR);
    }

    protected OLTrendingApiResponse(Parcel in) {
        this.works = in.createTypedArrayList(OLDocs.CREATOR);
    }

    public static final Parcelable.Creator<OLTrendingApiResponse> CREATOR = new Parcelable.Creator<OLTrendingApiResponse>() {
        @Override
        public OLTrendingApiResponse createFromParcel(Parcel source) {
            return new OLTrendingApiResponse(source);
        }

        @Override
        public OLTrendingApiResponse[] newArray(int size) {
            return new OLTrendingApiResponse[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OLTrendingApiResponse that = (OLTrendingApiResponse) o;
        return Objects.equals(works, that.works);
    }

    @Override
    public int hashCode() {
        return Objects.hash(works);
    }

    @NonNull
    @Override
    public String toString() {
        return "OLTrendingApiResponse{" +
                "works=" + works +
                '}';
    }
}

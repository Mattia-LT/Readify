package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class OLRatingResponse implements Parcelable {
    private OLSummaryRating summary;

    public OLRatingResponse(OLSummaryRating summary) {
        this.summary = summary;
    }

    public OLSummaryRating getSummary() {
        return summary;
    }

    public void setSummary(OLSummaryRating summary) {
        this.summary = summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OLRatingResponse that = (OLRatingResponse) o;
        return Objects.equals(summary, that.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary);
    }

    @NonNull
    @Override
    public String toString() {
        return "OLRatingResponse{" +
                "summary=" + summary +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.summary, flags);
    }

    public void readFromParcel(Parcel source) {
        this.summary = source.readParcelable(OLSummaryRating.class.getClassLoader());
    }

    protected OLRatingResponse(Parcel in) {
        this.summary = in.readParcelable(OLSummaryRating.class.getClassLoader());
    }

    public static final Parcelable.Creator<OLRatingResponse> CREATOR = new Parcelable.Creator<OLRatingResponse>() {
        @Override
        public OLRatingResponse createFromParcel(Parcel source) {
            return new OLRatingResponse(source);
        }

        @Override
        public OLRatingResponse[] newArray(int size) {
            return new OLRatingResponse[size];
        }
    };
}

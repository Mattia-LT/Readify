package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class OLSummaryRating implements Parcelable {

    private double average;
    private int count;
    private double sortable;

    public OLSummaryRating(double average, int count, double sortable) {
        this.average = average;
        this.count = count;
        this.sortable = sortable;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getSortable() {
        return sortable;
    }

    public void setSortable(double sortable) {
        this.sortable = sortable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OLSummaryRating that = (OLSummaryRating) o;
        return Double.compare(that.average, average) == 0 && count == that.count && Double.compare(that.sortable, sortable) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(average, count, sortable);
    }

    @NonNull
    @Override
    public String toString() {
        return "OLSummaryRating{" +
                "average=" + average +
                ", count=" + count +
                ", sortable=" + sortable +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.average);
        dest.writeInt(this.count);
        dest.writeDouble(this.sortable);
    }

    public void readFromParcel(Parcel source) {
        this.average = source.readDouble();
        this.count = source.readInt();
        this.sortable = source.readDouble();
    }

    protected OLSummaryRating(Parcel in) {
        this.average = in.readDouble();
        this.count = in.readInt();
        this.sortable = in.readDouble();
    }

    public static final Parcelable.Creator<OLSummaryRating> CREATOR = new Parcelable.Creator<OLSummaryRating>() {
        @Override
        public OLSummaryRating createFromParcel(Parcel source) {
            return new OLSummaryRating(source);
        }

        @Override
        public OLSummaryRating[] newArray(int size) {
            return new OLSummaryRating[size];
        }
    };
}
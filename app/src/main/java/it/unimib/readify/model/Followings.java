package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Followings implements Parcelable {
    int counter;
    List<String> followings;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<String> getFollowings() {
        return followings;
    }

    public void setFollowings(List<String> followings) {
        this.followings = followings;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.counter);
        dest.writeStringList(this.followings);
    }

    public void readFromParcel(Parcel source) {
        this.counter = source.readInt();
        this.followings = source.createStringArrayList();
    }

    public Followings() {
    }

    protected Followings(Parcel in) {
        this.counter = in.readInt();
        this.followings = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Followings> CREATOR = new Parcelable.Creator<Followings>() {
        @Override
        public Followings createFromParcel(Parcel source) {
            return new Followings(source);
        }

        @Override
        public Followings[] newArray(int size) {
            return new Followings[size];
        }
    };
}

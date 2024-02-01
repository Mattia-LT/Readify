package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Followers implements Parcelable {
    int counter;
    List<Integer> followers;

    public Followers() {}

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<Integer> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Integer> followers) {
        this.followers = followers;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.counter);
        dest.writeList(this.followers);
    }

    public void readFromParcel(Parcel source) {
        this.counter = source.readInt();
        this.followers = new ArrayList<Integer>();
        source.readList(this.followers, Integer.class.getClassLoader());
    }

    protected Followers(Parcel in) {
        this.counter = in.readInt();
        this.followers = new ArrayList<Integer>();
        in.readList(this.followers, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Followers> CREATOR = new Parcelable.Creator<Followers>() {
        @Override
        public Followers createFromParcel(Parcel source) {
            return new Followers(source);
        }

        @Override
        public Followers[] newArray(int size) {
            return new Followers[size];
        }
    };
}

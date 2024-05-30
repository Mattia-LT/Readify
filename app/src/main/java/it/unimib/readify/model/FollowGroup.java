package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class FollowGroup implements Parcelable {
    private int counter;
    private List<FollowUser> users;

    public FollowGroup() {}

    public FollowGroup(int counter, List<FollowUser> users) {
        this.counter = counter;
        this.users = users;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<FollowUser> getUsers() {
        return users;
    }

    public void setUsers(List<FollowUser> users) {
        this.users = users;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.counter);
        dest.writeTypedList(this.users);
    }

    public void readFromParcel(Parcel source) {
        this.counter = source.readInt();
        this.users = source.createTypedArrayList(FollowUser.CREATOR);
    }

    protected FollowGroup(Parcel in) {
        this.counter = in.readInt();
        this.users = in.createTypedArrayList(FollowUser.CREATOR);
    }

    public static final Creator<FollowGroup> CREATOR = new Creator<FollowGroup>() {
        @Override
        public FollowGroup createFromParcel(Parcel source) {
            return new FollowGroup(source);
        }

        @Override
        public FollowGroup[] newArray(int size) {
            return new FollowGroup[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "FollowGroup{" +
                "counter=" + counter +
                ", users=" + users +
                '}';
    }
}

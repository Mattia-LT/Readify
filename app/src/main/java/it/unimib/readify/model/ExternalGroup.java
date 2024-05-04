package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class ExternalGroup implements Parcelable {
    private int counter;
    private List<ExternalUser> users;

    public ExternalGroup() {}

    public ExternalGroup(int counter, List<ExternalUser> users) {
        this.counter = counter;
        this.users = users;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<ExternalUser> getUsers() {
        return users;
    }

    public void setUsers(List<ExternalUser> users) {
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
        this.users = source.createTypedArrayList(ExternalUser.CREATOR);
    }

    protected ExternalGroup(Parcel in) {
        this.counter = in.readInt();
        this.users = in.createTypedArrayList(ExternalUser.CREATOR);
    }

    public static final Creator<ExternalGroup> CREATOR = new Creator<ExternalGroup>() {
        @Override
        public ExternalGroup createFromParcel(Parcel source) {
            return new ExternalGroup(source);
        }

        @Override
        public ExternalGroup[] newArray(int size) {
            return new ExternalGroup[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "ExternalGroup{" +
                "counter=" + counter +
                ", users=" + users +
                '}';
    }
}

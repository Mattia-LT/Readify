package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Follower implements Parcelable {
    boolean read;
    Date timestamp;
    String username;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.read ? (byte) 1 : (byte) 0);
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
        dest.writeString(this.username);
    }

    public void readFromParcel(Parcel source) {
        this.read = source.readByte() != 0;
        long tmpTimestamp = source.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.username = source.readString();
    }

    public Follower() {
    }

    protected Follower(Parcel in) {
        this.read = in.readByte() != 0;
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.username = in.readString();
    }

    public static final Parcelable.Creator<Follower> CREATOR = new Parcelable.Creator<Follower>() {
        @Override
        public Follower createFromParcel(Parcel source) {
            return new Follower(source);
        }

        @Override
        public Follower[] newArray(int size) {
            return new Follower[size];
        }
    };
}

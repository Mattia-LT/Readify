package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Notification implements Parcelable, Comparable<Notification> {

    private String idToken;
    private boolean read;
    private long timestamp;
    private User user;

    public Notification() {}

    public Notification(String idToken, boolean read, long timestamp) {
        this.idToken = idToken;
        this.read = read;
        this.timestamp = timestamp;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int compareTo(Notification o) {
        return Long.compare(this.getTimestamp(), o.getTimestamp());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idToken);
        dest.writeByte(this.read ? (byte) 1 : (byte) 0);
        dest.writeLong(this.timestamp);
        dest.writeParcelable(this.user, flags);
    }

    public void readFromParcel(Parcel source) {
        this.idToken = source.readString();
        this.read = source.readByte() != 0;
        this.timestamp = source.readLong();
        this.user = source.readParcelable(User.class.getClassLoader());
    }

    protected Notification(Parcel in) {
        this.idToken = in.readString();
        this.read = in.readByte() != 0;
        this.timestamp = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel source) {
            return new Notification(source);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Notification{" +
                "idToken='" + idToken + '\'' +
                ", read=" + read +
                ", timestamp=" + timestamp +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return read == that.read && timestamp == that.timestamp && Objects.equals(idToken, that.idToken) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idToken, read, timestamp, user);
    }
}
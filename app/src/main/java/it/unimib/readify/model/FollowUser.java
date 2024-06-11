package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;


public class FollowUser implements Parcelable {

    private boolean read;
    private long timestamp;
    private String idToken;
    private User user;

    public FollowUser() {}

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

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.read ? (byte) 1 : (byte) 0);
        dest.writeLong(this.timestamp);
        dest.writeString(this.idToken);
        dest.writeParcelable(this.user, flags);
    }

    public void readFromParcel(Parcel source) {
        this.read = source.readByte() != 0;
        this.timestamp = source.readLong();
        this.idToken = source.readString();
        this.user = source.readParcelable(User.class.getClassLoader());
    }

    protected FollowUser(Parcel in) {
        this.read = in.readByte() != 0;
        this.timestamp = in.readLong();
        this.idToken = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<FollowUser> CREATOR = new Creator<FollowUser>() {
        @Override
        public FollowUser createFromParcel(Parcel source) {
            return new FollowUser(source);
        }

        @Override
        public FollowUser[] newArray(int size) {
            return new FollowUser[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowUser that = (FollowUser) o;
        return read == that.read && timestamp == that.timestamp && Objects.equals(idToken, that.idToken) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(read, timestamp, idToken, user);
    }

    @NonNull
    @Override
    public String toString() {
        return "FollowUser{" +
                "read=" + read +
                ", timestamp='" + timestamp + '\'' +
                ", idToken='" + idToken + '\'' +
                ", user=" + user +
                '}';
    }
}
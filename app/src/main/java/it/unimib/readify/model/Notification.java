package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Notification implements Parcelable, Comparable<Notification> {

    private String idToken;
    private String username;
    private String avatar;
    private boolean followedByUser;
    private boolean isRead;
    private long timestamp;

    public Notification() {}

    public Notification(String idToken, boolean isRead, long timestamp) {
        this.idToken = idToken;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    public Notification(String idToken, String username, String avatar, boolean followedByUser, boolean isRead, long timestamp) {
        this.idToken = idToken;
        this.username = username;
        this.avatar = avatar;
        this.followedByUser = followedByUser;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    public Notification(Notification notification) {
        this.idToken = notification.getIdToken();
        this.username = notification.getUsername();
        this.avatar = notification.getAvatar();
        this.followedByUser = notification.isFollowedByUser();
        this.isRead = notification.isRead();
        this.timestamp = notification.getTimestamp();
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isFollowedByUser() {
        return followedByUser;
    }

    public void setFollowedByUser(boolean followedByUser) {
        this.followedByUser = followedByUser;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "Notification{" +
                "idToken='" + idToken + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", isFollowedByUser=" + followedByUser +
                ", isRead=" + isRead +
                ", timestamp=" + timestamp +
                '}';
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
        dest.writeString(this.username);
        dest.writeString(this.avatar);
        dest.writeByte(this.followedByUser ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
        dest.writeLong(this.timestamp);
    }

    public void readFromParcel(Parcel source) {
        this.idToken = source.readString();
        this.username = source.readString();
        this.avatar = source.readString();
        this.followedByUser = source.readByte() != 0;
        this.isRead = source.readByte() != 0;
        this.timestamp = source.readLong();
    }

    protected Notification(Parcel in) {
        this.idToken = in.readString();
        this.username = in.readString();
        this.avatar = in.readString();
        this.followedByUser = in.readByte() != 0;
        this.isRead = in.readByte() != 0;
        this.timestamp = in.readLong();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return followedByUser == that.followedByUser && isRead == that.isRead && timestamp == that.timestamp && Objects.equals(idToken, that.idToken) && Objects.equals(username, that.username) && Objects.equals(avatar, that.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idToken, username, avatar, followedByUser, isRead, timestamp);
    }
}
package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;


public class ExternalUser implements Parcelable {
    private boolean read;
    private String timestamp;
    private String idToken;
    private User user;

    public ExternalUser() {}

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalUser user1 = (ExternalUser) o;
        return read == user1.read && Objects.equals(timestamp, user1.timestamp) && Objects.equals(idToken, user1.idToken) && Objects.equals(user, user1.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(read, timestamp, idToken, user);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.read ? (byte) 1 : (byte) 0);
        dest.writeString(this.timestamp);
        dest.writeString(this.idToken);
        dest.writeParcelable(this.user, flags);
    }

    public void readFromParcel(Parcel source) {
        this.read = source.readByte() != 0;
        this.timestamp = source.readString();
        this.idToken = source.readString();
        this.user = source.readParcelable(User.class.getClassLoader());
    }

    protected ExternalUser(Parcel in) {
        this.read = in.readByte() != 0;
        this.timestamp = in.readString();
        this.idToken = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<ExternalUser> CREATOR = new Creator<ExternalUser>() {
        @Override
        public ExternalUser createFromParcel(Parcel source) {
            return new ExternalUser(source);
        }

        @Override
        public ExternalUser[] newArray(int size) {
            return new ExternalUser[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "ExternalUser{" +
                "read=" + read +
                ", timestamp='" + timestamp + '\'' +
                ", idToken='" + idToken + '\'' +
                ", user=" + user +
                '}';
    }
}

package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ExternalUser implements Parcelable {
    private boolean read;
    private String timestamp;
    private String username;

    public ExternalUser() {}

    public ExternalUser(boolean read, String timestamp, String username) {
        this.read = read;
        this.timestamp = timestamp;
        this.username = username;
    }

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
        dest.writeString(this.timestamp);
        dest.writeString(this.username);
    }

    public void readFromParcel(Parcel source) {
        this.read = source.readByte() != 0;
        this.timestamp = source.readString();
        this.username = source.readString();
    }

    protected ExternalUser(Parcel in) {
        this.read = in.readByte() != 0;
        this.timestamp = in.readString();
        this.username = in.readString();
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

    @Override
    public String toString() {
        return "ExternalUser{" +
                "read=" + read +
                ", timestamp='" + timestamp + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ExternalUser implements Parcelable {
    private boolean read;
    private Date timestamp;
    private String username;

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

    public ExternalUser() {
    }

    protected ExternalUser(Parcel in) {
        this.read = in.readByte() != 0;
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.username = in.readString();
    }

    public static final Parcelable.Creator<ExternalUser> CREATOR = new Parcelable.Creator<ExternalUser>() {
        @Override
        public ExternalUser createFromParcel(Parcel source) {
            return new ExternalUser(source);
        }

        @Override
        public ExternalUser[] newArray(int size) {
            return new ExternalUser[size];
        }
    };
}

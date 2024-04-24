package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {

    private String idToken;
    private boolean isRead;
    private long timestamp;

    public Notification() {}

    public Notification(String idToken, boolean isRead, long timestamp) {
        this.idToken = idToken;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idToken);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
        dest.writeLong(this.timestamp);
    }

    public void readFromParcel(Parcel source) {
        this.idToken = source.readString();
        this.isRead = source.readByte() != 0;
        this.timestamp = source.readLong();
    }

    protected Notification(Parcel in) {
        this.idToken = in.readString();
        this.isRead = in.readByte() != 0;
        this.timestamp = in.readLong();
    }

    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
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
    public String toString() {
        return "Notification{" +
                "idToken='" + idToken + '\'' +
                ", isRead=" + isRead +
                ", timestamp=" + timestamp +
                '}';
    }
}
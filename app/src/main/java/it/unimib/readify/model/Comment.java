package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;


public class Comment implements Parcelable {

    private String commentId;
    private String content;
    private String idToken;
    private long timestamp;
    private User user;

    public Comment() {
        // Default constructor for Firebase
    }

    public Comment(String commentId, String content, String idToken, long timestamp) {
        this.commentId = commentId;
        this.content = content;
        this.idToken = idToken;
        this.timestamp = timestamp;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commentId);
        dest.writeString(this.content);
        dest.writeString(this.idToken);
        dest.writeLong(this.timestamp);
        dest.writeParcelable(this.user, flags);
    }

    public void readFromParcel(Parcel source) {
        this.commentId = source.readString();
        this.content = source.readString();
        this.idToken = source.readString();
        this.timestamp = source.readLong();
        this.user = source.readParcelable(User.class.getClassLoader());
    }

    protected Comment(Parcel in) {
        this.commentId = in.readString();
        this.content = in.readString();
        this.idToken = in.readString();
        this.timestamp = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", content='" + content + '\'' +
                ", idToken='" + idToken + '\'' +
                ", timestamp=" + timestamp +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return timestamp == comment.timestamp && Objects.equals(commentId, comment.commentId) && Objects.equals(content, comment.content) && Objects.equals(idToken, comment.idToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, content, idToken, timestamp);
    }
}
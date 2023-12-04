package it.unimib.readify.model;

import java.util.Date;

public class Comment {
    private String userId;
    private String comment;
    private Date date;
    private int postId;

    // Constructor
    public Comment(String userId, String comment, Date date, int postId) {
        this.userId = userId;
        this.comment = comment;
        this.date = date;
        this.postId = postId;
    }

    // Getter and Setter methods

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}

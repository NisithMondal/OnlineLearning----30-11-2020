package com.nisith.onlinelearning.Model;

public class Comment {
    private String commentMessage;
    private String userId;
    private String date;
    private String timeStamp;
    public Comment(){}

    public Comment(String commentMessage, String userId, String date, String timeStamp) {
        this.commentMessage = commentMessage;
        this.userId = userId;
        this.date = date;
        this.timeStamp = timeStamp;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}

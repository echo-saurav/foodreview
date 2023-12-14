package com.example.productreview.comments;

import com.google.firebase.database.ServerValue;

import java.util.Map;

public class CommentValueHolder {
    private String userName,commentText,uid;
    public long time;

    CommentValueHolder(){}

    public CommentValueHolder(String userName, String commentText, String uid) {
        this.userName = userName;
        this.commentText = commentText;
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Map<String, String> getTIME() {
        return ServerValue.TIMESTAMP;
    }

    public String getUid() {
        return uid;
    }
}

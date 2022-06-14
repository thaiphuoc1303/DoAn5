package com.example.doantest.Model;

import com.example.doantest.Model.UserModel;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class ResultPostModel implements Serializable  {
    UserModel author;
    String  content, pathPicture, token;
    int likeCount, commentCount;
    long time;

    public ResultPostModel(){}

    public ResultPostModel(UserModel author, String content, String pathPicture, int likeCount, int commentCount, long time) {
        this.author = author;
        this.content = content;
        this.pathPicture = pathPicture;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.time = time;
    }

    public PostModel getPostModel(){
        return new PostModel(author, content, pathPicture, time, likeCount, commentCount);
    }

    public UserModel getAuthor() {
        return author;
    }

    public void setAuthor(UserModel author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPathPicture() {
        return pathPicture;
    }

    public void setPathPicture(String pathPicture) {
        this.pathPicture = pathPicture;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

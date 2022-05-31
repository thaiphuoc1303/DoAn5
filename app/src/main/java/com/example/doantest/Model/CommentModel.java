package com.example.doantest.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CommentModel {
    String uID, name, content, pathPicture, token;
    long time;

    public CommentModel(){}
    public CommentModel(String uID, String name, String content, String pathPicture, long time) {
        this.uID = uID;
        this.name = name;
        this.content = content;
        this.pathPicture = pathPicture;
        this.time = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

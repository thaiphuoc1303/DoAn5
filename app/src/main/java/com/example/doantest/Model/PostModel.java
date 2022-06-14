package com.example.doantest.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class PostModel{
    public UserModel author;
    public String  content, pathPicture;
    public int likeCount, commentCount;
    public long time;

    public PostModel() {
    }

    public PostModel(UserModel author, String content, String pathPicture,
                     long time, int likeCount, int commentCount) {
        this.author = author;
        this.content = content;
        this.pathPicture = pathPicture;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.time = time;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("content", content);
        result.put("pathPicture", pathPicture);
        result.put("likeCount", likeCount);
        result.put("commentCount", commentCount);
        result.put("time", time);
        return result;
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

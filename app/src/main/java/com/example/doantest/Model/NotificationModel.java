package com.example.doantest.Model;

public class NotificationModel implements Comparable<NotificationModel> {
    String content, token;
    int icon;
    long time;

    public NotificationModel(String token, String content, int icon, long time) {
        this.content = content;
        this.token = token;
        this.icon = icon;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    @Override
    public int compareTo(NotificationModel notificationItem){
        return (this.time <notificationItem.getTime()? 1 : (this.time == notificationItem.getTime() ? 0 : -1));
    }
}

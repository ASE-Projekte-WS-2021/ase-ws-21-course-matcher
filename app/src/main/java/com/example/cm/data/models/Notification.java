package com.example.cm.data.models;

import java.util.Date;

public class Notification {

    public enum NotificationType {
        FRIEND_REQUEST,
        MEETUP_REQUEST,
        MEETUP_CANCELLED
    }

    private String id;
    private String title;
    private String content;
    private String userId;
    private NotificationType type;
    private Date createdAt;

    public Notification() {}

    public Notification(String title, String content, NotificationType type, String userId) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.userId = userId;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

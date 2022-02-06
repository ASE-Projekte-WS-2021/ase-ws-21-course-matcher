package com.example.cm.config;

public enum CollectionConfig {

    USERS("users"),
    MEETUPS("meetups"),
    FRIEND_REQUESTS("friend-requests"),
    MEETUP_REQUESTS("meetup-requests");

    private final String collection;

    CollectionConfig(String collection) {
        this.collection = collection;
    }

    public String toString() {
        return collection;
    }
}

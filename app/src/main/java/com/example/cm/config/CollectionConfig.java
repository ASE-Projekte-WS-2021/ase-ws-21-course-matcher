package com.example.cm.config;

import androidx.annotation.NonNull;

public enum CollectionConfig {

    USERS("users"),
    MEETUPS("meetups"),
    FRIEND_REQUESTS("friend-requests"),
    MEETUP_REQUESTS("meetup-requests");

    private final String collection;

    CollectionConfig(String collection) {
        this.collection = collection;
    }

    @NonNull
    public String toString() {
        return collection;
    }
}

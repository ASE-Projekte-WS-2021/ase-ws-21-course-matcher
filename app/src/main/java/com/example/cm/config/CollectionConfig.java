package com.example.cm.config;

public enum CollectionConfig {

    USERS("users"),
    MEETUPS("meetups"),
    NOTIFICATIONS("notifications");

    private final String collection;

    CollectionConfig(String collection) {
        this.collection = collection;
    }

    public String toString() {
        return collection;
    }
}
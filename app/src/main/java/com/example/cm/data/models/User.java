package com.example.cm.data.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

import java.util.List;

public class User {

    private String id;
    private String username, displayName, email, bio, profileImageString;
    private List<String> friends;
    private LatLng location;
    private boolean isSharingLocation;
    private Availability availability;

    public User() {
    }

    public User(String id, String username, String displayName, String email, String imgString) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.profileImageString = imgString;
        this.availability = Availability.AVAILABLE;
    }

    public User(String id, String username, String displayName, String email, List<String> friends, LatLng location, Availability availability, boolean isSharingLocation) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.friends = friends;
        this.location = location;
        this.availability = availability;
        this.isSharingLocation = isSharingLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImageString() {
        return profileImageString;
    }

    public void setProfileImageString(String profileImageString) {
        this.profileImageString = profileImageString;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public boolean getIsSharingLocation() {
        return isSharingLocation;
    }

    public void setIsSharingLocation(boolean sharingLocation) {
        isSharingLocation = sharingLocation;
    }
}
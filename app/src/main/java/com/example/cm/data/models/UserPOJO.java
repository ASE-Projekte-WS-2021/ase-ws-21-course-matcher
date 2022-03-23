package com.example.cm.data.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class UserPOJO {
    private String id;
    private String username, firstName, lastName, email, bio, profileImageString;
    private List<String> friends;
    private List<Double> location;
    private boolean isSharingLocation;
    private Availability availability;
    private byte[] profileImageBytes;

    public UserPOJO() {
        // Required empty constructor
    }

    public UserPOJO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.profileImageString = user.getProfileImageString();
        this.profileImageBytes = user.getProfileImageBytes();
        this.friends = user.getFriends();
        this.availability = user.getAvailability();
        this.isSharingLocation = user.getIsSharingLocation();

        this.location = new ArrayList<>();
        if (user.getLocation() != null) {
            this.location.add(user.getLocation().latitude);
            this.location.add(user.getLocation().longitude);
        }
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public byte[] getProfileImageBytes() {
        return profileImageBytes;
    }

    public void setProfileImageBytes(byte[] profileImageBytes) {
        this.profileImageBytes = profileImageBytes;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public boolean getIsSharingLocation() {
        return isSharingLocation;
    }

    public void setIsSharingLocation(boolean sharingLocation) {
        this.isSharingLocation = sharingLocation;
    }

    public User toObject() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        user.setBio(this.bio);
        user.setProfileImageString(this.profileImageString);
        user.setProfileImageBytes(this.profileImageBytes);
        user.setFriends(this.friends);
        user.setAvailability(this.availability);
        user.setIsSharingLocation(this.isSharingLocation);

        if (this.location != null) {
            LatLng location = new LatLng(this.location.get(0), this.location.get(1));
            user.setLocation(location);
        }

        return user;
    }
}

package com.example.cm.data.models;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerClusterItem implements ClusterItem {

    private final User user;
    private final Drawable profileImage;
    private final boolean isCurrentUser;

    public MarkerClusterItem(@NonNull User user, Drawable profileImage, boolean isCurrentUser) {
        this.user = user;
        this.profileImage = profileImage;
        this.isCurrentUser = isCurrentUser;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public Drawable getProfileImage() {
        return profileImage;
    }

    public User getUser() {
        return user;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return user.getLocation();
    }

    @Override
    public String getTitle() {
        return user.getFullName();
    }

    @Override
    public String getSnippet() {
        return "";
    }
}

package com.example.cm.data.models;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerClusterItem implements ClusterItem {

    private final User user;
    private int iconPicture;
    private Bitmap profileImage;

    public MarkerClusterItem(@NonNull User user, int iconPicture) {
        this.user = user;
        this.iconPicture = iconPicture;
    }

    public MarkerClusterItem(@NonNull User user, Bitmap profileImage) {
        this.user = user;
        this.profileImage = profileImage;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public int getIconPicture() {
        return iconPicture;
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

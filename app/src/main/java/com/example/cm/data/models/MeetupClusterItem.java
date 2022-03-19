package com.example.cm.data.models;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.example.cm.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MeetupClusterItem implements ClusterItem {

    private final Meetup meetup;
    private Drawable image;

    public MeetupClusterItem(@NonNull Meetup meetup) {
        this.meetup = meetup;
    }

    public Drawable getImage() {
        return image;
    }

    public Meetup getMeetup() {
        return meetup;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return meetup.getLocation();
    }

    @Override
    public String getTitle() {
        return "Meetup Standort";
    }

    @Override
    public String getSnippet() {
        return "";
    }
}

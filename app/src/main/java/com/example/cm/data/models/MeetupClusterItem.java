package com.example.cm.data.models;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MeetupClusterItem implements ClusterItem {

    private final Meetup meetup;

    public MeetupClusterItem(@NonNull Meetup meetup) {
        this.meetup = meetup;
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
        return "";
    }

    @Override
    public String getSnippet() {
        return "";
    }
}

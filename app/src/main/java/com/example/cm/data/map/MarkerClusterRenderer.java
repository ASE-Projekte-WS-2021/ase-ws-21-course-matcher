package com.example.cm.data.map;

import android.content.Context;

import com.example.cm.data.models.MarkerClusterItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MarkerClusterRenderer<T extends MarkerClusterItem> extends DefaultClusterRenderer<T> {

    public MarkerClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<T> clusterManager){
        super(context, googleMap, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }
}

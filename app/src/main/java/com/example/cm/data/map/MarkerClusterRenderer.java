package com.example.cm.data.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.cm.data.models.MarkerClusterItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

public class MarkerClusterRenderer<T extends MarkerClusterItem> extends DefaultClusterRenderer<MarkerClusterItem> {
    private final ImageView imageView;
    private final IconGenerator iconGenerator;
    private final int padding = 20;

    public MarkerClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<MarkerClusterItem> clusterManager) {
        super(context, googleMap, clusterManager);
        imageView = new ImageView(context);
        iconGenerator = new IconGenerator(context);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull MarkerClusterItem item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        if (item.getUser().getProfileImageUrl() == null) {
            imageView.setImageResource(item.getIconPicture());
            Bitmap icon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
            return;
        }

        if (item.getProfileImage() != null) {
            Timber.d("Marker with profile image loaded....");
            imageView.setImageBitmap(item.getProfileImage());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getProfileImage())).title(item.getTitle());
        }
    }

    // Only show clusters when 2 or more items are close together
    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }
}

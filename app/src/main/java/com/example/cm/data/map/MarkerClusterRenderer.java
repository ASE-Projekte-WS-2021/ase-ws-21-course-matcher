package com.example.cm.data.map;

import static com.example.cm.Constants.MARKER_PADDING;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.cm.data.models.MarkerClusterItem;
import com.example.cm.data.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MarkerClusterRenderer<T extends MarkerClusterItem> extends DefaultClusterRenderer<MarkerClusterItem> {
    private final ImageView imageView;
    private final IconGenerator iconGenerator;

    public MarkerClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<MarkerClusterItem> clusterManager) {
        super(context, googleMap, clusterManager);
        imageView = new ImageView(context);
        iconGenerator = new IconGenerator(context);
        imageView.setPadding(MARKER_PADDING, MARKER_PADDING, MARKER_PADDING, MARKER_PADDING);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull MarkerClusterItem item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        User user = item.getUser();
        String profileImageUrl = user.getProfileImageUrl();

        // User does not have a custom profile image set
        // Choose a default image
        if (profileImageUrl == null || profileImageUrl.isEmpty()) {
            imageView.setImageResource(item.getIconPicture());
            Bitmap icon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
            return;
        }

        imageView.setImageBitmap(item.getProfileImage());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getProfileImage())).title(item.getTitle());
    }

    // Only show clusters when 2 or more items are close together
    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }
}

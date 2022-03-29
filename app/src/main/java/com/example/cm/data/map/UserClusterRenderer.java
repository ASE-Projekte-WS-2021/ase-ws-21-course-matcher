package com.example.cm.data.map;

import static com.example.cm.Constants.CURRENT_USER_Z_INDEX;
import static com.example.cm.Constants.MARKER_PADDING;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.cm.R;
import com.example.cm.data.models.MarkerClusterItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class UserClusterRenderer extends DefaultClusterRenderer<MarkerClusterItem> {
    private final ImageView imageView;
    private final IconGenerator iconGenerator;
    private final Context context;

    public UserClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<MarkerClusterItem> clusterManager) {
        super(context, googleMap, clusterManager);
        this.context = context;
        imageView = new ImageView(context);
        iconGenerator = new IconGenerator(context);
        imageView.setPadding(MARKER_PADDING, MARKER_PADDING, MARKER_PADDING, MARKER_PADDING);
        iconGenerator.setContentView(imageView);
    }


    @Override
    protected void onBeforeClusterItemRendered(@NonNull MarkerClusterItem item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(item.getProfileImage());
        markerOptions.icon(markerIcon).title(item.getTitle());

        // Make sure to render current user above other users
        if (item.isCurrentUser()) {
            markerOptions.zIndex(CURRENT_USER_Z_INDEX);
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }

    @Override
    protected int getColor(int clusterSize) {
        return ContextCompat.getColor(context, R.color.gray600);
    }
}

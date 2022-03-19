package com.example.cm.data.map;

import static com.example.cm.Constants.MARKER_SIZE;
import static com.example.cm.Constants.MAX_PARTICIPANT_COUNT_TO_SHOW;
import static com.example.cm.Constants.MEETUP_COUNT_RADIUS;
import static com.example.cm.Constants.MEETUP_COUNT_TEXT_SIZE;
import static com.example.cm.Constants.MEETUP_Z_INDEX;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.cm.R;
import com.example.cm.data.models.MeetupClusterItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MeetupClusterRenderer extends DefaultClusterRenderer<MeetupClusterItem> {
    private final IconGenerator iconGenerator;
    private final Context context;

    public MeetupClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<MeetupClusterItem> clusterManager) {
        super(context, googleMap, clusterManager);
        this.context = context;
        iconGenerator = new IconGenerator(context);

        Drawable meetupIcon = ContextCompat.getDrawable(context, R.drawable.ic_meetup_marker);
        if (meetupIcon != null) {
            Bitmap marker = getMarkerIconFromDrawable(meetupIcon);
            iconGenerator.setBackground(new BitmapDrawable(context.getResources(), marker));
        }
    }

    private Bitmap getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(MARKER_SIZE, MARKER_SIZE, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, MARKER_SIZE, MARKER_SIZE);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull MeetupClusterItem item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        Bitmap icon = iconGenerator.makeIcon();
        addParticipantCount(item, icon);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        markerOptions.zIndex(MEETUP_Z_INDEX);
    }

    private void addParticipantCount(MeetupClusterItem item, Bitmap icon) {
        Canvas canvas = new Canvas(icon);
        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int participantCount = item.getMeetup().getConfirmedFriends().size();

        // Draw background circle for participant count
        backgroundPaint.setColor(ContextCompat.getColor(context, R.color.orange500));
        canvas.drawCircle(canvas.getWidth() - MEETUP_COUNT_RADIUS, MEETUP_COUNT_RADIUS, MEETUP_COUNT_RADIUS, backgroundPaint);

        // Draw text for participant count
        textPaint.setTextSize(MEETUP_COUNT_TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(ContextCompat.getColor(context, R.color.white));

        String numberOfParticipants = String.valueOf(participantCount);
        if (participantCount > MAX_PARTICIPANT_COUNT_TO_SHOW) {
            numberOfParticipants = context.getString(R.string.moreThanNineParticipants);
        }

        // Get the position of the text, to center it in the circle
        Rect bounds = new Rect();
        backgroundPaint.getTextBounds(numberOfParticipants, 0, numberOfParticipants.length(), bounds);
        int textXPos = canvas.getWidth() - MEETUP_COUNT_RADIUS;
        int textYPos = bounds.height() + MEETUP_COUNT_RADIUS;
        canvas.drawText(numberOfParticipants, textXPos, textYPos, textPaint);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > MEETUP_Z_INDEX;
    }
}

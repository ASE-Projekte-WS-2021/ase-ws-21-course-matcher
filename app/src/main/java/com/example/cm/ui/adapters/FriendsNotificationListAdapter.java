package com.example.cm.ui.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.cm.R;
import com.example.cm.data.models.Notification;

public class FriendsNotificationListAdapter extends NotificationListAdapter{
    public FriendsNotificationListAdapter(OnFriendAcceptanceListener listener) {
        super(listener);
    }

    @NonNull
    @Override
    //todo
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Notification notification = mNotifications.get(position);
        boolean isAccepted = notification.getState() == Notification.NotificationState.NOTIFICATION_ACCEPTED;

        holder.getTvTitle().setText(R.string.friend_request_title);
        holder.getTvContent().setText(isAccepted ? R.string.friend_accepted_text : R.string.friend_request_text);
        holder.getBtnAccept().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
        holder.getBtnDecline().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
    }
}

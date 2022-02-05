package com.example.cm.ui.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.cm.R;
import com.example.cm.data.models.Notification;

public class MeetupNotificationListAdapter extends NotificationListAdapter{

    public MeetupNotificationListAdapter(OnFriendAcceptanceListener listener) {
        super(listener);
    }

    @NonNull
    @Override
    //todo
    public NotificationListAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.NotificationViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Notification notification = mNotifications.get(position);
        boolean isAccepted = notification.getState() == Notification.NotificationState.NOTIFICATION_ACCEPTED;

        int content = 0;
        switch(notification.getType()){
            case MEETUP_REQUEST:
                holder.getTvTitle().setText(notification.toString());
                content = isAccepted ? R.string.meetup_accepted_text : R.string.meetup_request_text;
                break;
            case MEETUP_ACCEPTED:
                holder.getTvTitle().setText(notification.toString());
                isAccepted = true;
                content = R.string.meetup_accepted_text;
                break;
            case MEETUP_DECLINED:
                holder.getTvTitle().setText(notification.toString());
                isAccepted = true;
                content = R.string.meetup_declined_text;
                break;
        }

        holder.getTvContent().setText(content);
        holder.getBtnAccept().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
        holder.getBtnDecline().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
    }
}

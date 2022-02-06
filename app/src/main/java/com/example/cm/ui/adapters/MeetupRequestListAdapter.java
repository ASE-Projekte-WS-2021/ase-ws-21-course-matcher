package com.example.cm.ui.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.cm.R;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;

public class MeetupRequestListAdapter extends RequestListAdapter {

    public MeetupRequestListAdapter(OnRequestAcceptanceListener listener) {
        super(listener);
    }

    @NonNull
    @Override
    //todo
    public RequestListAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestListAdapter.NotificationViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MeetupRequest notification = (MeetupRequest) mRequests.get(position);
        boolean isAccepted = notification.getState() == Request.RequestState.REQUEST_ACCEPTED;

        int content = 0;
        switch(notification.getType()){
            case MEETUP_REQUEST:
                holder.getTvTitle().setText(notification.toString());
                content = isAccepted ? R.string.meetup_accepted_text : R.string.meetup_request_text;
                break;
            case MEETUP_INFO_ACCEPTED:
                holder.getTvTitle().setText(notification.toString());
                isAccepted = true;
                content = R.string.meetup_accepted_text;
                break;
            case MEETUP_INFO_DECLINED:
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

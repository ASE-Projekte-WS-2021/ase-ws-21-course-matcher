package com.example.cm.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.ItemMeetupRequestBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MeetupRequestListAdapter extends RecyclerView.Adapter<MeetupRequestListAdapter.MeetupRequestViewHolder> {

    private List<MeetupRequest> mRequests;
    private OnMeetupRequestAcceptanceListener listener;

    public MeetupRequestListAdapter(OnMeetupRequestAcceptanceListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRequests(List<MeetupRequest> newRequests){
        // filter out declined request to not display them again
        for (MeetupRequest request : newRequests) {
            if (request.getState() == Request.RequestState.REQUEST_DECLINED) {
                newRequests.remove(request);
            }
        }

        if(mRequests == null){
            mRequests = newRequests;
            notifyDataSetChanged();
            return;
        }
        mRequests = newRequests;
    }

    @NonNull
    @Override
    public MeetupRequestListAdapter.MeetupRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMeetupRequestBinding binding = ItemMeetupRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetupRequestListAdapter.MeetupRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetupRequestListAdapter.MeetupRequestViewHolder holder, int position) {
        MeetupRequest request = mRequests.get(position);

        String user = "@" + request.getSenderName();
        String date = request.getCreationTimeAgo();
        String meetupTime = request.getMeetupAt() + " Uhr";
        String location = request.getLocation();

        boolean isAccepted = request.getState() == Request.RequestState.REQUEST_ACCEPTED;

        int content = 0;
        switch (request.getType()) {
            case MEETUP_REQUEST:
                content = isAccepted ? R.string.meetup_accepted_text : R.string.meetup_request_description;
                break;
            case MEETUP_INFO_ACCEPTED:
                isAccepted = true;
                content = R.string.meetup_accepted_text;
                break;
            case MEETUP_INFO_DECLINED:
                isAccepted = true;
                content = R.string.meetup_declined_text;
                break;
        }

        holder.getTvMeetupTime().setText(meetupTime);
        holder.getTvLocation().setText(location);
        holder.getTvSender().setText(user);
        holder.getTvDate().setText(date);
        holder.getTvDescription().setText(content);
        holder.getBtnAccept().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
        holder.getBtnDecline().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if(mRequests == null){
            return 0;
        }
        return mRequests.size();
    }

    public interface OnMeetupRequestAcceptanceListener {
        void onAccept(MeetupRequest request);
        void onDecline(MeetupRequest request);
        void onUndo(MeetupRequest request, int position);
    }

    public class MeetupRequestViewHolder extends RecyclerView.ViewHolder {

        private final ItemMeetupRequestBinding binding;

        public MeetupRequestViewHolder(ItemMeetupRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        private void setListeners() {
            binding.notificationAcceptButton.setOnClickListener(view -> onAccept());
            binding.notificationDeclineButton.setOnClickListener(view -> onDecline());
        }

        private void onAccept() {
            MeetupRequest request = mRequests.get(getAdapterPosition());
            listener.onAccept(request);
            notifyItemChanged(getAdapterPosition());
        }

        private void onUndo(MeetupRequest request, int position){
            listener.onUndo(request, position);
            notifyItemInserted(position);
        }

        private void onDecline(){
            int position = getAdapterPosition();
            MeetupRequest request = mRequests.get(position);
            listener.onDecline(request);
            notifyItemRemoved(position);
            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.decline_snackbar_text, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo_snackbar_text, view -> onUndo(request, position));
            snackbar.show();
        }

        /**
         * Getters for the views in the list item
         */
        public TextView getTvMeetupTime() {
            return binding.meetupTimeRequestTextView;
        }

        public TextView getTvLocation() {
            return binding.meetupLocationRequestTextView;
        }

        public TextView getTvSender() {
            return binding.meetupSenderTextView;
        }

        public TextView getTvDescription() {
            return binding.meetupSenderDescriptionTextView;
        }

        public TextView getTvDate() {
            return binding.notificationDateTextView;
        }

        public ImageView getBtnAccept(){
            return binding.notificationAcceptButton;
        }

        public ImageView getBtnDecline(){
            return binding.notificationDeclineButton;
        }
    }
}

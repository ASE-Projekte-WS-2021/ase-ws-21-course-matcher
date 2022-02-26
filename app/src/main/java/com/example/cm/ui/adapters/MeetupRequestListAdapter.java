package com.example.cm.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.ItemMeetupRequestBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Iterator;
import java.util.List;

public class MeetupRequestListAdapter extends RecyclerView.Adapter<MeetupRequestListAdapter.MeetupRequestViewHolder> {

    private ViewGroup parent;
    private List<MutableLiveData<MeetupRequest>> mRequests;
    private final OnMeetupRequestListener listener;

    public MeetupRequestListAdapter(List<MutableLiveData<MeetupRequest>> requests, OnMeetupRequestListener listener) {
        mRequests = requests;
        this.listener = listener;
    }

    public void deleteItem(int position) {
        Log.e("REMOVE", "delete");
        MeetupRequest request = mRequests.get(position).getValue();
        Request.RequestState previousState = request.getState();
        mRequests.remove(position);
        notifyItemRemoved(position);
        listener.onItemDeleted(request);
        Snackbar snackbar = Snackbar.make(parent, R.string.delete_snackbar_text, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo_snackbar_text, view -> onUndoDelete(request, position, previousState));
        snackbar.show();
    }

    private void onUndoDelete(MeetupRequest request, int position, Request.RequestState previousState){
        listener.onUndoDelete(request, position, previousState);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public MeetupRequestListAdapter.MeetupRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        ItemMeetupRequestBinding binding = ItemMeetupRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetupRequestListAdapter.MeetupRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetupRequestListAdapter.MeetupRequestViewHolder holder, int position) {
        Context context = holder.binding.getRoot().getContext();
        MeetupRequest request = mRequests.get(position).getValue();

        String user = String.format("@%s ", request.getSenderName());
        String date = request.getCreationTimeAgo();
        String location = request.getLocation();

        boolean isAccepted = request.getState() == Request.RequestState.REQUEST_ACCEPTED;

        switch (request.getPhase()){
            case MEETUP_UPCOMING:
                holder.getTvMeetupTime().setText(request.getFormattedTime());
                break;
            case MEETUP_ACTIVE:
                holder.getTvMeetupTime().setText(context.getString(R.string.meetup_active_text, request.getFormattedTime()));
                break;
            case MEETUP_ENDED:
                int color = context.getResources().getColor(R.color.outgreyed);
                holder.getTvMeetupTime().setText(R.string.meetup_ended_text);

                holder.getTvMeetupTime().setTextColor(color);
                holder.getTvLocation().setTextColor(color);
                holder.getTvSender().setTextColor(color);
                holder.getTvDescription().setTextColor(color);
                holder.getBtnAccept().setImageResource(R.drawable.accept_btn_disabled);
                holder.getBtnDecline().setImageResource(R.drawable.decline_btn_disabled);
                holder.getBtnAccept().setOnClickListener(null);
                holder.getBtnDecline().setOnClickListener(null);

                break;
        }

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

    public interface OnMeetupRequestListener {
        void onItemClicked(String id);
        void onItemDeleted(MeetupRequest request);
        void onAccept(MeetupRequest request);
        void onDecline(MeetupRequest request);
        void onUndoDecline(MeetupRequest request, int position);
        void onUndoDelete(MeetupRequest request, int position, Request.RequestState previousState);
    }

    public class MeetupRequestViewHolder extends RecyclerView.ViewHolder {

        private final ItemMeetupRequestBinding binding;

        public MeetupRequestViewHolder(ItemMeetupRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        private void setListeners() {
            binding.getRoot().setOnClickListener(view -> onItemClicked());
            binding.acceptButton.setOnClickListener(view -> onAccept());
            binding.declineButton.setOnClickListener(view -> onDecline());
        }

        private void onItemClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;
            listener.onItemClicked(mRequests.get(position).getValue().getMeetupId());
        }

        private void onAccept() {
            MeetupRequest request = mRequests.get(getAdapterPosition()).getValue();
            listener.onAccept(request);
            notifyItemChanged(getAdapterPosition());
        }

        private void onUndo(MeetupRequest request, int position){
            listener.onUndoDecline(request, position);
            notifyItemInserted(position);
        }

        private void onDecline(){
            int position = getAdapterPosition();
            MeetupRequest request = mRequests.get(position).getValue();
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
            return binding.meetupTimeTextView;
        }

        public TextView getTvLocation() {
            return binding.locationTextView;
        }

        public TextView getTvSender() {
            return binding.meetupSenderTextView;
        }

        public TextView getTvDescription() {
            return binding.meetupSenderDescriptionTextView;
        }

        public TextView getTvDate() {
            return binding.sentDateTextView;
        }

        public ImageView getBtnAccept(){
            return binding.acceptButton;
        }

        public ImageView getBtnDecline(){
            return binding.declineButton;
        }
    }
}



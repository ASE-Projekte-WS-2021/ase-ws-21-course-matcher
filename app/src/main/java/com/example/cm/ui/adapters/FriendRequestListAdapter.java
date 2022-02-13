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
import com.example.cm.databinding.ItemFriendRequestBinding;
import com.example.cm.databinding.ItemMeetupRequestBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FriendRequestListAdapter extends RecyclerView.Adapter<FriendRequestListAdapter.FriendRequestViewHolder>{

    private List<FriendRequest> mRequests;
    private FriendRequestListAdapter.OnFriendRequestAcceptanceListener listener;

    public FriendRequestListAdapter(OnFriendRequestAcceptanceListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRequests(List<FriendRequest> newRequests){
        // filter out declined request to not display them again
        for (FriendRequest request : newRequests) {
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
    public FriendRequestListAdapter.FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendRequestBinding binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendRequestListAdapter.FriendRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestListAdapter.FriendRequestViewHolder holder, int position) {
        FriendRequest request = mRequests.get(position);

        String user = "@" + request.getSenderName();
        String date = request.getCreationTimeAgo();

        holder.getTvSender().setText(user);
        holder.getTvDate().setText(date);

        boolean isAccepted = request.getState() == Request.RequestState.REQUEST_ACCEPTED;

        holder.getTvTitle().setText(R.string.friend_request_title);
        holder.getTvContent().setText(isAccepted ? R.string.friend_accepted_text : R.string.friend_request_text);
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

    public interface OnFriendRequestAcceptanceListener {
        void onAccept(FriendRequest request);
        void onDecline(FriendRequest request);
        void onUndo(FriendRequest request, int position);
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        private final ItemFriendRequestBinding binding;

        public FriendRequestViewHolder(ItemFriendRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        private void setListeners() {
            binding.notificationAcceptButton.setOnClickListener(view -> onAccept());
            binding.notificationDeclineButton.setOnClickListener(view -> onDecline());
        }

        private void onAccept() {
            FriendRequest request = mRequests.get(getAdapterPosition());
            listener.onAccept(request);
            notifyItemChanged(getAdapterPosition());
        }

        private void onUndo(FriendRequest request, int position){
            listener.onUndo(request, position);
            notifyItemInserted(position);
        }

        private void onDecline(){
            int position = getAdapterPosition();
            FriendRequest request = mRequests.get(position);
            listener.onDecline(request);
            notifyItemRemoved(position);
            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.decline_snackbar_text, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo_snackbar_text, view -> onUndo(request, position));
            snackbar.show();
        }

        /**
         * Getters for the views in the list item
         */
        public TextView getTvTitle() {
            return binding.notificationTitleTextView;
        }

        public TextView getTvContent() {
            return binding.notificationContentTextView;
        }

        public TextView getTvSender() {
            return binding.notificationSenderTextView;
        }

        public TextView getTvDate() {
            return binding.notificationDateTextView;
        }

        public ImageView getIvProfilePicture(){
            return binding.notificationImageView;
        }

        public Button getBtnAccept(){
            return binding.notificationAcceptButton;
        }

        public Button getBtnDecline(){
            return binding.notificationDeclineButton;
        }
    }
}

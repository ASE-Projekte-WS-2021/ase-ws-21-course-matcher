package com.example.cm.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.ItemFriendRequestBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Iterator;
import java.util.List;

public class FriendRequestListAdapter extends RecyclerView.Adapter<FriendRequestListAdapter.FriendRequestViewHolder>{

    private List<FriendRequest> mRequests;
    private OnFriendRequestListener listener;

    public FriendRequestListAdapter(OnFriendRequestListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRequests(List<FriendRequest> newRequests){
        // filter out declined request to not display them again
        Iterator<FriendRequest> iterator = newRequests.iterator();
        while (iterator.hasNext()) {
            FriendRequest request = iterator.next();
            if (request.getState() == Request.RequestState.REQUEST_DECLINED) {
                iterator.remove();
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
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendRequest request = mRequests.get(position);

        String user = request.getSenderName();
        String date = request.getCreationTimeAgo();
        boolean isAccepted = request.getState() == Request.RequestState.REQUEST_ACCEPTED;

        holder.getTvSender().setText(user);
        holder.getTvSentDate().setText(date);
        holder.getTvDescription().setText(isAccepted ? R.string.friend_accepted_text : R.string.friend_request_text);
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

    public interface OnFriendRequestListener {
        void onItemClicked(String id);
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
            binding.getRoot().setOnClickListener(v -> onItemClicked());
            binding.acceptButton.setOnClickListener(view -> onAccept());
            binding.declineButton.setOnClickListener(view -> onDecline());
        }

        private void onItemClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;
            listener.onItemClicked(mRequests.get(position).getSenderId());
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

        public ImageView getIvProfilePicture(){
            return binding.senderProfileImageView;
        }

        public TextView getTvSender() {
            return binding.senderTextView;
        }

        public TextView getTvDescription() {
            return binding.descriptionTextView;
        }

        public TextView getTvSentDate() {
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

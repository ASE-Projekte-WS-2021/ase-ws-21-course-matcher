package com.example.cm.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.FriendRequestDTO;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.ItemFriendRequestBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class FriendRequestListAdapter extends RecyclerView.Adapter<FriendRequestListAdapter.FriendRequestViewHolder>{

    private ViewGroup parent;
    private List<FriendRequestDTO> mRequests;
    private final OnFriendRequestListener listener;

    public FriendRequestListAdapter(OnFriendRequestListener listener) {
        this.listener = listener;
    }

    public void setRequests(List<FriendRequestDTO> newRequests) {
        if (mRequests == null) {
            mRequests = newRequests;
            notifyItemRangeInserted(0, newRequests.size());
            return;
        }

        DiffUtil.DiffResult result = calculateDiffFriendRequests(mRequests, newRequests);
        mRequests = newRequests;
        result.dispatchUpdatesTo(this);
    }

    public static DiffUtil.DiffResult calculateDiffFriendRequests(List<FriendRequestDTO> oldRequests, List<FriendRequestDTO> newRequests) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldRequests.size();
            }

            @Override
            public int getNewListSize() {
                return newRequests.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(Objects.requireNonNull(oldRequests.get(oldItemPosition)).getId(),
                        Objects.requireNonNull(newRequests.get(newItemPosition)).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                FriendRequestDTO newRequest = newRequests.get(newItemPosition);
                FriendRequestDTO oldRequest = oldRequests.get(oldItemPosition);

                return Objects.equals(Objects.requireNonNull(newRequest).getId(), Objects.requireNonNull(oldRequest).getId());
            }
        });
    }

    public void deleteItem(int position) {
        FriendRequestDTO request = mRequests.get(position);
        Request.RequestState previousState = Objects.requireNonNull(request).getState();
        listener.onItemDeleted(position);
        Snackbar snackbar = Snackbar.make(parent, R.string.delete_snackbar_text, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo_snackbar_text, view -> onUndoDelete(request, position, previousState));
        snackbar.show();
    }

    private void onUndoDelete(FriendRequestDTO request, int position, Request.RequestState previousState){
        listener.onUndo(request, position, previousState);
        mRequests.add(position, request);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public FriendRequestListAdapter.FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        ItemFriendRequestBinding binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendRequestListAdapter.FriendRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendRequestDTO request = mRequests.get(position);

        String senderName = Objects.requireNonNull(request).getSenderName();
        String senderUsername = Objects.requireNonNull(request).getSenderUserName();
        String date = request.getCreationTimeAgo();
        boolean isAccepted = request.getState() == Request.RequestState.REQUEST_ACCEPTED;

        holder.getTvSender().setText(senderName);
        holder.getTvSenderUsername().setText(senderUsername);
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
        void onItemDeleted(int position);
        void onAccept(int position);
        void onDecline(int position);
        void onUndo(FriendRequestDTO request, int position, Request.RequestState previousState);
    }

    /**
     * Fix for the bug in the RecyclerView that caused it to show incorrect data (e.g. image)
     * Source: https://www.solutionspirit.com/on-scrolling-recyclerview-change-values/
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
            listener.onItemClicked(Objects.requireNonNull(mRequests.get(position)).getSenderId());
        }

        private void onAccept() {
            listener.onAccept(getAdapterPosition());
            notifyItemChanged(getAdapterPosition());
        }

        private void onUndo(FriendRequestDTO request, int position, Request.RequestState previousState){
            listener.onUndo(request, position, previousState);
            notifyItemInserted(position);
        }

        private void onDecline(){
            int position = getAdapterPosition();
            FriendRequestDTO request = mRequests.get(position);
            Request.RequestState previousState = Objects.requireNonNull(request).getState();
            listener.onDecline(position);
            notifyItemRemoved(position);
            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.decline_snackbar_text, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo_snackbar_text, view -> onUndo(request, position, previousState));
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

        public TextView getTvSenderUsername() {
            return binding.tvSenderUsername;
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

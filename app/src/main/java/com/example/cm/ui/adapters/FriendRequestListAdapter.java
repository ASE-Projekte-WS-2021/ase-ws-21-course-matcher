package com.example.cm.ui.adapters;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.databinding.ItemFriendRequestBinding;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class FriendRequestListAdapter extends RecyclerView.Adapter<FriendRequestListAdapter.FriendRequestViewHolder> {

    private final OnFriendRequestListener listener;
    private ViewGroup parent;
    private List<User> users;
    private List<FriendRequest> mRequests;

    public FriendRequestListAdapter(OnFriendRequestListener listener) {
        this.listener = listener;
    }

    public static DiffUtil.DiffResult calculateDiffFriendRequests(List<FriendRequest> oldRequests,
                                                                  List<FriendRequest> newRequests) {
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
                FriendRequest newRequest = newRequests.get(newItemPosition);
                FriendRequest oldRequest = oldRequests.get(oldItemPosition);

                return Objects.equals(Objects.requireNonNull(newRequest).getId(),
                        Objects.requireNonNull(oldRequest).getId());
            }
        });
    }

    public void setRequests(List<FriendRequest> newRequests, List<User> users) {
        if (mRequests == null) {
            mRequests = newRequests;
            this.users = users;
            notifyItemRangeInserted(0, newRequests.size());
            return;
        }

        DiffUtil.DiffResult result = calculateDiffFriendRequests(mRequests, newRequests);
        mRequests = newRequests;
        result.dispatchUpdatesTo(this);
    }

    public void deleteItem(int position) {
        FriendRequest request = mRequests.get(position);
        Request.RequestState previousState = Objects.requireNonNull(request).getState();
        mRequests.remove(position);
        notifyItemRemoved(position);
        listener.onItemDeleted(request);
        Snackbar snackbar = Snackbar.make(parent, R.string.delete_snackbar_text, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo_snackbar_text, view -> onUndoDelete(request, position, previousState));
        snackbar.show();
    }

    private void onUndoDelete(FriendRequest request, int position, Request.RequestState previousState) {
        listener.onUndo(request, position, previousState);
        mRequests.add(position, request);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public FriendRequestListAdapter.FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                               int viewType) {
        this.parent = parent;
        ItemFriendRequestBinding binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new FriendRequestListAdapter.FriendRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendRequest request = mRequests.get(position);

        String currentUserId = new AuthRepository().getCurrentUser().getUid();

        if (request != null) {
            if (request.getReceiverId().equals(currentUserId)) {
                setReceivedRequests(holder, request);
            }
            if (request.getSenderId().equals(currentUserId)) {
                setSentRequests(holder, request);
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void setSentRequests(FriendRequestViewHolder holder, FriendRequest request) {
        String displayName = getDisplayName(request.getReceiverId());
        String userName = getUserName(request.getReceiverId());

        String profileImageString = getProfileImageString(request.getReceiverId());
        if (profileImageString != null && !profileImageString.isEmpty()) {
            Bitmap img = Utils.convertBaseStringToBitmap(profileImageString);
            holder.getIvProfilePicture().setImageBitmap(img);
        }

        String date = request.getCreationTimeAgo();

        String requestDescription = "";

        Request.RequestState requestState = request.getState();

        Resources resources = parent.getResources();

        if (requestState == Request.RequestState.REQUEST_PENDING) {
            requestDescription = resources.getString(R.string.friend_request_own_pending);
        } else if (requestState == Request.RequestState.REQUEST_ACCEPTED) {
            requestDescription = resources.getString(R.string.friend_request_own_accepted);
        } else if (requestState == Request.RequestState.REQUEST_ANSWERED) {
            requestDescription = resources.getString(R.string.friend_request_own_answered);
        } else if (requestState == Request.RequestState.REQUEST_DECLINED) {
            requestDescription = resources.getString(R.string.friend_request_own_declined);
        }

        if (displayName != null) {
            holder.getTvSender().setText("An: " + displayName);
        }

        if (userName != null) {
            holder.getTvSenderUsername().setText(userName);
        }

        holder.getTvDescription().setText(requestDescription);
        holder.getTvSentDate().setText(date);
        holder.getBtnAccept().setVisibility(View.GONE);
        holder.getBtnDecline().setVisibility(View.GONE);
    }

    private String getDisplayName(String userId) {
        if (users == null) {
            return null;
        }

        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user.getDisplayName();
            }
        }

        return null;
    }

    private String getUserName(String userId) {
        if (users == null) {
            return null;
        }

        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user.getUsername();
            }
        }
        return null;
    }

    private void setReceivedRequests(FriendRequestViewHolder holder, FriendRequest request) {
        String displayName = getDisplayName(request.getSenderId());
        String userName = getUserName(request.getSenderId());

        String profileImageString = getProfileImageString(request.getSenderId());
        if (profileImageString != null && !profileImageString.isEmpty()) {
            Bitmap img = Utils.convertBaseStringToBitmap(profileImageString);
            holder.getIvProfilePicture().setImageBitmap(img);
        }

        String date = request.getCreationTimeAgo();
        boolean isAccepted = request.getState() == Request.RequestState.REQUEST_ACCEPTED;

        if (userName != null) {
            holder.getTvSenderUsername().setText(userName);
        }

        if (displayName != null) {
            holder.getTvSender().setText(displayName);
        }
        holder.getTvSentDate().setText(date);
        holder.getTvDescription().setText(isAccepted ? R.string.friend_accepted_text : R.string.friend_request_text);
        holder.getBtnAccept().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
        holder.getBtnDecline().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
    }

    private String getProfileImageString(String senderId) {
        if (users == null) {
            return null;
        }

        for (User user : users) {
            if (user.getId().equals(senderId)) {
                return user.getProfileImageString();
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (mRequests == null) {
            return 0;
        }
        return mRequests.size();
    }

    /**
     * Fix for the bug in the RecyclerView that caused it to show incorrect data
     * (e.g. image)
     * Source:
     * https://www.solutionspirit.com/on-scrolling-recyclerview-change-values/
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnFriendRequestListener {
        void onItemClicked(String id);

        void onItemDeleted(FriendRequest request);

        void onAccept(FriendRequest request);

        void onDecline(FriendRequest request);

        void onUndo(FriendRequest request, int position, Request.RequestState previousState);
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
            if (position == RecyclerView.NO_POSITION || listener == null)
                return;

            String currentUserId = new AuthRepository().getCurrentUser().getUid();
            FriendRequest request = mRequests.get(position);
            if (request.getReceiverId().equals(currentUserId)) {
                listener.onItemClicked(mRequests.get(position).getSenderId());
            }
            if (request.getSenderId().equals(currentUserId)) {
                listener.onItemClicked(mRequests.get(position).getReceiverId());
            }
        }

        private void onAccept() {
            FriendRequest request = mRequests.get(getAdapterPosition());
            listener.onAccept(request);
            notifyItemChanged(getAdapterPosition());
        }

        private void onUndo(FriendRequest request, int position, Request.RequestState previousState) {
            listener.onUndo(request, position, previousState);
            notifyItemInserted(position);
        }

        private void onDecline() {
            int position = getAdapterPosition();
            FriendRequest request = mRequests.get(position);
            Request.RequestState previousState = Objects.requireNonNull(request).getState();
            listener.onDecline(request);
            notifyItemRemoved(position);
            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.decline_snackbar_text, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo_snackbar_text, view -> onUndo(request, position, previousState));
            snackbar.show();
        }

        /**
         * Getters for the views in the list item
         */

        public ImageView getIvProfilePicture() {
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

        public ImageView getBtnAccept() {
            return binding.acceptButton;
        }

        public ImageView getBtnDecline() {
            return binding.declineButton;
        }
    }
}

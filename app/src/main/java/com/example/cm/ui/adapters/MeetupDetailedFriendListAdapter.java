package com.example.cm.ui.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.Availability;
import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemSingleFriendBinding;
import com.example.cm.utils.Utils;

import java.util.List;
import java.util.Objects;

public class MeetupDetailedFriendListAdapter
        extends RecyclerView.Adapter<MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder> {

    private final List<User> friends;
    private final List<String> lateFriends;
    private final User currentUser;
    private final OnItemClickListener listener;

    public MeetupDetailedFriendListAdapter(List<User> friends, List<String> lateFriends, User currentUser, OnItemClickListener listener) {
        this.friends = friends;
        this.lateFriends = lateFriends;
        this.currentUser = currentUser;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSingleFriendBinding binding = ItemSingleFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetupDetailedFriendsListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder holder,
                                 int position) {
        if (friends != null) {
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            User friend = friends.get(position);

            String fullName = Objects.requireNonNull(friend).getFullName();
            String username = friend.getUsername();
            String profileImageString = friend.getProfileImageString();
            Availability availability = friend.getAvailability();

            if (profileImageString != null && !profileImageString.isEmpty()) {
                Bitmap img = Utils.convertBaseStringToBitmap(profileImageString);
                holder.getProfileImage().setImageBitmap(img);
            }

            if (lateFriends != null && lateFriends.contains(friend.getId())) {
                holder.getIvLate().setVisibility(View.VISIBLE);
            }
            holder.getTvFullName().setText(fullName);
            holder.getTvUserName().setText(username);

            boolean isFriendOfCurrentUser = currentUser.getFriends().contains(friend.getId());
            boolean isCurrentUser = currentUser.getId().equals(currentUser.getId());

            if (isFriendOfCurrentUser || isCurrentUser) {
                if (availability != null) {
                    switch (availability) {
                        case AVAILABLE:
                            holder.getAvailabilityDot().setImageResource(R.drawable.ic_dot_available);
                            break;
                        case SOON_AVAILABLE:
                            holder.getAvailabilityDot().setImageResource(R.drawable.ic_dot_soon_available);
                            break;
                        case UNAVAILABLE:
                            holder.getAvailabilityDot().setImageResource(R.drawable.ic_dot_unavailable);
                            break;
                    }
                }
            } else {
                holder.getAvailabilityDot().setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (friends == null) {
            return 0;
        }
        return friends.size();
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

    public interface OnItemClickListener {
        void onItemClicked(String id);
    }

    public class MeetupDetailedFriendsListViewHolder extends RecyclerView.ViewHolder {

        private final ItemSingleFriendBinding binding;

        public MeetupDetailedFriendsListViewHolder(ItemSingleFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        /**
         * Listeners for the views in the list item
         */
        private void setListeners() {
            binding.getRoot().setOnClickListener(v -> onItemClicked());
        }

        private void onItemClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null)
                return;
            listener.onItemClicked(friends.get(position).getId());
        }

        public ImageView getProfileImage() {
            return binding.ivUserImage;
        }

        public TextView getTvUserName() {
            return binding.tvUsername;
        }

        public TextView getTvFullName() {
            return binding.tvName;
        }

        public ImageView getIvLate() {
            return binding.ivFriendLateInfo;
        }

        public ImageView getAvailabilityDot() {
            return binding.dotAvailabilityIcon;
        }
    }
}
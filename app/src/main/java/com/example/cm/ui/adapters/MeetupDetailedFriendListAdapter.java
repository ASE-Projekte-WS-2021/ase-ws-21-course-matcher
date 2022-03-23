package com.example.cm.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemSingleFriendBinding;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class MeetupDetailedFriendListAdapter
        extends RecyclerView.Adapter<MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder> {

    private final List<User> friends;
    private final List<String> lateFriends;
    private final OnItemClickListener listener;

    public MeetupDetailedFriendListAdapter(List<User> friends, List<String> lateFriends, OnItemClickListener listener) {
        this.friends = friends;
        this.lateFriends = lateFriends;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSingleFriendBinding binding = ItemSingleFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.dotAvailabilityIcon.setVisibility(View.INVISIBLE);
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
            String profileImageUrl = friend.getProfileImageUrl();

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                holder.getProfileImage().setImageTintMode(null);
                Picasso.get().load(profileImageUrl).fit().centerCrop().into(holder.getProfileImage());
            }

            if (lateFriends != null && lateFriends.contains(friend.getId())) {
                holder.getIvLate().setVisibility(View.VISIBLE);
            }
            holder.getTvFullName().setText(fullName);
            holder.getTvUserName().setText(username);

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
    }
}
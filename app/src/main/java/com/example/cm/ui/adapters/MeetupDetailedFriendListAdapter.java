package com.example.cm.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemSingleFriendBinding;

import java.util.List;

public class MeetupDetailedFriendListAdapter extends RecyclerView.Adapter<MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder> {
    private final List<User> friends;

    public MeetupDetailedFriendListAdapter(List<User> friends) {
        this.friends = friends;
    }

    @NonNull
    @Override
    public MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSingleFriendBinding binding = ItemSingleFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetupDetailedFriendsListViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder holder, int position) {
        if (friends != null) {
            String fullName = friends.get(position).getFullName();
            String username = friends.get(position).getUsername();
            TextView tvUserName = holder.getTvUserName();
            TextView tvFullName = holder.getTvFullName();

            tvFullName.setText(fullName);
            tvUserName.setText(username);
        }
    }


    @Override
    public int getItemCount() {
        if (friends == null) {
            return 0;
        }
        return friends.size();
    }

    public static class MeetupDetailedFriendsListViewHolder extends RecyclerView.ViewHolder {

        private final ItemSingleFriendBinding binding;

        public MeetupDetailedFriendsListViewHolder(ItemSingleFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TextView getTvUserName() {
            return binding.tvUsername;
        }

        public TextView getTvFullName() {
            return binding.tvName;
        }
    }
}

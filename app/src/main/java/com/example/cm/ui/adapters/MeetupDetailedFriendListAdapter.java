package com.example.cm.ui.adapters;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.ItemMeetupBinding;
import com.example.cm.databinding.ItemMeetupFriendBinding;
import com.example.cm.ui.meetup.MeetupDetailed.MeetupFriendsListState;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import org.w3c.dom.Text;

import java.util.List;

public class MeetupDetailedFriendListAdapter extends RecyclerView.Adapter<MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder> {
    private List<String> friends;
    private MeetupFriendsListState status;

    public MeetupDetailedFriendListAdapter(List<String> friends, MeetupFriendsListState status) {
        this.friends = friends;
        this.status = status;
    }

    @NonNull
    @Override
    public MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMeetupFriendBinding binding = ItemMeetupFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetupDetailedFriendsListViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull MeetupDetailedFriendListAdapter.MeetupDetailedFriendsListViewHolder holder, int position) {
        if (friends != null){
            String id = friends.get(position);
            TextView tvUserName = holder.getTvUserName();
            tvUserName.setText(id);
        }
    }


    @Override
    public int getItemCount() {
        if(friends == null) {
            return 0;
        }
        return friends.size();
    }

    public static class MeetupDetailedFriendsListViewHolder extends RecyclerView.ViewHolder {

        private final ItemMeetupFriendBinding binding;

        public MeetupDetailedFriendsListViewHolder(ItemMeetupFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TextView getTvUserName() {
            return binding.tvMeetupFriendName;
        }
    }
}

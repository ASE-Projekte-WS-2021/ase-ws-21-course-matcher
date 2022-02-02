package com.example.cm.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.databinding.ItemMeetupBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MeetupListAdapter extends RecyclerView.Adapter<MeetupListAdapter.MeetupListViewHolder> {
    List<Meetup> meetups;

    public MeetupListAdapter(List<Meetup> meetups) {
        this.meetups = meetups;
    }

    @NonNull
    @Override
    public MeetupListAdapter.MeetupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("onCreateViewHolder");
        ItemMeetupBinding binding = ItemMeetupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetupListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetupListAdapter.MeetupListViewHolder holder, int position) {
        Meetup meetup = meetups.get(position);

        holder.getTvLocation().setText("Treffen in: " + meetup.getLocation());
        holder.getTvTime().setText("Um " + meetup.getTime());

        // replace this ugly code with for example an expandable listview
        List<String> confirmedFriends = meetup.getConfirmedFriends();
        List<String> invitedFriends = meetup.getInvitedFriends();
        List<String> declinedFriends = meetup.getDeclinedFriends();

        //replace this ugly code also
        setFriendsTextFields(confirmedFriends, holder.getTvConfirmedFriends(), holder.getTvConfirmedFriendsTitle());
        setFriendsTextFields(invitedFriends, holder.getTvInvitedFriends(), holder.getTvInvitedFriendsTitle());
        setFriendsTextFields(declinedFriends, holder.getTvDeclinedFriends(), holder.getTvDeclinedFriendsTitle());
    }

    public void setMeetups(List<Meetup> meetups) {
        this.meetups = meetups;
    }

    //replace this ugly code also :D
    private void setFriendsTextFields(List<String> list, TextView textView, TextView textViewTitle) {
        FirebaseFirestore.getInstance().collection(CollectionConfig.USERS.toString());
        if (list == null || list.isEmpty()) {
            textView.setVisibility(View.GONE);
            textViewTitle.setVisibility(View.GONE);
        } else {
            new UserRepository().getUserByIdMeetup(list, textView);
        }
    }

    @Override
    public int getItemCount() {
        return meetups.size();
    }

    public static class MeetupListViewHolder extends RecyclerView.ViewHolder {

        private final ItemMeetupBinding binding;

        public MeetupListViewHolder(ItemMeetupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TextView getTvLocation() {
            return binding.locationText;
        }

        public TextView getTvTime() {
            return binding.timeText;
        }

        public TextView getTvConfirmedFriends() {
            return binding.confirmedFriendsText;
        }

        public TextView getTvConfirmedFriendsTitle() {
            return binding.confirmedTextTitle;
        }

        public TextView getTvInvitedFriends() {
            return binding.invitedFriendsText;
        }

        public TextView getTvInvitedFriendsTitle() {
            return binding.invitedTextTitle;
        }

        public TextView getTvDeclinedFriends() {
            return binding.declinedFriendsText;
        }

        public TextView getTvDeclinedFriendsTitle() {
            return binding.declinedTextTitle;
        }
    }
}

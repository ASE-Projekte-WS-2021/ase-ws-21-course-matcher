package com.example.cm.ui.adapters;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.ItemMeetupBinding;
import com.google.android.material.imageview.ShapeableImageView;

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

        holder.getTvLocation().setText(meetup.getLocation());
        holder.getTvTime().setText(meetup.getTime());

        List<String> confirmedFriends = meetup.getConfirmedFriends();
        List<String> invitedFriends = meetup.getInvitedFriends();
        List<String> declinedFriends = meetup.getDeclinedFriends();

        LinearLayout imagesLayout = holder.getImagesLayout();
        imagesLayout.setPadding(-3, 0, 0,0);

        addUserImage(confirmedFriends, imagesLayout, R.color.green);
        addUserImage(invitedFriends, imagesLayout, R.color.orange);
        addUserImage(declinedFriends, imagesLayout, R.color.red);
    }

    public void addUserImage(List<String> friendIds, LinearLayout layout, int color){
        if (friendIds != null){
            for(String id: friendIds){
                //toDo: add profile image instead of "R.drawable.ic_baseline_person_24"
                ShapeableImageView imageRounded = new ShapeableImageView(new ContextThemeWrapper(layout.getContext(), R.style.ShapeAppearance_App_CircleImageView));
                imageRounded.setBackgroundResource(R.drawable.ic_baseline_person_24);
                imageRounded.setLayoutParams(new ViewGroup.LayoutParams(80, 80));
                imageRounded.setStrokeColorResource(color);
                imageRounded.setStrokeWidth(4);
                imageRounded.setPadding(5, 5, 5, 5);
                layout.addView(imageRounded);
            }
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

        public LinearLayout getImagesLayout() {
            return binding.meetupImagesLayout;
        }

    }
}

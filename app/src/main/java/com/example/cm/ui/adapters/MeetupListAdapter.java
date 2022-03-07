package com.example.cm.ui.adapters;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.ItemMeetupBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Objects;

public class MeetupListAdapter extends RecyclerView.Adapter<MeetupListAdapter.MeetupListViewHolder> {
    List<MutableLiveData<Meetup>> meetups;

    public MeetupListAdapter(List<MutableLiveData<Meetup>> meetups) {
        this.meetups = meetups;
    }

    @NonNull
    @Override
    public MeetupListAdapter.MeetupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMeetupBinding binding = ItemMeetupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetupListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetupListAdapter.MeetupListViewHolder holder, int position) {
        Meetup meetup = meetups.get(position).getValue();

        MaterialCardView meetupCard = holder.getMeetupCard();

        meetupCard.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_MEETUP_ID, Objects.requireNonNull(meetup).getId());
            Navigation.findNavController(view).navigate(R.id.navigateToMeetupDetailed, bundle);
        });

        holder.getTvLocation().setText(Objects.requireNonNull(meetup).getLocation());
        switch (meetup.getPhase()) {
            case MEETUP_UPCOMING:
                holder.getTvTime().setText(meetup.getFormattedTime());
                break;
            case MEETUP_ACTIVE:
                holder.getTvTime().setText(meetupCard.getContext().getString(R.string.meetup_active_text, meetup.getFormattedTime()));
                break;
        }

        List<String> confirmedFriends = meetup.getConfirmedFriends();
        List<String> invitedFriends = meetup.getInvitedFriends();
        List<String> declinedFriends = meetup.getDeclinedFriends();

        LinearLayout imagesLayout = holder.getImagesLayout();
        imagesLayout.setPadding(-3, 0, 0, 0);

        addUserImage(confirmedFriends, imagesLayout, R.color.green);
        addUserImage(invitedFriends, imagesLayout, R.color.orange);
        addUserImage(declinedFriends, imagesLayout, R.color.red);
    }

    public void addUserImage(List<String> friendIds, LinearLayout layout, int color) {
        if (friendIds != null) {
            for (String id : friendIds) {
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
        if (meetups == null) {
            return 0;
        }
        return meetups.size();
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

        public MaterialCardView getMeetupCard() {
            return binding.card;
        }
    }
}

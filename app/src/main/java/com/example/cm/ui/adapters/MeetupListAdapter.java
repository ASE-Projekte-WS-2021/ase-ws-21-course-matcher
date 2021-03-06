package com.example.cm.ui.adapters;

import static com.example.cm.Constants.MEETUP_DETAILED_MAX_USER;
import static com.example.cm.Constants.MEETUP_DETAILED_USER_IMAGE_PADDING;
import static com.example.cm.Constants.MEETUP_DETAILED_USER_IMAGE_SIZE;
import static com.example.cm.Constants.MEETUP_DETAILED_USER_IMAGE_STROKE;
import static com.example.cm.Constants.OFFSET_BOTTOM;
import static com.example.cm.Constants.OFFSET_LEFT;
import static com.example.cm.Constants.OFFSET_RIGHT;
import static com.example.cm.Constants.OFFSET_TOP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.databinding.ItemMeetupBinding;
import com.example.cm.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Objects;

public class MeetupListAdapter extends RecyclerView.Adapter<MeetupListAdapter.MeetupListViewHolder> {
    private List<Meetup> meetups;
    private List<User> users;
    private int userCounter = 0;

    public MeetupListAdapter(List<Meetup> meetups, List<User> users) {
        this.meetups = meetups;
        this.users = users;
    }

    @NonNull
    @Override
    public MeetupListAdapter.MeetupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMeetupBinding binding = ItemMeetupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetupListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetupListAdapter.MeetupListViewHolder holder, int position) {
        Meetup meetup = meetups.get(position);

        if (meetup == null) {
            return;
        }

        initCard(holder, meetup);
        initLocationImg(holder, meetup);
        initTextViews(holder, meetup);
        initUserIcons(holder.getImagesLayout(), meetup);
    }

    private void initCard(MeetupListAdapter.MeetupListViewHolder holder, Meetup meetup) {
        MaterialCardView meetupCard = holder.getMeetupCard();
        if (meetup.getRequestingUser().equals(new AuthRepository().getCurrentUser().getUid())) {
            holder.getOwnMeetupMarker().setVisibility(View.VISIBLE);
        }
        meetupCard.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_MEETUP_ID, Objects.requireNonNull(meetup).getId());
            Navigation.findNavController(view).navigate(R.id.navigateToMeetupDetailed, bundle);
        });
    }

    private void initLocationImg(MeetupListAdapter.MeetupListViewHolder holder, Meetup meetup) {
        String locationImageString = meetup.getLocationImageString();
        if (locationImageString != null && !locationImageString.isEmpty()) {
            Bitmap img = Utils.convertBaseStringToBitmap(meetup.getLocationImageString());
            holder.getIvLocation().setImageBitmap(img);
        }
    }

    private void initTextViews(MeetupListAdapter.MeetupListViewHolder holder, Meetup meetup) {
        Context context = holder.getContext();
        String address = meetup.getLocationName();
        holder.getTvLocation().setText(address);

        switch (Utils.getPhaseByTimestamp(meetup.getTimestamp())) {
            case MEETUP_UPCOMING:
                holder.getTvTime().setText(meetup.getFormattedTime());
                holder.getTvTime().setTextColor(context.getResources().getColor(R.color.orange400));
                holder.getTvTime()
                        .setBackground(AppCompatResources.getDrawable(context, R.drawable.label_rounded_upcoming));
                break;
            case MEETUP_ACTIVE:
                holder.getTvTime()
                        .setText(holder.getContext().getString(R.string.meetup_active_text, meetup.getFormattedTime()));
                holder.getTvTime().setTextColor(context.getResources().getColor(R.color.orange600));
                holder.getTvTime()
                        .setBackground(AppCompatResources.getDrawable(context, R.drawable.label_rounded_active));
                break;
            case MEETUP_ENDED:
                holder.getTvTime().setText(R.string.meetup_ended_text);
                holder.getTvTime().setTextColor(context.getResources().getColor(R.color.gray500));
                holder.getTvTime()
                        .setBackground(AppCompatResources.getDrawable(context, R.drawable.label_rounded_ended));
                break;
        }
    }

    private void initUserIcons(LinearLayout imgLayout, Meetup meetup) {
        List<String> confirmedFriends = meetup.getConfirmedFriends();
        List<String> invitedFriends = meetup.getInvitedFriends();
        List<String> declinedFriends = meetup.getDeclinedFriends();

        imgLayout.setPadding(OFFSET_LEFT, OFFSET_TOP, OFFSET_RIGHT, OFFSET_BOTTOM);

        addUserImage(confirmedFriends, imgLayout, R.color.green);
        addUserImage(invitedFriends, imgLayout, R.color.orange);
        addUserImage(declinedFriends, imgLayout, R.color.red);

        addRestUserCounter(imgLayout);
        userCounter = 0;
    }

    @SuppressLint("SetTextI18n")
    private void addRestUserCounter(LinearLayout imgLayout) {
        int rest = userCounter - MEETUP_DETAILED_MAX_USER;

        if (rest > 0) {
            TextView restOfUserTv = new TextView(imgLayout.getContext());
            restOfUserTv.setText(" +" + rest);
            imgLayout.addView(restOfUserTv);
        }
    }

    public void addUserImage(List<String> friendIds, LinearLayout layout, int color) {
        if (friendIds == null) {
            return;
        }

        for (String id : friendIds) {
            userCounter++;
            if (userCounter <= MEETUP_DETAILED_MAX_USER) {
                ShapeableImageView imageRounded = new ShapeableImageView(
                        new ContextThemeWrapper(layout.getContext(), R.style.ShapeAppearance_App_CircleImageView));

                String imageUrl = getImageUrl(id);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Bitmap img = Utils.convertBaseStringToBitmap(imageUrl);
                    imageRounded.setImageBitmap(img);
                } else {
                    imageRounded.setBackgroundResource(R.drawable.ic_baseline_person_24);
                }

                imageRounded.setLayoutParams(
                        new ViewGroup.LayoutParams(MEETUP_DETAILED_USER_IMAGE_SIZE,
                                MEETUP_DETAILED_USER_IMAGE_SIZE));
                imageRounded.setStrokeColorResource(color);
                imageRounded.setStrokeWidth(MEETUP_DETAILED_USER_IMAGE_STROKE);
                imageRounded.setPadding(MEETUP_DETAILED_USER_IMAGE_PADDING, MEETUP_DETAILED_USER_IMAGE_PADDING,
                        MEETUP_DETAILED_USER_IMAGE_PADDING, MEETUP_DETAILED_USER_IMAGE_PADDING);
                imageRounded.setScaleType(ImageView.ScaleType.CENTER_CROP);
                layout.addView(imageRounded);
            }
        }
    }

    private String getImageUrl(String id) {
        if (users == null) {
            return null;
        }

        for (User user : users) {
            if (user.getId().equals(id)) {
                return user.getProfileImageString();
            }
        }

        return null;
    }

    @Override
    public int getItemCount() {
        if (meetups == null) {
            return 0;
        }
        return meetups.size();
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

    public static class MeetupListViewHolder extends RecyclerView.ViewHolder {

        private final ItemMeetupBinding binding;

        public MeetupListViewHolder(ItemMeetupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ImageView getIvLocation() {
            return binding.ivLocation;
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
            return binding.meetupCard;
        }

        public FrameLayout getOwnMeetupMarker() {
            return binding.ownMeetupMarker;
        }

        public Context getContext() {
            return binding.getRoot().getContext();
        }
    }
}
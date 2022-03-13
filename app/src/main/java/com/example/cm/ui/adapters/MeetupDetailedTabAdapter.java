package com.example.cm.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cm.data.models.Meetup;
import com.example.cm.ui.meetup.MeetupDetailed.MeetupDetailedFriendsListFragment;

public class MeetupDetailedTabAdapter extends FragmentStateAdapter {

    public static final int TAB_COUNT = 3;

    private final Meetup meetup;

    public MeetupDetailedTabAdapter(Fragment fragment, Meetup meetup) {
        super(fragment);
        this.meetup = meetup;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new MeetupDetailedFriendsListFragment(meetup.getConfirmedFriends(), meetup.getId());
        } else if (position == 1) {
            return new MeetupDetailedFriendsListFragment(meetup.getDeclinedFriends(), meetup.getId());
        } else {
            return new MeetupDetailedFriendsListFragment(meetup.getInvitedFriends(), meetup.getId());
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
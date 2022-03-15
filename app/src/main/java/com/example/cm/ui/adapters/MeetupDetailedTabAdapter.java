package com.example.cm.ui.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cm.data.models.Meetup;
import com.example.cm.ui.invite_friends.InviteFriendsFragment;
import com.example.cm.ui.invite_friends.InviteMoreFriendsFragment;
import com.example.cm.ui.meetup.MeetupDetailed.MeetupDetailedFriendsListFragment;

import java.util.ArrayList;
import java.util.List;

public class MeetupDetailedTabAdapter extends FragmentStateAdapter {

    public static final int TAB_COUNT = 4;
    private List<String> usersAlreadyInMeetup = new ArrayList<>();
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
        } else if (position == 2) {
            return new MeetupDetailedFriendsListFragment(meetup.getInvitedFriends(), meetup.getId());
        } else {
            if (meetup.getInvitedFriends() != null) {
                usersAlreadyInMeetup.addAll(meetup.getInvitedFriends());
            }
            if (meetup.getConfirmedFriends() != null) {
                usersAlreadyInMeetup.addAll(meetup.getConfirmedFriends());
            }
            if (meetup.getDeclinedFriends() != null) {
                usersAlreadyInMeetup.addAll(meetup.getDeclinedFriends());
            }
            return new InviteMoreFriendsFragment(meetup.getId(), usersAlreadyInMeetup);
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
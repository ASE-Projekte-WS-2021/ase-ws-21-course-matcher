package com.example.cm.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cm.data.models.Meetup;
import com.example.cm.ui.meetup.MeetupDetailed.MeetupDetailedFriendsListFragment;
import com.example.cm.ui.meetup.MeetupDetailed.MeetupFriendsListState;

public class MeetupDetailedTabAdapter extends FragmentStateAdapter {

    private final Meetup meetup;

    public MeetupDetailedTabAdapter(Fragment fragment, Meetup meetup) {
        super(fragment);
        this.meetup = meetup;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new MeetupDetailedFriendsListFragment(meetup.getConfirmedFriends(), MeetupFriendsListState.ACCEPTED);
        }else if(position == 1){
            return new MeetupDetailedFriendsListFragment(meetup.getDeclinedFriends(), MeetupFriendsListState.DECLINED);
        }else{
            return new MeetupDetailedFriendsListFragment(meetup.getInvitedFriends(), MeetupFriendsListState.PENDING);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

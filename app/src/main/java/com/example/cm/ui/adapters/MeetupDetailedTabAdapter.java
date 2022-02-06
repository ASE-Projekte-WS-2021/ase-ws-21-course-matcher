package com.example.cm.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cm.ui.meetup.MeetupDetailed.MeetupDetailedFriendsListFragment;

public class MeetupDetailedTabAdapter extends FragmentStateAdapter {

    public MeetupDetailedTabAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new MeetupDetailedFriendsListFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

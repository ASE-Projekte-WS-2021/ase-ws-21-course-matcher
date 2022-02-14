package com.example.cm.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cm.ui.meetup.MeetupList.MeetupListFragment;
import com.example.cm.ui.meetup.MeetupRequests.MeetupRequestsFragment;

public class MeetupTabAdapter extends FragmentStateAdapter {

    public MeetupTabAdapter(Fragment fragment) {
        super(fragment);
    }
    public Fragment[] tabs = new Fragment[]{new MeetupListFragment(), new MeetupRequestsFragment()};

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return tabs[position];
    }

    @Override
    public int getItemCount() {
        return tabs.length;
    }
}
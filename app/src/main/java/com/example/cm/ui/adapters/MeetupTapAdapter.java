package com.example.cm.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cm.ui.dashboard.DashboardFragment;
import com.example.cm.ui.meetup.MeetupListFragment;
import com.example.cm.ui.meetup.MeetupNotificationsFragment;

public class MeetupTapAdapter extends FragmentStateAdapter {

    public MeetupTapAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new MeetupListFragment(): new MeetupNotificationsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
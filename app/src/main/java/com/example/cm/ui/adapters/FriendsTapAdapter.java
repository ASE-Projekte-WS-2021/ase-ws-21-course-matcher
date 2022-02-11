package com.example.cm.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cm.ui.friends.FriendRequests.FriendRequestsFragment;
import com.example.cm.ui.friends.FriendsListFragment;

public class FriendsTapAdapter extends FragmentStateAdapter {

    public FriendsTapAdapter(Fragment fragment) {
        super(fragment);
    }
    public Fragment[] tabs = new Fragment[]{new FriendsListFragment(), new FriendRequestsFragment()};

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
package com.example.cm.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cm.R;
import com.example.cm.databinding.FragmentFriendsTabsBinding;
import com.example.cm.ui.adapters.FriendsTapAdapter;
import com.example.cm.utils.Navigator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FriendsTabsFragment extends Fragment {

    private FriendsTapAdapter friendsTabAdapter;
    private ViewPager2 viewPager;
    private FragmentFriendsTabsBinding binding;
    private Navigator navigator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFriendsTabsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());

        binding.addFriendsFab.setOnClickListener(view -> {
            navigator.navigateToSelectFriends(); //todo: navigate to add friends ???
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        friendsTabAdapter = new FriendsTapAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(friendsTabAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.friends_tabs_list);
            } else if (position == 1) {
                tab.setText(R.string.friends_tabs_requests);
            }
        }).attach();
    }

}

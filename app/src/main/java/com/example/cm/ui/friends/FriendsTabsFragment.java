package com.example.cm.ui.friends;

import static com.example.cm.data.models.Request.RequestState.REQUEST_PENDING;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cm.R;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.databinding.FragmentFriendsTabsBinding;
import com.example.cm.ui.adapters.FriendsTapAdapter;
import com.example.cm.ui.friends.FriendRequests.FriendRequestsViewModel;
import com.example.cm.utils.Navigator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class FriendsTabsFragment extends Fragment {
    private FriendsTapAdapter friendsTabAdapter;
    private FriendRequestsViewModel friendRequestsViewModel;
    private ViewPager2 viewPager;
    private FragmentFriendsTabsBinding binding;
    private Navigator navigator;
    private int openRequests = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsTabsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());

        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        binding.addFriendsFab.setOnClickListener(view -> {
            navigator.navigateToSelectFriends();
        });
    }

    private void initViewModel() {
        friendRequestsViewModel = new ViewModelProvider(requireActivity()).get(FriendRequestsViewModel.class);
        friendRequestsViewModel.getFriendRequests().observe(getViewLifecycleOwner(), friendRequests -> {
            if (friendRequests == null) {
                return;
            }
            countOpenRequests(friendRequests);
            hideShowBadge();
        });
    }

    private void countOpenRequests(List<MutableLiveData<FriendRequest>> friendRequests) {
        // Reset the open requests counter
        openRequests = 0;

        for (int i = 0; i < friendRequests.size(); i++) {
            FriendRequest friendRequest = friendRequests.get(i).getValue();
            if (friendRequest == null) {
                continue;
            }
            if (friendRequest.getState() == REQUEST_PENDING) {
                openRequests++;
            }
        }
    }

    private void hideShowBadge() {
        TabLayout.Tab tab = binding.tabLayout.getTabAt(1);
        if (tab == null) {
            return;
        }
        if (openRequests > 0) {
            tab.getOrCreateBadge().setNumber(openRequests);
            tab.getOrCreateBadge().setBackgroundColor(getResources().getColor(R.color.orange500));
            tab.getOrCreateBadge().setBadgeTextColor(getResources().getColor(R.color.white));
            tab.getOrCreateBadge().setVisible(true);
        } else {
            tab.getOrCreateBadge().setVisible(false);
        }
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

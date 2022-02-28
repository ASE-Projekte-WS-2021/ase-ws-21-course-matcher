package com.example.cm.ui.meetup.MeetupDetailed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupDetailedFriendsListBinding;
import com.example.cm.ui.adapters.MeetupDetailedFriendListAdapter;
import com.example.cm.utils.Navigator;

import java.util.List;

public class MeetupDetailedFriendsListFragment extends Fragment implements MeetupDetailedFriendListAdapter.OnItemClickListener {

    private final List<String> friends;
    private FragmentMeetupDetailedFriendsListBinding binding;
    private MeetupDetailedFriendsListViewModel meetupDetailedFriendsListViewModel;
    private Navigator navigator;

    public MeetupDetailedFriendsListFragment(List<String> friends) {
        this.friends = friends;
    }

    private void initUI() {
        binding.meetupDetailedFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.meetupDetailedFriendsList.setHasFixedSize(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMeetupDetailedFriendsListBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());

        initUI();
        initViewModel();

        return binding.getRoot();
    }

    private void initViewModel() {
        meetupDetailedFriendsListViewModel = new ViewModelProvider(this, new MeetupDetailedFriendsListFactory(friends)).get(MeetupDetailedFriendsListViewModel.class);
        meetupDetailedFriendsListViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            MeetupDetailedFriendListAdapter adapter = new MeetupDetailedFriendListAdapter(users, this);
            binding.meetupDetailedFriendsList.setAdapter(adapter);
        });
    }

    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);
        if (meetupDetailedFriendsListViewModel.isOwnUserId(id)) {
            navigator.getNavController().navigate(R.id.fromMeetupDetailedToOwnProfile, bundle);
        } else {
            navigator.getNavController().navigate(R.id.fromMeetupDetailedToOtherProfile, bundle);
        }
    }
}
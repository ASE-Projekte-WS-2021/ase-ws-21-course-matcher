package com.example.cm.ui.meetup.MeetupDetailed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupDetailedFriendsListBinding;
import com.example.cm.ui.adapters.MeetupDetailedFriendListAdapter;
import com.example.cm.utils.Navigator;

import java.util.List;
import java.util.Objects;

public class MeetupDetailedFriendsListFragment extends Fragment implements MeetupDetailedFriendListAdapter.OnItemClickListener {

    private final List<String> friends;
    private final String meetupId;
    private FragmentMeetupDetailedFriendsListBinding binding;
    private MeetupDetailedFriendsListViewModel meetupDetailedFriendsListViewModel;
    private Navigator navigator;
    private MeetupDetailedFriendListAdapter adapter;

    public MeetupDetailedFriendsListFragment(List<String> friends, String meetupId) {
        this.friends = friends;
        this.meetupId = meetupId;
    }

    private void initUI() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal)));
        binding.meetupDetailedFriendsList.addItemDecoration(dividerItemDecoration);
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
        meetupDetailedFriendsListViewModel = new ViewModelProvider(this, new MeetupDetailedFriendsListFactory(friends, meetupId)).get(MeetupDetailedFriendsListViewModel.class);
        meetupDetailedFriendsListViewModel.getUsers().observe(getViewLifecycleOwner(), users -> meetupDetailedFriendsListViewModel.getLateUsers().observe(getViewLifecycleOwner(), lateUsers -> meetupDetailedFriendsListViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            adapter = new MeetupDetailedFriendListAdapter(users, lateUsers, currentUser, this);
            binding.meetupDetailedFriendsList.setAdapter(adapter);
        })));
    }

    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);
        bundle.putBoolean(Constants.KEY_IS_OWN_USER, meetupDetailedFriendsListViewModel.isOwnUserId(id));
        navigator.getNavController().navigate(R.id.fromMeetupDetailedToOtherProfile, bundle);
    }
}
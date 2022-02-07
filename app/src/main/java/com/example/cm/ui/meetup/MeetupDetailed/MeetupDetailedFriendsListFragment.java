package com.example.cm.ui.meetup.MeetupDetailed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.databinding.FragmentMeetupDetailedFriendsListBinding;
import com.example.cm.ui.adapters.MeetupDetailedFriendListAdapter;

import java.util.List;

public class MeetupDetailedFriendsListFragment extends Fragment {

    private final List<String> friends;
    private FragmentMeetupDetailedFriendsListBinding binding;

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

        initUI();
        initViewModel();

        return binding.getRoot();
    }

    private void initViewModel() {
        MeetupDetailedFriendsListViewModel viewModel = new ViewModelProvider(this, new MeetupDetailedFriendsListFactory(friends)).get(MeetupDetailedFriendsListViewModel.class);
        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            MeetupDetailedFriendListAdapter adapter = new MeetupDetailedFriendListAdapter(users);
            binding.meetupDetailedFriendsList.setAdapter(adapter);
        });

    }
}
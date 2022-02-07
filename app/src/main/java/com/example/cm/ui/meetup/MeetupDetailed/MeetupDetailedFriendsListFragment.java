package com.example.cm.ui.meetup.MeetupDetailed;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupDetailedFriendsListBinding;
import com.example.cm.ui.adapters.MeetupDetailedFriendListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MeetupDetailedFriendsListFragment extends Fragment {

    private MeetupFriendsListState status;
    private List<String> friends;
    private FragmentMeetupDetailedFriendsListBinding binding;

    public MeetupDetailedFriendsListFragment(List<String> friends, MeetupFriendsListState status) {
        this.friends = friends;
        this.status = status;
    }

    private void initUI() {
        MeetupDetailedFriendListAdapter adapter = new MeetupDetailedFriendListAdapter(friends, status);
        binding.meetupDetailedFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.meetupDetailedFriendsList.setHasFixedSize(true);
        binding.meetupDetailedFriendsList.setAdapter(adapter);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMeetupDetailedFriendsListBinding.inflate(inflater, container, false);

        initUI();
        return binding.getRoot();
    }
}
package com.example.cm.ui.meetup.MeetupList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.FragmentMeetupListBinding;
import com.example.cm.ui.adapters.MeetupListAdapter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MeetupListFragment extends Fragment {

    private FragmentMeetupListBinding binding;
    private MeetupListAdapter meetupListAdapter;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupListBinding.inflate(inflater, container, false);

        initUi();
        initViewModel();

        return binding.getRoot();
    }

    private void initUi() {
        GridLayoutManager gridLayout = new GridLayoutManager(requireContext(), 2);
        binding.meetupListRecyclerView.setLayoutManager(gridLayout);
        binding.meetupListRecyclerView.setHasFixedSize(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initViewModel() {
        MeetupListViewModel meetupListViewModel = new ViewModelProvider(this).get(MeetupListViewModel.class);
        meetupListViewModel.getMeetups().observe(getViewLifecycleOwner(), meetups -> {
            if (meetups.isEmpty()) {
                binding.loadingCircle.setVisibility(View.GONE);
                binding.meetupListRecyclerView.setVisibility(View.GONE);
                binding.noMeetupsWrapper.setVisibility(View.VISIBLE);
                return;
            }

            List<String> userIds = getUserIds(meetups);
            meetupListViewModel.setUserIds(userIds);
            meetupListViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
                meetupListAdapter = new MeetupListAdapter(meetups, users);

                binding.meetupListRecyclerView.setAdapter(meetupListAdapter);
                binding.loadingCircle.setVisibility(View.GONE);
                binding.noMeetupsWrapper.setVisibility(View.GONE);
                binding.meetupListRecyclerView.setVisibility(View.VISIBLE);
            });
        });
    }

    private List<String> getUserIds(List<Meetup> meetups) {
        List<String> ids = new ArrayList<>();

        for (Meetup meetup : meetups) {
            if (meetup != null) {
                List<String> confirmedFriends = meetup.getConfirmedFriends();
                List<String> declinedFriends = meetup.getDeclinedFriends();
                List<String> invitedFriends = meetup.getInvitedFriends();

                if (confirmedFriends != null) {
                    ids.addAll(confirmedFriends);
                }

                if (declinedFriends != null) {
                    ids.addAll(declinedFriends);
                }

                if (invitedFriends != null) {
                    ids.addAll(invitedFriends);
                }
            }
        }
        return ids;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
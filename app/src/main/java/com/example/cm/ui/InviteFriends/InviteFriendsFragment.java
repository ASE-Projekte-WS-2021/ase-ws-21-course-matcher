package com.example.cm.ui.InviteFriends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.FragmentInviteFriendsBinding;

import com.example.cm.ui.SharedViewModel;

import java.util.ArrayList;
import java.util.Arrays;


public class InviteFriendsFragment extends Fragment {

    String requestingUser = ("CURRENTLY LOGGED IN USER");
    private SharedViewModel sharedViewModel;
    private FragmentInviteFriendsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInviteFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initUI();
        initViewModel();
        initListener();

        return root;

    }

    private void initUI() {
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }

    private void initListener() {
        binding.inviteFriendsBackBtn.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).navigate(R.id.navigationToInfoMeetup));

        binding.inviteFriendsSubmitBtn.setOnClickListener(v -> {

            String location = sharedViewModel.getMeetupLocation().getValue();
            String time = sharedViewModel.getMeetupTime().getValue();
            Boolean isPrivate = sharedViewModel.getMeetupIsPrivate().getValue();

            Meetup meetup = new Meetup(requestingUser, location, time, isPrivate, new ArrayList<>(Arrays.asList("Max", "Julia", "Tim")));
            sharedViewModel.createMeetup2(meetup);

            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_navigation_invite_friends_to_invitationSuccess);

        });
    }


    public void initViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}

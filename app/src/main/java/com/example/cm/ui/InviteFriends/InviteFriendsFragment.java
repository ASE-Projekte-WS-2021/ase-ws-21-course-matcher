package com.example.cm.ui.InviteFriends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.FragmentInviteFriendsBinding;

import com.example.cm.ui.CreateMeetupViewModel;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.ui.select_friends.SelectFriendsViewModel.OnNotificationSentListener;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;


public class InviteFriendsFragment extends Fragment implements AdapterView.OnItemClickListener, OnNotificationSentListener {

    String requestingUser = ("CURRENTLY LOGGED IN USER");

    private CreateMeetupViewModel createMeetupViewModel;
    private FragmentInviteFriendsBinding binding;
    private InviteFriendsListAdapter inviteFriendsListAdapter;


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
        createMeetupViewModel = new ViewModelProvider(this).get(CreateMeetupViewModel.class);
        inviteFriendsListAdapter = new InviteFriendsListAdapter();
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(inviteFriendsListAdapter);
    }

    private void initListener() {
        //binding.inviteFriendsSearchBtn.setOnClickListener(v -> onSearchButtonClicked());
        binding.inviteFriendsBackBtn.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInfoMeetup));

        binding.inviteFriendsSubmitBtn.setOnClickListener(v -> {

            String location = createMeetupViewModel.getMeetupLocation().getValue();
            String time = createMeetupViewModel.getMeetupTime().getValue();
            Boolean isPrivate = createMeetupViewModel.getMeetupIsPrivate().getValue();

            Meetup meetup = new Meetup(requestingUser, location, time, isPrivate, new ArrayList<>(Arrays.asList("Max", "Julia", "Tim")));
            createMeetupViewModel.createMeetup2(meetup);

            Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInvitationSuccess);

        });


    }

    public void initViewModel() {
        createMeetupViewModel = new ViewModelProvider(requireActivity()).get(CreateMeetupViewModel.class);

       // createMeetupViewModel = new ViewModelProvider(this).get(CreateMeetupViewModel.class);
        createMeetupViewModel.setOnNotificationSentListener(this);

        createMeetupViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users == null) {
                return;
            }

            inviteFriendsListAdapter.setUsers(users);
            binding.inviteFriendsLoadingCircle.setVisibility(View.GONE);
            binding.rvUserList.setVisibility(View.VISIBLE);
        });

       /* createMeetupViewModel.getSelectedUsers().observe(getViewLifecycleOwner(), selectedUsers -> {
            if (selectedUsers == null) {
                return;
            }

            showFriendRequestButton(selectedUsers.size() > 0);
            inviteFriendsListAdapter.setSelectedUsers(selectedUsers);
        });*/
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private void onSearchButtonClicked() {
        String query = binding.inviteUserSearch.getText().toString();
        createMeetupViewModel.searchUsers(query);

        Utils.hideKeyboard(requireActivity(), binding.getRoot());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onNotificationSent() {
        Snackbar.make(binding.getRoot(), "Anfragen wurden verschickt", Snackbar.LENGTH_SHORT).show();
    }


}

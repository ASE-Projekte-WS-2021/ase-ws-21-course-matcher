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

import com.example.cm.ui.SharedViewModel;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.databinding.FragmentSelectFriendsBinding;
import com.example.cm.ui.adapters.SelectFriendsAdapter;
import com.example.cm.ui.adapters.SelectFriendsAdapter.OnItemClickListener;
import com.example.cm.ui.select_friends.SelectFriendsViewModel.OnNotificationSentListener;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;


public class InviteFriendsFragment extends Fragment implements AdapterView.OnItemClickListener, OnNotificationSentListener {

    String requestingUser = ("CURRENTLY LOGGED IN USER");

    private SharedViewModel sharedViewModel;
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
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        inviteFriendsListAdapter = new InviteFriendsListAdapter(this);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(inviteFriendsListAdapter);
    }

    private void initListener() {
        binding.inviteFriendsBackBtn.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInfoMeetup));

        binding.inviteFriendsSubmitBtn.setOnClickListener(v -> {

            String location = sharedViewModel.getMeetupLocation().getValue();
            String time = sharedViewModel.getMeetupTime().getValue();
            Boolean isPrivate = sharedViewModel.getMeetupIsPrivate().getValue();

            Meetup meetup = new Meetup(requestingUser, location, time, isPrivate, new ArrayList<>(Arrays.asList("Max", "Julia", "Tim")));
            sharedViewModel.createMeetup2(meetup);

            Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInvitationSuccess);

        });
        binding.btnSearch.setOnClickListener(v -> onSearchButtonClicked());
        binding.btnSendFriendRequest.setOnClickListener(v -> onSendRequestButtonClicked());

    }

    public void initViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setOnNotificationSentListener(this);

        sharedViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users == null) {
                return;
            }

            inviteFriendsListAdapter.setUsers(users);
            binding.loadingCircle.setVisibility(View.GONE);
            binding.rvUserList.setVisibility(View.VISIBLE);
        });

        inviteFriendsListAdapter.getSelectedUsers().observe(getViewLifecycleOwner(), selectedUsers -> {
            if (selectedUsers == null) {
                return;
            }

            showFriendRequestButton(selectedUsers.size() > 0);
            inviteFriendsListAdapter.setSelectedUsers(selectedUsers);
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private void onSendRequestButtonClicked() {
        sharedViewModel.sendFriendRequest();
    }

    private void onSearchButtonClicked() {
        String query = binding.etUserSearch.getText().toString();
        sharedViewModel.searchUsers(query);

        Utils.hideKeyboard(requireActivity(), binding.getRoot());
    }

    private void showFriendRequestButton(boolean showButton) {
        if (showButton) {
            binding.btnSendFriendRequest.setVisibility(View.VISIBLE);
            return;
        }
        binding.btnSendFriendRequest.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCheckBoxClicked(String id) {
        sharedViewModel.toggleSelectUser(id);
    }

    @Override
    public void onItemClicked(String id) {
        // TODO: Open profile of clicked user
    }

    @Override
    public void onNotificationSent() {
        Snackbar.make(binding.getRoot(), "Anfragen wurden verschickt", Snackbar.LENGTH_SHORT).show();
    }


}

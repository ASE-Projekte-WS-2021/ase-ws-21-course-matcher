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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.R;
import com.example.cm.databinding.FragmentInviteFriendsBinding;
import com.example.cm.ui.meetup.CreateMeetupViewModel;
import com.example.cm.ui.adapters.InviteFriendsAdapter;
import com.example.cm.ui.add_friends.AddFriendsViewModel.OnNotificationSentListener;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;


public class InviteFriendsFragment extends Fragment implements AdapterView.OnItemClickListener, OnNotificationSentListener, InviteFriendsAdapter.OnItemClickListener {

    private CreateMeetupViewModel createMeetupViewModel;
    private FragmentInviteFriendsBinding binding;
    private InviteFriendsAdapter inviteFriendsListAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInviteFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initUI();
        initViewModel();
        initListener();

        return root;
    }

    private void initUI() {
        createMeetupViewModel = new ViewModelProvider(this).get(CreateMeetupViewModel.class);
        inviteFriendsListAdapter = new InviteFriendsAdapter(this);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(inviteFriendsListAdapter);
    }

    private void initListener() {
        binding.inviteFriendsSearchBtn.setOnClickListener(v -> onSearchButtonClicked());

        binding.btnSendInvite.setOnClickListener(v -> {
            createMeetupViewModel.createMeetup();
            Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInvitationSuccess);
        });
    }


    public void initViewModel() {
        createMeetupViewModel = new ViewModelProvider(requireActivity()).get(CreateMeetupViewModel.class);

        createMeetupViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users == null) {
                return;
            }

            if (users.size() == 0) {
                Snackbar snackbar = Snackbar.make(binding.getRoot(), getContext().getText(R.string.snackbar_no_friends_text), Snackbar.LENGTH_LONG);
                // todo: set snackbar action -> go to add-friends-fragment
                snackbar.show();
                binding.inviteFriendsLoadingCircle.setVisibility(View.GONE);
            }

            inviteFriendsListAdapter.setUsers(users);
            binding.inviteFriendsLoadingCircle.setVisibility(View.GONE);
            binding.rvUserList.setVisibility(View.VISIBLE);
        });

        createMeetupViewModel.getSelectedUsers().observe(getViewLifecycleOwner(), selectedUsers -> {
            if (selectedUsers == null) {
                return;
            }

            showInvitationButton(selectedUsers.size() > 0);
            inviteFriendsListAdapter.setSelectedUsers(selectedUsers);
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void onSearchButtonClicked() {
        String query = binding.inviteUserSearch.getText().toString();
        createMeetupViewModel.searchUsers(query);

        Utils.hideKeyboard(requireActivity(), binding.getRoot());
    }

    private void showInvitationButton(boolean showButton) {
        if (showButton) {
            binding.btnSendInvite.setVisibility(View.VISIBLE);
            return;
        }
        binding.btnSendInvite.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onNotificationAdded() {
        Snackbar.make(binding.getRoot(), R.string.snackbar_sent_requests, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onNotificationDeleted() {

    }


    @Override
    public void onCheckBoxClicked(String id) {
        createMeetupViewModel.toggleSelectUser(id);
    }

    @Override
    public void onItemClicked(String id) {
        // do nothing
    }
}
package com.example.cm.ui.select_friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.databinding.FragmentSelectFriendsBinding;
import com.example.cm.ui.adapters.SelectFriendsAdapter;
import com.example.cm.ui.adapters.SelectFriendsAdapter.OnItemClickListener;
import com.example.cm.ui.select_friends.SelectFriendsViewModel.OnNotificationSentListener;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

public class SelectFriendsFragment extends Fragment implements OnItemClickListener, OnNotificationSentListener {

    private SelectFriendsViewModel selectFriendsViewModel;
    private FragmentSelectFriendsBinding binding;
    private SelectFriendsAdapter selectFriendsAdapter;
    private Navigator navigator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = new Navigator(requireActivity());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigator.navigateToFriends();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSelectFriendsBinding.inflate(inflater, container, false);
        initUI();
        initListener();
        initViewModel();

        return binding.getRoot();
    }


    private void initUI() {
        selectFriendsAdapter = new SelectFriendsAdapter(this);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(selectFriendsAdapter);
    }

    private void initListener() {
        binding.btnSearch.setOnClickListener(v -> onSearchButtonClicked());
        binding.btnSendFriendRequest.setOnClickListener(v -> onSendRequestButtonClicked());
    }

    private void initViewModel() {
        selectFriendsViewModel = new ViewModelProvider(this).get(SelectFriendsViewModel.class);
        selectFriendsViewModel.setOnNotificationSentListener(this);

        selectFriendsViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users == null) {
                return;
            }

            selectFriendsAdapter.setUsers(users);
            binding.loadingCircle.setVisibility(View.GONE);
            binding.rvUserList.setVisibility(View.VISIBLE);
        });

        selectFriendsViewModel.getSelectedUsers().observe(getViewLifecycleOwner(), selectedUsers -> {
            if (selectedUsers == null) {
                return;
            }

            showFriendRequestButton(selectedUsers.size() > 0);
            selectFriendsAdapter.setSelectedUsers(selectedUsers);
        });
    }

    private void onSendRequestButtonClicked() {
        selectFriendsViewModel.sendFriendRequest();
    }

    private void onSearchButtonClicked() {
        String query = binding.etUserSearch.getText().toString();
        selectFriendsViewModel.searchUsers(query);

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
        selectFriendsViewModel.toggleSelectUser(id);
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

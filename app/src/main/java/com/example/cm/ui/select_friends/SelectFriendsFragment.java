package com.example.cm.ui.select_friends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.R;
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
    private OnItemClickListener onItemClickListener;
    private Navigator navigator;

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
        navigator = new Navigator(requireActivity());
        binding.btnSearch.setOnClickListener(v -> onSearchButtonClicked());
    }

    private void initViewModel() {
        selectFriendsViewModel = new ViewModelProvider(this).get(SelectFriendsViewModel.class);
        selectFriendsViewModel.setOnNotificationSentListener(this);
        observeSentFriendRequests();

    }

    private void observeSentFriendRequests() {
        selectFriendsViewModel.getSentFriendRequests().observe(getViewLifecycleOwner(), sentFriendRequests -> {
            if (sentFriendRequests == null) {
                return;
            }

            selectFriendsAdapter.setSentFriendRequests(sentFriendRequests);
        });
    }

    private void onSearchButtonClicked() {
        String query = binding.etUserSearch.getText().toString();
        selectFriendsViewModel.searchUsers(query);

        Utils.hideKeyboard(requireActivity(), binding.getRoot());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onFriendRequestButtonClicked(String receiverId, int position) {
        selectFriendsViewModel.sendOrDeleteFriendRequest(receiverId);
    }


    @Override
    public void onItemClicked(String id) {

        Bundle bundle = new Bundle();
        bundle.putString("userId", id);

        navigator.getNavController().navigate(R.id.fromSelectFriendsToProfile, bundle);

    }


    @Override
    public void onNotificationSent() {
        Snackbar.make(binding.getRoot(), "Anfrage wurde verschickt", Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void onNotificationDeleted() {
        Snackbar.make(binding.getRoot(), "Anfrage wurde entfernt", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFriendRequestsSet() {
        selectFriendsViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users == null) {
                return;
            }

            selectFriendsAdapter.setUsers(users);
            binding.loadingCircle.setVisibility(View.GONE);
            binding.rvUserList.setVisibility(View.VISIBLE);
        });
    }
}

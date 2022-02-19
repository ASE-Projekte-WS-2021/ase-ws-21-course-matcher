package com.example.cm.ui.add_friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.FragmentAddFriendsBinding;
import com.example.cm.ui.adapters.AddFriendsAdapter;
import com.example.cm.ui.adapters.AddFriendsAdapter.OnItemClickListener;
import com.example.cm.ui.add_friends.AddFriendsViewModel.OnRequestSentListener;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class AddFriendsFragment extends Fragment implements OnItemClickListener, OnRequestSentListener {

    private AddFriendsViewModel addFriendsViewModel;
    private FragmentAddFriendsBinding binding;
    private AddFriendsAdapter selectFriendsAdapter;
    private Navigator navigator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddFriendsBinding.inflate(inflater, container, false);
        initUI();
        initListener();
        initViewModel();

        return binding.getRoot();
    }


    private void initUI() {
        selectFriendsAdapter = new AddFriendsAdapter(this, requireActivity());
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(selectFriendsAdapter);
    }

    private void initListener() {
        navigator = new Navigator(requireActivity());
        binding.btnSearch.setOnClickListener(v -> onSearchButtonClicked());
    }

    private void initViewModel() {
        addFriendsViewModel = new ViewModelProvider(this).get(AddFriendsViewModel.class);
        addFriendsViewModel.setOnRequestSentListener(this);
        observeSentFriendRequests();
    }

    private void observeSentFriendRequests() {
        addFriendsViewModel.getSentFriendRequests().observe(getViewLifecycleOwner(), sentFriendRequests -> {
            if (sentFriendRequests == null) {
                return;
            }
            ArrayList<Request> requestsToSet = new ArrayList<>();
            for(FriendRequest request : sentFriendRequests) {
                requestsToSet.add((Request) request);
            }
            selectFriendsAdapter.setSentFriendRequests(requestsToSet);
        });
    }

    private void onSearchButtonClicked() {
        String query = binding.etUserSearch.getText().toString();
        addFriendsViewModel.searchUsers(query);

        Utils.hideKeyboard(requireActivity(), binding.getRoot());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onFriendRequestButtonClicked(String receiverId) {
        addFriendsViewModel.sendOrDeleteFriendRequest(receiverId);
    }


    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);

        navigator.getNavController().navigate(R.id.fromSelectFriendsToProfile, bundle);
    }

    @Override
    public void onRequestAdded() {
        Snackbar.make(binding.getRoot(), R.string.snackbar_sent_request, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onRequestDeleted() {
        Snackbar.make(binding.getRoot(), R.string.snackbar_remove_request, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onFriendRequestsSet() {
        addFriendsViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users == null) {
                return;
            }

            selectFriendsAdapter.setUsers(users);
            binding.loadingCircle.setVisibility(View.GONE);
            binding.rvUserList.setVisibility(View.VISIBLE);
        });
    }
}

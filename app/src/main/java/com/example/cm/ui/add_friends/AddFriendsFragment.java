package com.example.cm.ui.add_friends;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.databinding.FragmentAddFriendsBinding;
import com.example.cm.ui.adapters.AddFriendsAdapter;
import com.example.cm.ui.adapters.AddFriendsAdapter.OnItemClickListener;
import com.example.cm.ui.add_friends.AddFriendsViewModel.OnRequestSentListener;
import com.example.cm.utils.Navigator;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class AddFriendsFragment extends Fragment implements OnItemClickListener, OnRequestSentListener {

    private AddFriendsViewModel addFriendsViewModel;
    private FragmentAddFriendsBinding binding;
    private AddFriendsAdapter selectFriendsAdapter;
    private Navigator navigator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddFriendsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        initUI();
        initListener();
        initViewModel();
        return binding.getRoot();
    }

    private void initUI() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        selectFriendsAdapter = new AddFriendsAdapter(this, requireActivity());
        dividerItemDecoration.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal)));
        binding.rvUserList.addItemDecoration(dividerItemDecoration);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(selectFriendsAdapter);
        binding.btnBack.bringToFront();
    }

    private void initListener() {
        binding.ivClearInput.setOnClickListener(v -> onClearInputClicked());
        binding.etUserSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onSearchTextChanged(charSequence);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
    }

    private void onClearInputClicked() {
        binding.etUserSearch.setText(requireActivity().getString(R.string.empty_string));
        addFriendsViewModel.searchUsers(requireActivity().getString(R.string.empty_string));
        binding.ivClearInput.setVisibility(View.GONE);
    }

    private void onSearchTextChanged(CharSequence charSequence) {
        String query = charSequence.toString();
        if (!query.isEmpty()) {
            binding.ivClearInput.setVisibility(View.VISIBLE);
        } else {
            binding.ivClearInput.setVisibility(View.GONE);
        }

        addFriendsViewModel.searchUsers(query);
    }

    private void initViewModel() {
        addFriendsViewModel = new ViewModelProvider(this).get(AddFriendsViewModel.class);
        addFriendsViewModel.setOnRequestSentListener(this);
        observeSentFriendRequests();
    }

    private void observeSentFriendRequests() {
        addFriendsViewModel.getSentFriendRequestsPending().observe(getViewLifecycleOwner(), sentFriendRequests -> {
            if (sentFriendRequests == null) {
                return;
            }
            addFriendsViewModel.getReceivedFriendRequestsPending().observe(getViewLifecycleOwner(), receivedFriendRequests -> {
                selectFriendsAdapter.setFriendRequests(sentFriendRequests, receivedFriendRequests);
            });
        });
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
            binding.loadingCircle.setVisibility(View.GONE);

            if (users == null || users.isEmpty()) {
                binding.noFriendsWrapper.setVisibility(View.VISIBLE);
                binding.rvUserList.setVisibility(View.GONE);
                return;
            }

            selectFriendsAdapter.setUsers(users);
            binding.noFriendsWrapper.setVisibility(View.GONE);
            binding.rvUserList.setVisibility(View.VISIBLE);
        });
    }
}
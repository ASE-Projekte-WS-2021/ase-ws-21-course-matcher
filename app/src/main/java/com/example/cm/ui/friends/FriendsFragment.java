package com.example.cm.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.R;
import com.example.cm.databinding.FragmentFriendsBinding;
import com.example.cm.ui.adapters.FriendsListAdapter;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;

public class FriendsFragment extends Fragment implements FriendsListAdapter.OnItemClickListener {

    private FriendsViewModel friendsViewModel;
    private FragmentFriendsBinding binding;
    private FriendsListAdapter friendsListAdapter;
    private Navigator navigator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = new Navigator(requireActivity());

        // Handles bottom back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigator.navigateToProfile();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        initUI();
        initListener();
        initViewModel();

        return binding.getRoot();
    }


    private void initUI() {
        setHasOptionsMenu(true);
        friendsListAdapter = new FriendsListAdapter(this);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(friendsListAdapter);
    }

    private void initListener() {
        binding.btnSearch.setOnClickListener(v -> onSearchButtonClicked());
    }

    private void initViewModel() {
        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        friendsViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users == null) {
                return;
            }
            friendsListAdapter.setUsers(users);
            binding.loadingCircle.setVisibility(View.GONE);
            binding.rvUserList.setVisibility(View.VISIBLE);
        });
    }

    private void onSearchButtonClicked() {
        String query = binding.etUserSearch.getText().toString();
        friendsViewModel.searchUsers(query);

        Utils.hideKeyboard(requireActivity(), binding.getRoot());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_friends, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_friend) {
            navigator.navigateToSelectFriends();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(String id) {
        // TODO: Open profile of clicked user
    }
}

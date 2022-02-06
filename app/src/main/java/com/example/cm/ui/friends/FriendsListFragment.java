package com.example.cm.ui.friends;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.R;
import com.example.cm.databinding.FragmentFriendsListBinding;
import com.example.cm.ui.adapters.FriendsListAdapter;
import com.example.cm.ui.adapters.FriendsListAdapter.OnItemClickListener;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;

public class FriendsListFragment extends Fragment implements OnItemClickListener {

    private FriendsViewModel friendsViewModel;
    private FragmentFriendsListBinding binding;
    private FriendsListAdapter friendsListAdapter;
    private Navigator navigator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsListBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());

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
        binding.btnAddFriends.setOnClickListener(v -> navigator.navigateToSelectFriends());
    }

    private void initViewModel() {
        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        friendsViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            binding.loadingCircle.setVisibility(View.GONE);

            if (friends == null) {
                binding.noFriendsWrapper.setVisibility(View.VISIBLE);
                return;
            }

            if (friends.size() == 0) {
                binding.noFriendsWrapper.setVisibility(View.VISIBLE);
                return;
            }

            friendsListAdapter.setFriends(friends);
            binding.rvUserList.setVisibility(View.VISIBLE);
            binding.noFriendsWrapper.setVisibility(View.GONE);
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
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", id);
        navigator.getNavController().navigate(R.id.fromFriendsToProfile, bundle);
    }
}

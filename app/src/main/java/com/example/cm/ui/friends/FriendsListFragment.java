package com.example.cm.ui.friends;


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

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.databinding.FragmentFriendsListBinding;
import com.example.cm.ui.adapters.FriendsListAdapter;
import com.example.cm.ui.adapters.FriendsListAdapter.OnItemClickListener;
import com.example.cm.utils.LinearLayoutManagerWrapper;
import com.example.cm.utils.Navigator;

import java.util.Objects;

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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal)));
        binding.rvUserList.addItemDecoration(dividerItemDecoration);
        binding.rvUserList.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(friendsListAdapter);
    }

    private void initListener() {
        //binding.btnSearch.setOnClickListener(v -> onSearchButtonClicked());
        binding.btnAddFriends.setOnClickListener(v -> navigator.navigateToSelectFriends());
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
    }

    private void onClearInputClicked() {
        binding.etUserSearch.setText("");
        friendsViewModel.searchUsers("");
        binding.ivClearInput.setVisibility(View.GONE);
    }

    private void initViewModel() {
        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        friendsViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            binding.loadingCircle.setVisibility(View.GONE);

            if (friends.isEmpty()) {
                binding.noFriendsWrapper.setVisibility(View.VISIBLE);
                return;
            }

            friendsListAdapter.setFriends(friends);
            binding.rvUserList.setVisibility(View.VISIBLE);
            binding.noFriendsWrapper.setVisibility(View.GONE);
        });
    }

    private void onSearchTextChanged(CharSequence charSequence) {
        String query = charSequence.toString();
        if (query.length() > 0) {
            binding.ivClearInput.setVisibility(View.VISIBLE);
        } else {
            binding.ivClearInput.setVisibility(View.GONE);
        }

        friendsViewModel.searchUsers(query);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);
        navigator.getNavController().navigate(R.id.fromFriendsToProfile, bundle);
    }
}

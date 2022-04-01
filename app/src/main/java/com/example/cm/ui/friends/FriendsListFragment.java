package com.example.cm.ui.friends;

import android.annotation.SuppressLint;
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
import com.example.cm.data.models.User;
import com.example.cm.databinding.FragmentFriendsListBinding;
import com.example.cm.ui.adapters.FriendsListAdapter;
import com.example.cm.ui.adapters.FriendsListAdapter.OnItemClickListener;
import com.example.cm.utils.LinearLayoutManagerWrapper;
import com.example.cm.utils.Navigator;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

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
        binding.ivClearInput.setOnClickListener(v -> onClearInputClicked());
        binding.etUserSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
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
        binding.ivClearInput.setVisibility(View.GONE);
    }


    private void initViewModel() {
        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        observeFriends();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void observeFriends() {
        friendsViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            binding.loadingCircle.setVisibility(View.GONE);

            if (friends == null || friends.isEmpty()) {
                binding.noFriendsWrapper.setVisibility(View.VISIBLE);
                binding.rvUserList.setVisibility(View.GONE);
                return;
            }

            Timber.d("Getting friends...: %s", friends.size());

            friendsListAdapter.setFriends(friends);
            friendsListAdapter.notifyDataSetChanged();
            binding.rvUserList.setVisibility(View.VISIBLE);
            binding.noFriendsWrapper.setVisibility(View.GONE);
        });
    }

    private void onSearchTextChanged(CharSequence charSequence) {
        String query = charSequence.toString();
        toggleClearButton(query);
        updateListByQuery(query);
    }

    private void toggleClearButton(String query) {
        if (!query.isEmpty()) {
            binding.ivClearInput.setVisibility(View.VISIBLE);
        } else {
            binding.ivClearInput.setVisibility(View.GONE);
        }
    }

    private void updateListByQuery(String query) {
        List<User> filteredUsers = friendsViewModel.getFilteredUsers(query);
        if (filteredUsers == null) {
            return;
        }
        if (filteredUsers.isEmpty()) {
            binding.noFriendsWrapper.setVisibility(View.VISIBLE);
            binding.rvUserList.setVisibility(View.GONE);
            return;
        }
        binding.noFriendsWrapper.setVisibility(View.GONE);
        binding.rvUserList.setVisibility(View.VISIBLE);
        friendsListAdapter.setFriends(filteredUsers);
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


    @Override
    public void onResume() {
        super.onResume();
        if(friendsViewModel != null) {
            observeFriends();
        }
    }
}

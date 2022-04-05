package com.example.cm.ui.invite_friends;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.User;
import com.example.cm.databinding.FragmentInviteMoreFriendsBinding;
import com.example.cm.ui.adapters.InviteFriendsAdapter;
import com.example.cm.utils.Navigator;

import java.util.List;

public class InviteMoreFriendsFragment extends Fragment
        implements AdapterView.OnItemClickListener, InviteFriendsAdapter.OnItemClickListener {

    private final String meetupId;
    private final List<String> userIdsAlreadyInMeetup;
    private InviteMoreFriendsViewModel inviteMoreFriendsViewModel;
    private FragmentInviteMoreFriendsBinding binding;
    private InviteFriendsAdapter inviteFriendsListAdapter;
    private Navigator navigator;
    private boolean filtered = false;

    public InviteMoreFriendsFragment(String meetupId, List<String> userIdsAlreadyInMeetup) {
        this.meetupId = meetupId;
        this.userIdsAlreadyInMeetup = userIdsAlreadyInMeetup;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInviteMoreFriendsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());

        initUI();
        initViewModel();
        initListener();

        return binding.getRoot();
    }

    private void initUI() {
        inviteFriendsListAdapter = new InviteFriendsAdapter(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);

        if (AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal) != null) {
            dividerItemDecoration
                    .setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal));
        }

        binding.rvUserList.addItemDecoration(dividerItemDecoration);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(inviteFriendsListAdapter);
    }

    private void initListener() {
        binding.ivClearInput.setOnClickListener(v -> onClearInputClicked());
        binding.btnSendInvite.setOnClickListener(v -> inviteMoreFriendsViewModel.sendMeetupRequests());
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

    @SuppressLint("NotifyDataSetChanged")
    public void initViewModel() {
        inviteMoreFriendsViewModel = new ViewModelProvider(this, new InviteMoreFriendsFactory(meetupId))
                .get(InviteMoreFriendsViewModel.class);
        inviteMoreFriendsViewModel.getUsers(userIdsAlreadyInMeetup).observe(getViewLifecycleOwner(), users -> {
            binding.loadingCircle.setVisibility(View.GONE);

            if (users.isEmpty()) {
                binding.tvNoFriendsFound.setText(R.string.friendslist_tv_no_friends_not_in_meetup);
                binding.noFriendsWrapper.setVisibility(View.VISIBLE);
                binding.rvUserList.setVisibility(View.GONE);
                return;
            }

            inviteFriendsListAdapter.setUsers(users);
            inviteFriendsListAdapter.notifyDataSetChanged();
            binding.rvUserList.setVisibility(View.VISIBLE);
            binding.noFriendsWrapper.setVisibility(View.GONE);
        });

        inviteMoreFriendsViewModel.getSelectedUsers().observe(getViewLifecycleOwner(), selectedUsers -> {
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

    private void onClearInputClicked() {
        binding.etUserSearch.setText("");
        binding.ivClearInput.setVisibility(View.GONE);
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
        List<User> filteredUsers = inviteMoreFriendsViewModel.getFilteredUsers(query);
        if (filteredUsers == null) {
            return;
        }
        if (filteredUsers.isEmpty()) {
            binding.tvNoFriendsFound.setText(R.string.find_friends_no_friends_found);
            binding.noFriendsWrapper.setVisibility(View.VISIBLE);
            binding.rvUserList.setVisibility(View.GONE);
            return;
        }
        binding.noFriendsWrapper.setVisibility(View.GONE);
        binding.rvUserList.setVisibility(View.VISIBLE);
        inviteFriendsListAdapter.setUsers(filteredUsers);
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
        inviteMoreFriendsViewModel.clearSelectedUsers();
    }

    @Override
    public void onCheckBoxClicked(String id) {
        inviteMoreFriendsViewModel.toggleSelectUser(id);
    }

    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);
        navigator.getNavController().navigate(R.id.action_global_navigate_to_other_profile, bundle);
    }
}
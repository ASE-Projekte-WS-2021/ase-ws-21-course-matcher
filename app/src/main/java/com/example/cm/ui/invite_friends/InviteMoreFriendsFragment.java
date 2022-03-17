package com.example.cm.ui.invite_friends;

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
import com.example.cm.databinding.FragmentInviteMoreFriendsBinding;
import com.example.cm.ui.adapters.InviteFriendsAdapter;
import com.example.cm.utils.Navigator;

import java.util.List;
import java.util.Objects;


public class InviteMoreFriendsFragment extends Fragment implements AdapterView.OnItemClickListener,
        InviteFriendsAdapter.OnItemClickListener {

    private InviteMoreFriendsViewModel inviteMoreFriendsViewModel;
    private FragmentInviteMoreFriendsBinding binding;
    private InviteFriendsAdapter inviteFriendsListAdapter;
    private String meetupId;
    private List<String> userIdsAlreadyInMeetup;
    private Navigator navigator;

    public InviteMoreFriendsFragment(String meetupId, List<String> userIdsAlreadyInMeetup) {
        this.meetupId = meetupId;
        this.userIdsAlreadyInMeetup = userIdsAlreadyInMeetup;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInviteMoreFriendsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        View root = binding.getRoot();
        initUI();
        initViewModel();
        initListener();
        return root;
    }

    private void initUI() {
        inviteFriendsListAdapter = new InviteFriendsAdapter(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal)));
        binding.rvUserList.addItemDecoration(dividerItemDecoration);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserList.setHasFixedSize(true);
        binding.rvUserList.setAdapter(inviteFriendsListAdapter);
    }

    private void initListener() {
        binding.ivClearInput.setOnClickListener(v -> onClearInputClicked());
        binding.btnSendInvite.setOnClickListener(v -> {
            inviteMoreFriendsViewModel.sendMeetupRequests();
        });
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

    public void initViewModel() {
        inviteMoreFriendsViewModel = new ViewModelProvider(this, new InviteMoreFriendsFactory(meetupId)).get(InviteMoreFriendsViewModel.class);
        inviteMoreFriendsViewModel.getUsers(userIdsAlreadyInMeetup).observe(getViewLifecycleOwner(), users -> {
            binding.loadingCircle.setVisibility(View.GONE);

            if (users.isEmpty()) {
                binding.noFriendsWrapper.setVisibility(View.VISIBLE);
                binding.rvUserList.setVisibility(View.GONE);
                return;
            }

            inviteFriendsListAdapter.setUsers(users);
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
        inviteMoreFriendsViewModel.searchUsers("", userIdsAlreadyInMeetup);
        binding.ivClearInput.setVisibility(View.GONE);
    }

    private void onSearchTextChanged(CharSequence charSequence) {
        String query = charSequence.toString();
        if (query.length() > 0) {
            binding.ivClearInput.setVisibility(View.VISIBLE);
        } else {
            binding.ivClearInput.setVisibility(View.GONE);
        }
        inviteMoreFriendsViewModel.searchUsers(query, userIdsAlreadyInMeetup);
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
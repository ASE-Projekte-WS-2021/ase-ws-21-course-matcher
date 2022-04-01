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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.User;
import com.example.cm.databinding.FragmentInviteFriendsBinding;
import com.example.cm.ui.adapters.InviteFriendsAdapter;
import com.example.cm.ui.meetup.CreateMeetup.CreateMeetupViewModel;
import com.example.cm.utils.Navigator;

import java.util.List;
import java.util.Objects;


public class InviteFriendsFragment extends Fragment implements AdapterView.OnItemClickListener,
        InviteFriendsAdapter.OnItemClickListener {

    private CreateMeetupViewModel createMeetupViewModel;
    private FragmentInviteFriendsBinding binding;
    private InviteFriendsAdapter inviteFriendsListAdapter;
    private Navigator navigator;
    private Bundle bundle;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInviteFriendsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        bundle = getArguments();
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
        binding.btnBack.bringToFront();
    }

    private void initListener() {
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.ivClearInput.setOnClickListener(v -> onClearInputClicked());
        binding.btnSendInvite.setOnClickListener(v -> {
            createMeetupViewModel.createMeetup();
            navigator.getNavController().navigate(R.id.navigateToMeetupInviteSuccess);
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
        createMeetupViewModel = (CreateMeetupViewModel) bundle.getSerializable(Constants.KEY_CREATE_MEETUP_VM);
        createMeetupViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
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

        createMeetupViewModel.getSelectedUsers().observe(getViewLifecycleOwner(), selectedUsers -> {
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
        List<User> filteredUsers = createMeetupViewModel.getFilteredUsers(query);
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
        createMeetupViewModel.clearSelectedUsers();
    }

    @Override
    public void onCheckBoxClicked(String id) {
        createMeetupViewModel.toggleSelectUser(id);
    }

    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);
        navigator.getNavController().navigate(R.id.action_navigation_invite_friends_to_navigation_other_profile, bundle);
    }
}
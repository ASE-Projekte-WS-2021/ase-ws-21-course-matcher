package com.example.cm.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.databinding.FragmentProfileBinding;
import com.example.cm.utils.Navigator;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private Navigator navigator;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        initListener();
        initViewModel();

        return binding.getRoot();
    }

    private void initListener() {
        navigator = new Navigator(requireActivity());
        binding.btnToFriendsList.setOnClickListener(v -> navigator.navigateToFriends());
    }

    private void initViewModel() {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        profileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            binding.tvName.setText(currentUser.getFullName());
            binding.tvUsername.setText(currentUser.getUsername());
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("userId")) {
                binding.btnToFriendsList.setVisibility(View.GONE);
                String profileId = bundle.getString("userId");
                profileViewModel.getUserById(profileId);
            }
        } else {
            profileViewModel.getLoggedInUser();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
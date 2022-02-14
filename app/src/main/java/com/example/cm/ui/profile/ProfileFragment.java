package com.example.cm.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.databinding.FragmentProfileBinding;
import com.example.cm.utils.Navigator;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        initViewModel();

        return binding.getRoot();
    }

    private void initViewModel() {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        Bundle bundle = this.getArguments();
        getProfileInformation(bundle);

        profileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            binding.tvName.setText(currentUser.getFullName());
            binding.tvUsername.setText(currentUser.getUsername());
        });
    }

    private void getProfileInformation(Bundle bundle) {
        if (bundle == null) {
            profileViewModel.getLoggedInUser();
            return;
        }

        if (bundle.containsKey("userId")) {
            String profileId = bundle.getString("userId");
            profileViewModel.getUserById(profileId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
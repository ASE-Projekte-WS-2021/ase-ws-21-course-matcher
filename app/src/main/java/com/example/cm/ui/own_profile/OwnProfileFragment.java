package com.example.cm.ui.own_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.data.models.Availability;
import com.example.cm.data.models.User;
import com.example.cm.databinding.FragmentOwnProfileBinding;
import com.example.cm.utils.Navigator;
import com.google.android.gms.maps.GoogleMap;
import com.squareup.picasso.Picasso;

public class OwnProfileFragment extends Fragment {

    private OwnProfileViewModel ownProfileViewModel;
    private FragmentOwnProfileBinding binding;
    private Navigator navigator;
    private GoogleMap map;
    private Availability availability;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnProfileBinding.inflate(inflater, container, false);
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.btnProfileSettings.setOnClickListener(v -> {
            navigator.getNavController().navigate(R.id.action_navigation_profile_to_settingsFragment);
        });
        binding.btnEditProfile.setOnClickListener(v -> {
            navigator.getNavController().navigate(R.id.action_ownProfileFragment_to_editProfileFragment);
        });
    }

    private void initViewModel() {
        ownProfileViewModel = new ViewModelProvider(this).get(OwnProfileViewModel.class);
        ownProfileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }

            binding.tvName.setText(currentUser.getFullName());
            binding.tvUsername.setText(currentUser.getUsername());
            binding.tvBioDescription.setText(currentUser.getBio());
            if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
                binding.ivProfileImage.setImageTintMode(null);
                binding.ivProfileImage.setScaleX(1f);
                binding.ivProfileImage.setScaleY(1f);
                Picasso.get().load(currentUser.getProfileImageUrl()).fit().centerCrop().into(binding.ivProfileImage);
                binding.btnProfileSettings.bringToFront();
            }
            availability = currentUser.getAvailability();
            if (availability != null) {
                switch (availability) {
                    case AVAILABLE:
                        binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_available);
                        break;
                    case SOON_AVAILABLE:
                        binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_soon_available);
                        break;
                    case UNAVAILABLE:
                        binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_unavailable);
                        break;
                }
            }
            initUi(currentUser);

        });
    }

    private void initUi(User currentUser) {
        binding.btnEditProfile.setText(R.string.profile_btn_edit);
        binding.tvName.setText(currentUser.getFullName());
        binding.tvUsername.setText(currentUser.getUsername());
        binding.tvBioDescription.setText(currentUser.getBio());
        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
            binding.ivProfileImage.setImageTintMode(null);
            binding.ivProfileImage.setScaleX(1f);
            binding.ivProfileImage.setScaleY(1f);
            Picasso.get().load(currentUser.getProfileImageUrl()).fit().centerCrop().into(binding.ivProfileImage);
        }
        binding.btnProfileSettings.bringToFront();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
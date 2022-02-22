package com.example.cm.ui.own_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.databinding.FragmentOwnProfileBinding;

import timber.log.Timber;

public class OwnProfileFragment extends Fragment {

    private OwnProfileViewModel ownProfileViewModel;
    private FragmentOwnProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnProfileBinding.inflate(inflater, container, false);
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        binding.btnProfileSettings.setOnClickListener(v -> {
            Timber.d("Profile settings button clicked");
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
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
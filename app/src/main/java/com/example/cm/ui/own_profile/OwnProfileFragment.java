package com.example.cm.ui.own_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.Constants;
import com.example.cm.databinding.FragmentOwnProfileBinding;

public class OwnProfileFragment extends Fragment {

    private OwnProfileViewModel ownProfileViewModel;
    private FragmentOwnProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnProfileBinding.inflate(inflater, container, false);
        initViewModel();

        return binding.getRoot();
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
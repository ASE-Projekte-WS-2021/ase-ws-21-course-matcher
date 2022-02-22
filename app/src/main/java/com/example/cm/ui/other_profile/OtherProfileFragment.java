package com.example.cm.ui.other_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.Constants;
import com.example.cm.databinding.FragmentOtherProfileBinding;
import com.example.cm.utils.Navigator;

public class OtherProfileFragment extends Fragment {

    private OtherProfileViewModel otherProfileViewModel;
    private FragmentOtherProfileBinding binding;
    private Navigator navigator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtherProfileBinding.inflate(inflater, container, false);
        initViewModel();
        initListener();

        return binding.getRoot();
    }

    private void initListener() {
        navigator = new Navigator(getActivity());
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
    }

    private void initViewModel() {
        otherProfileViewModel = new ViewModelProvider(this).get(OtherProfileViewModel.class);
        getProfileInformation();

        otherProfileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            binding.tvName.setText(currentUser.getFullName());
            binding.tvUsername.setText(currentUser.getUsername());
        });
    }

    private void getProfileInformation() {
        Bundle bundle = this.getArguments();

        if (bundle == null) {
            return;
        }

        if (bundle.containsKey(Constants.KEY_USER_ID)) {
            String profileId = bundle.getString(Constants.KEY_USER_ID);
            otherProfileViewModel.getUserById(profileId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
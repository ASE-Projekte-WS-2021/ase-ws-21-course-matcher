package com.example.cm.ui.settings.edit_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cm.databinding.FragmentEditProfileBinding;
import com.example.cm.utils.Navigator;

public class EditProfileFragment extends Fragment {
    FragmentEditProfileBinding binding;
    Navigator navigator;

    public EditProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
    }
}
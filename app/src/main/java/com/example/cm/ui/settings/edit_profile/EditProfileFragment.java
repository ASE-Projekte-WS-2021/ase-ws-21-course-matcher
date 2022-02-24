package com.example.cm.ui.settings.edit_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cm.R;
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
        initUI();
        initListeners();

        return binding.getRoot();
    }

    private void initUI() {
        binding.actionBar.tvTitle.setText(getString(R.string.title_edit_profile));
        binding.inputUsername.inputLabel.setText(R.string.input_label_username);
        binding.inputFirstName.inputLabel.setText(getString(R.string.input_label_first_name));
        binding.inputLastName.inputLabel.setText(getString(R.string.input_label_last_name));
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
    }
}
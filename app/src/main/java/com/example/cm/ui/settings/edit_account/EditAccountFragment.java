package com.example.cm.ui.settings.edit_account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.databinding.FragmentEditAccountBinding;
import com.example.cm.utils.Navigator;

public class EditAccountFragment extends Fragment {
    FragmentEditAccountBinding binding;
    EditAccountViewModel editAccountViewModel;
    Navigator navigator;

    public EditAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false);
        initUI();
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initUI() {
        binding.actionBar.tvTitle.setText(R.string.title_edit_account);
        binding.inputEmail.inputLabel.setText(getString(R.string.input_label_email));
        binding.inputCurrentPassword.inputLabel.setText(getString(R.string.input_label_current_password));
        binding.inputNewPassword.inputLabel.setText(getString(R.string.input_label_new_password));
        binding.inputNewPasswordConfirm.inputLabel.setText(getString(R.string.input_label_new_password_confirm));
        // Disable editing of email field
        binding.inputEmail.inputField.setEnabled(false);
    }

    private void initViewModel() {
        editAccountViewModel = new ViewModelProvider(requireActivity()).get(EditAccountViewModel.class);
        editAccountViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
           binding.inputEmail.inputField.setText(user.getEmail());
        });
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.btnUpdatePassword.setOnClickListener(v -> {
            String currentPassword = binding.inputCurrentPassword.inputField.getText().toString().trim();
            String newPassword = binding.inputNewPassword.inputField.getText().toString().trim();
            String newPasswordConfirm = binding.inputNewPasswordConfirm.inputField.getText().toString().trim();

            editAccountViewModel.updatePassword(currentPassword, newPassword, newPasswordConfirm);
        });
    }
}
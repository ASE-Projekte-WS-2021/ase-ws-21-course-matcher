package com.example.cm.ui.settings.edit_account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.databinding.FragmentEditAccountBinding;
import com.example.cm.utils.Navigator;
import com.google.android.material.snackbar.Snackbar;

public class EditAccountFragment extends Fragment {
    private FragmentEditAccountBinding binding;
    private EditAccountViewModel editAccountViewModel;
    private Navigator navigator;

    public EditAccountFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false);

        initUI();
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initUI() {
        binding.actionBar.tvTitle.setText(R.string.title_edit_account);
        binding.inputEmail.textInputLayout.setHint(getString(R.string.input_label_email));
        binding.inputCurrentPassword.textInputLayout.setHint(getString(R.string.input_label_current_password));
        binding.inputNewPassword.textInputLayout.setHint(getString(R.string.input_label_new_password));
        binding.inputNewPasswordConfirm.textInputLayout.setHint(getString(R.string.input_label_new_password_confirm));
        // Disable editing of email field
        binding.inputEmail.inputField.setEnabled(false);
    }

    private void initViewModel() {
        editAccountViewModel = new ViewModelProvider(requireActivity()).get(EditAccountViewModel.class);
        editAccountViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            binding.inputEmail.inputField.setText(user.getEmail());
        });
        observeStatusChanges();
    }

    private void observeStatusChanges() {
        editAccountViewModel.status.observe(getViewLifecycleOwner(), status -> {
            if (status == null) {
                return;
            }

            switch (status.getFlag()) {
                case SUCCESS:
                    Snackbar.make(binding.getRoot(), getResources().getString(status.getMessageResourceId()), Snackbar.LENGTH_SHORT).show();
                    resetPasswordFields();
                    break;
                case ERROR:
                    Snackbar.make(binding.getRoot(), getResources().getString(status.getMessageResourceId()), Snackbar.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        });
    }

    private void resetPasswordFields() {
        binding.inputCurrentPassword.inputField.setText(requireContext().getString(R.string.empty_string));
        binding.inputNewPassword.inputField.setText(requireContext().getString(R.string.empty_string));
        binding.inputNewPasswordConfirm.inputField.setText(requireContext().getString(R.string.empty_string));
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.btnUpdatePassword.setOnClickListener(v -> onUpdatePasswordClicked());
    }

    private void onUpdatePasswordClicked() {
        boolean isCurrentPasswordNull = binding.inputCurrentPassword.inputField.getText() == null;
        boolean isNewPasswordNull = binding.inputNewPassword.inputField.getText() == null;
        boolean isNewPasswordConfirmNull = binding.inputNewPasswordConfirm.inputField.getText() == null;

        if (isCurrentPasswordNull || isNewPasswordNull || isNewPasswordConfirmNull) {
            return;
        }

        String currentPassword = binding.inputCurrentPassword.inputField.getText().toString().trim();
        String newPassword = binding.inputNewPassword.inputField.getText().toString().trim();
        String newPasswordConfirm = binding.inputNewPasswordConfirm.inputField.getText().toString().trim();

        editAccountViewModel.updatePassword(currentPassword, newPassword, newPasswordConfirm);
    }

    @Override
    public void onPause() {
        super.onPause();
        editAccountViewModel.status.postValue(null);
    }
}


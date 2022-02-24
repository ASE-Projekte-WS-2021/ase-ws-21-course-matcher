package com.example.cm.ui.settings.edit_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.databinding.FragmentEditProfileBinding;
import com.example.cm.utils.EditTextDialog;
import com.example.cm.utils.Navigator;

public class EditProfileFragment extends Fragment implements EditTextDialog.OnSaveListener {
    FragmentEditProfileBinding binding;
    EditProfileViewModel editProfileViewModel;
    Navigator navigator;
    EditTextDialog dialog;

    public EditProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        initUI();
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initViewModel() {
        editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        editProfileViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            binding.inputUsername.inputField.setText(user.getUsername());
            binding.inputFirstName.inputField.setText(user.getFirstName());
            binding.inputLastName.inputField.setText(user.getLastName());
        });
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
        binding.inputUsername.inputField.setOnClickListener(v -> {
            openDialog(getString(R.string.input_label_username), binding.inputUsername.inputField.getText().toString());
        });
        binding.inputFirstName.inputField.setOnClickListener(v -> {
            openDialog(getString(R.string.input_label_first_name), binding.inputFirstName.inputField.getText().toString());
        });
        binding.inputLastName.inputField.setOnClickListener(v -> {
            openDialog(getString(R.string.input_label_last_name), binding.inputLastName.inputField.getText().toString());
        });
    }

    private void openDialog(String fieldToUpdate, String valueToEdit) {
        dialog = new EditTextDialog(requireContext(), this);
        dialog.setFieldToUpdate(fieldToUpdate)
                .setValueOfField(valueToEdit)
                .show();
    }

    @Override
    public void onSave(String fieldToUpdate, String updatedValue) {
        editProfileViewModel.updateField(fieldToUpdate, updatedValue);
        dialog.hide();
    }
}
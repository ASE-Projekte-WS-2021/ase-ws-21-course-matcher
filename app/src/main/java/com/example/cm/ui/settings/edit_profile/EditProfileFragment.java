package com.example.cm.ui.settings.edit_profile;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.config.FieldType;
import com.example.cm.data.repositories.StorageRepository;
import com.example.cm.databinding.FragmentEditProfileBinding;
import com.example.cm.utils.EditTextAreaDialog;
import com.example.cm.utils.EditTextDialog;
import com.example.cm.utils.Navigator;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class EditProfileFragment extends Fragment implements EditTextDialog.OnSaveListener, EditTextAreaDialog.OnSaveListener {
    private final int SELECT_IMAGE_REQUEST_CODE = 1;

    FragmentEditProfileBinding binding;
    EditProfileViewModel editProfileViewModel;
    Navigator navigator;
    Dialog dialog;

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
        editProfileViewModel = new ViewModelProvider(this, new EditProfileViewModelFactory(requireContext())).get(EditProfileViewModel.class);
        editProfileViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            binding.inputUsername.inputField.setText(user.getUsername());
            binding.inputFirstName.inputField.setText(user.getFirstName());
            binding.inputLastName.inputField.setText(user.getLastName());
            binding.inputFieldBio.setText(user.getBio());
            Picasso.get().load(user.getProfileImageUrl()).fit().centerCrop().into(binding.profileImage);
        });

        editProfileViewModel.status.observe(getViewLifecycleOwner(), status -> {
            if (status == null) {
                return;
            }

            switch (status.getFlag()) {
                case SUCCESS:
                    if(dialog != null) {
                        dialog.hide();
                    }
                    Snackbar.make(binding.getRoot(), status.getMessage(), Snackbar.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    if(dialog != null) {
                        dialog.hide();
                    }
                    Snackbar.make(binding.getRoot(), status.getMessage(), Snackbar.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
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
            openDialog(FieldType.TEXT_INPUT.toString(), getString(R.string.input_label_username), binding.inputUsername.inputField.getText().toString());
        });
        binding.inputFirstName.inputField.setOnClickListener(v -> {
            openDialog(FieldType.TEXT_INPUT.toString(), getString(R.string.input_label_first_name), binding.inputFirstName.inputField.getText().toString());
        });
        binding.inputLastName.inputField.setOnClickListener(v -> {
            openDialog(FieldType.TEXT_INPUT.toString(), getString(R.string.input_label_last_name), binding.inputLastName.inputField.getText().toString());
        });
        binding.inputFieldBio.setOnClickListener(v -> {
            openDialog(FieldType.TEXT_AREA.toString(), getString(R.string.input_label_bio), binding.inputFieldBio.getText().toString());
        });
        binding.editProfileImageBtn.setOnClickListener(v -> {
            onEditProfileImageClicked();
        });
    }


    private void onEditProfileImageClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            binding.profileImage.setImageURI(uri);
            editProfileViewModel.updateImage(uri);
        }
    }

    private void openDialog(String fieldType, String fieldToUpdate, String valueToEdit) {
        if (fieldType.equals(FieldType.TEXT_AREA.toString())) {
            dialog = new EditTextAreaDialog(requireContext(), this);
            ((EditTextAreaDialog) dialog).setFieldToUpdate(fieldToUpdate).setValueOfField(valueToEdit).show();
        } else {
            dialog = new EditTextDialog(requireContext(), this);
            ((EditTextDialog) dialog).setFieldToUpdate(fieldToUpdate).setValueOfField(valueToEdit).show();
        }
    }

    @Override
    public void onTextAreaSaved(String fieldToUpdate, String updatedValue) {
        editProfileViewModel.updateField(fieldToUpdate, updatedValue);
    }

    @Override
    public void onTextInputSaved(String fieldToUpdate, String updatedValue) {
        editProfileViewModel.updateField(fieldToUpdate, updatedValue);
    }
}
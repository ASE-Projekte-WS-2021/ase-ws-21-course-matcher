package com.example.cm.ui.settings.edit_profile;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.config.FieldType;
import com.example.cm.data.models.StatusFlag;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.databinding.FragmentEditProfileBinding;
import com.example.cm.ui.dialogs.EditTextAreaDialog;
import com.example.cm.ui.dialogs.EditTextDialog;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;

public class EditProfileFragment extends Fragment implements EditTextDialog.OnSaveListener, EditTextAreaDialog.OnSaveListener {
    private ActivityResultLauncher<String> storagePermissionRequestLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FragmentEditProfileBinding binding;
    private EditProfileViewModel editProfileViewModel;
    private Navigator navigator;
    private Dialog dialog;

    public EditProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        initPermissionRequest();
        initImagePicker();
        initUI();
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    /**
     * Initialize the image picker
     */
    private void initImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Intent intent = result.getData();
                            Uri uri = intent.getData();
                            editProfileViewModel.updateImage(uri, requireContext());
                        } catch (FileNotFoundException e) {
                            Snackbar.make(binding.getRoot(), R.string.edit_profile_error_message, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Initialize the permission request
     */
    private void initPermissionRequest() {
        storagePermissionRequestLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        imagePickerLauncher.launch(intent);
                    }
                });
    }

    private void initViewModel() {
        editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        editProfileViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            binding.inputUsername.inputField.setText(user.getUsername());
            binding.inputDisplayName.inputField.setText(user.getDisplayName());
            binding.inputBio.inputField.setText(user.getBio());

            String profileImageString = user.getProfileImageString();
            if (profileImageString != null && !profileImageString.isEmpty()) {
                Bitmap img = Utils.convertBaseStringToBitmap(profileImageString);
                binding.profileImage.setImageBitmap(img);
            }
        });

        editProfileViewModel.status.observe(getViewLifecycleOwner(), status -> {
            if (status == null) {
                return;
            }

            if (status.getFlag() == StatusFlag.ERROR) {
                if (dialog instanceof EditTextDialog) {
                    ((EditTextDialog) dialog).setError(getString(status.getMessageResourceId()));
                    ((EditTextDialog) dialog).enableConfirmButton();
                    ((EditTextDialog) dialog).setConfirmButtonText(getString(R.string.edit_save));

                } else {
                    ((EditTextAreaDialog) dialog).setError(getString(status.getMessageResourceId()));
                    ((EditTextAreaDialog) dialog).enableConfirmButton();
                    ((EditTextAreaDialog) dialog).setConfirmButtonText(getString(R.string.edit_save));
                }
                return;
            }

            Snackbar.make(binding.getRoot(), status.getMessageResourceId(), Snackbar.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void initUI() {
        binding.actionBar.tvTitle.setText(getString(R.string.title_edit_profile));

        binding.inputUsername.textInputLayout.setHint(R.string.input_label_username);
        binding.inputUsername.inputField.setFocusable(false);
        binding.inputUsername.inputField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.MAX_CHARACTER_NAME)});

        binding.inputDisplayName.textInputLayout.setHint(R.string.input_label_display_name);
        binding.inputDisplayName.inputField.setFocusable(false);
        binding.inputDisplayName.inputField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.MAX_CHARACTER_NAME)});

        binding.inputBio.textInputLayout.setHint(getString(R.string.input_label_bio));
        binding.inputBio.inputField.setFocusable(false);
    }

    private void initListeners() {
        boolean isUsernameNull = binding.inputUsername.inputField.getText() == null;
        boolean isDisplayNameNull = binding.inputDisplayName.inputField.getText() == null;
        boolean isBioNull = binding.inputBio.inputField.getText() == null;

        navigator = new Navigator(requireActivity());
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.inputUsername.inputField.setOnClickListener(v -> {
            if (isUsernameNull) {
                return;
            }
            openDialog(FieldType.TEXT_INPUT.toString(), getString(R.string.input_label_username), binding.inputUsername.inputField.getText().toString());
        });
        binding.inputDisplayName.inputField.setOnClickListener(v -> {
            if (isDisplayNameNull) {
                return;
            }
            openDialog(FieldType.TEXT_INPUT.toString(), getString(R.string.input_label_display_name), binding.inputDisplayName.inputField.getText().toString());
        });
        binding.inputBio.inputField.setOnClickListener(v -> {
            if (isBioNull) {
                return;

            }
            openDialog(FieldType.TEXT_AREA.toString(), getString(R.string.input_label_bio), binding.inputBio.inputField.getText().toString());
        });
        binding.editProfileImageBtn.setOnClickListener(v -> {
            onEditProfileImageClicked();
        });
    }

    private void openDialog(String fieldType, String fieldToUpdate, String valueToEdit) {
        if (fieldType.equals(FieldType.TEXT_AREA.toString())) {
            dialog = new EditTextAreaDialog(requireContext(), this);
            ((EditTextAreaDialog) dialog).setFieldToUpdate(fieldToUpdate).setValueOfField(valueToEdit).show();
        } else {
            dialog = new EditTextDialog(requireContext(), this);
            ((EditTextDialog) dialog).setFieldToUpdate(fieldToUpdate).setValueOfField(valueToEdit).show();
            if (fieldToUpdate.equals(requireContext().getString(R.string.input_label_username))) {
                editProfileViewModel.getUsernames((UserRepository.UsernamesRetrievedCallback) dialog);
            }
        }
    }

    private void onEditProfileImageClicked() {
        boolean hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!hasReadExternalStoragePermission) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            storagePermissionRequestLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onTextAreaSaved(String fieldToUpdate, String updatedValue) {
        editProfileViewModel.updateField(requireContext(), fieldToUpdate, updatedValue);
    }

    @Override
    public void onPause() {
        super.onPause();
        editProfileViewModel.status.postValue(null);
    }

    @Override
    public void onTextInputSaved(String fieldToUpdate, String updatedValue) {
        editProfileViewModel.updateField(requireContext(), fieldToUpdate, updatedValue);
    }
}
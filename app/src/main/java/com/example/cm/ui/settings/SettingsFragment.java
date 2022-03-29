package com.example.cm.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.data.listener.UserListener;
import com.example.cm.databinding.FragmentSettingsBinding;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.ui.dialogs.EditTextDialog;
import com.example.cm.utils.Navigator;
import com.example.cm.ui.dialogs.TextWithButtonDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private Navigator navigator;
    private SettingsViewModel settingsViewModel;
    private TextWithButtonDialog logoutDialog;
    private EditTextDialog deleteAccountDialog;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        initUI();
        initViewModel();
        initListeners();
        initLogoutDialog();
        initDeleteAccountDialog();

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        binding.actionBar.tvTitle.setText(R.string.title_settings);

        // Set labels of links
        binding.linkEditAccount.linkText.setText(getString(R.string.link_label_edit_account));
        binding.linkLogout.linkText.setText(getString(R.string.link_label_logout));
        binding.linkDeleteAccount.linkText.setText(getString(R.string.link_label_delete_account));
        binding.linkDeleteAccount.linkText.setTextColor(getResources().getColor(R.color.red));

        // Set icons of links
        binding.linkEditAccount.linkIcon.setImageResource(R.drawable.ic_edit_account);
        binding.linkLogout.linkIcon.setImageResource(R.drawable.ic_logout);
        binding.linkDeleteAccount.linkIcon.setImageResource(R.drawable.ic_delete);
    }

    private void initLogoutDialog() {
        logoutDialog = new TextWithButtonDialog(requireActivity(), () -> {
            settingsViewModel.logOut();
            goToLoginScreen();
        });
        logoutDialog
                .setTitle(getString(R.string.dialog_logout_title))
                .setConfirmButtonText(getString(R.string.dialog_logout_btn));
    }

    private void initViewModel() {
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.linkEditAccount.linkWrapper.setOnClickListener(v -> onEditAccountClicked());
        binding.linkLogout.linkWrapper.setOnClickListener(v -> onLogoutClicked());
        binding.linkDeleteAccount.linkWrapper.setOnClickListener(v -> onDeleteAccountClicked());
        binding.tvAbout.setOnClickListener(v -> onAboutClicked());
    }

    private void onEditAccountClicked() {
        navigator.getNavController().navigate(R.id.action_settingsFragment_to_editAccountFragment);
    }

    private void onLogoutClicked() {
        logoutDialog.show();
    }

    private void initDeleteAccountDialog() {
        deleteAccountDialog = new EditTextDialog(requireActivity(), (fieldToUpdate, updatedValue) -> settingsViewModel.reauthenticate(requireContext(), updatedValue, new UserListener<Boolean>() {
            @Override
            public void onUserSuccess(Boolean aBoolean) {
                settingsViewModel.deleteAccount(new UserListener<Boolean>() {
                    @Override
                    public void onUserSuccess(Boolean aBoolean) {
                        Snackbar.make(binding.getRoot(), R.string.account_deleted_success, Snackbar.LENGTH_LONG)
                                .show();
                        deleteAccountDialog.dismiss();
                        goToLoginScreen();
                    }

                    @Override
                    public void onUserError(Exception error) {
                        Snackbar.make(binding.getRoot(), R.string.edit_profile_general_error, Snackbar.LENGTH_LONG)
                                .show();
                        deleteAccountDialog.enableConfirmButton();
                    }
                });
            }

            @Override
            public void onUserError(Exception error) {
                if (error.getMessage() != null) {
                    Snackbar.make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
                deleteAccountDialog.enableConfirmButton();
            }
        }));
        deleteAccountDialog
                .setTitle(getString(R.string.delete_account_title))
                .setDescription(getString(R.string.delete_account_description))
                .setConfirmButtonText(getString(R.string.dialog_delete_btn))
                .setIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
    }

    private void onDeleteAccountClicked() {
        deleteAccountDialog.show();
    }

    private void onAboutClicked() {
        navigator.getNavController().navigate(R.id.action_settingsFragment_to_aboutFragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (logoutDialog != null) {
            logoutDialog.dismiss();
        }
    }

    private void goToLoginScreen() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
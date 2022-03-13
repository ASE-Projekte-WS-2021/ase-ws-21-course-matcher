package com.example.cm.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.databinding.FragmentSettingsBinding;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.utils.LogoutDialog;
import com.example.cm.utils.Navigator;

public class SettingsFragment extends Fragment implements LogoutDialog.OnLogoutListener {

    private FragmentSettingsBinding binding;
    private Navigator navigator;
    private SettingsViewModel settingsViewModel;
    private LogoutDialog logoutDialog;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        initUI();
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        binding.actionBar.tvTitle.setText(R.string.title_settings);
        // Set labels of links
        binding.linkEditProfile.linkText.setText(getString(R.string.link_label_edit_profile));
        binding.linkEditAccount.linkText.setText(getString(R.string.link_label_edit_account));
        binding.linkEditNotifications.linkText.setText(getString(R.string.link_label_edit_notifications));
        binding.linkPrivacyPolicy.linkText.setText(getString(R.string.link_label_privacy_policy));
        binding.linkImprint.linkText.setText(getString(R.string.link_label_imprint));
        binding.linkLogout.linkText.setText(getString(R.string.link_label_logout));

        // Set icons of links
        binding.linkEditProfile.linkIcon.setImageResource(R.drawable.ic_edit_profile);
        binding.linkEditAccount.linkIcon.setImageResource(R.drawable.ic_edit_account);
        binding.linkEditNotifications.linkIcon.setImageResource(R.drawable.ic_edit_notifications);
        binding.linkPrivacyPolicy.linkIcon.setImageResource(R.drawable.ic_privacy_policy);
        binding.linkImprint.linkIcon.setImageResource(R.drawable.ic_imprint);
        binding.linkLogout.linkIcon.setImageResource(R.drawable.ic_logout);

        // Set version number
        try {
            PackageInfo packageInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            binding.tvVersionNumber.setText(getString(R.string.app_version_prefix) + " " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initViewModel() {
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.linkEditProfile.linkWrapper.setOnClickListener(v -> onEditProfileClicked());
        binding.linkEditAccount.linkWrapper.setOnClickListener(v -> onEditAccountClicked());
        binding.linkEditNotifications.linkWrapper.setOnClickListener(v -> onEditNotificationsClicked());
        binding.linkPrivacyPolicy.linkWrapper.setOnClickListener(v -> onPrivacyPolicyClicked());
        binding.linkImprint.linkWrapper.setOnClickListener(v -> onImprintClicked());
        binding.linkLogout.linkWrapper.setOnClickListener(v -> onLogoutClicked());
    }

    private void onEditProfileClicked() {
        navigator.getNavController().navigate(R.id.action_settingsFragment_to_editProfileFragment);
    }

    private void onEditAccountClicked() {
        navigator.getNavController().navigate(R.id.action_settingsFragment_to_editAccountFragment);
    }

    private void onEditNotificationsClicked() {
        navigator.getNavController().navigate(R.id.action_settingsFragment_to_editNotificationsFragment);
    }

    private void onPrivacyPolicyClicked() {
        String url = getString(R.string.url_privacy_policy);
        openLink(url);
    }

    private void onImprintClicked() {
        String url = getString(R.string.url_imprint);
        openLink(url);
    }

    private void onLogoutClicked() {
        logoutDialog = new LogoutDialog(requireActivity(), this);
        logoutDialog.show();
    }

    private void openLink(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (logoutDialog != null) {
            logoutDialog.dismiss();
        }
    }

    @Override
    public void onLogoutApproved() {
        settingsViewModel.logOut();

        // Navigate to Login Screen
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
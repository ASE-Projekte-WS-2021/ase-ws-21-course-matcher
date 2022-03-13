package com.example.cm.ui.settings;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static com.example.cm.Constants.PREFS_SETTINGS_KEY;
import static com.example.cm.Constants.PREFS_SHARE_LOCATION_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.data.listener.UserListener;
import com.example.cm.databinding.FragmentSettingsBinding;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.utils.LogoutDialog;
import com.example.cm.utils.Navigator;
import com.google.android.material.snackbar.Snackbar;

import timber.log.Timber;

public class SettingsFragment extends Fragment implements LogoutDialog.OnLogoutListener {

    private ActivityResultLauncher<String> locationPermissionLauncher;
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

        initLocationPermissionLauncher();
        initUI();
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                request -> {
                    if(!request) {
                        Snackbar.make(binding.getRoot(), R.string.location_permission_denied, Snackbar.LENGTH_LONG).show();
                        binding.switchShareLocation.setChecked(false);
                    }
                    SharedPreferences.Editor editor = requireActivity().getSharedPreferences(PREFS_SETTINGS_KEY, Context.MODE_PRIVATE).edit();
                    editor.putBoolean(PREFS_SHARE_LOCATION_KEY, request);
                    editor.apply();
                }
        );
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        binding.actionBar.tvTitle.setText(R.string.title_settings);

        // Setup switch for location sharing
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_SETTINGS_KEY, Context.MODE_PRIVATE);
        boolean shareLocation = sharedPreferences.getBoolean(PREFS_SHARE_LOCATION_KEY, false);
        binding.switchShareLocation.setChecked(shareLocation);

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
        binding.switchShareLocation.setOnCheckedChangeListener((v, isChecked) -> onShareLocationClicked(isChecked));
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

    private void onShareLocationClicked(boolean isChecked) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_SETTINGS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;

        if (!hasCoarseLocationPermission || !hasFineLocationPermission) {
            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION);
            return;
        }

        settingsViewModel.updateLocationSharing(isChecked, new UserListener<Boolean>() {
            @Override
            public void onUserSuccess(Boolean isChecked) {
                editor.putBoolean(PREFS_SHARE_LOCATION_KEY, isChecked);
                editor.apply();
            }

            @Override
            public void onUserError(Exception error) {
                if (error.getMessage() != null) {
                    Snackbar.make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                error.printStackTrace();
            }
        });
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
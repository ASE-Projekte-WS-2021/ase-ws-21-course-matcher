package com.example.cm.ui.settings;

import static com.example.cm.data.models.Availability.AVAILABLE;
import static com.example.cm.data.models.Availability.SOON_AVAILABLE;
import static com.example.cm.data.models.Availability.UNAVAILABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.R;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.Availability;
import com.example.cm.databinding.FragmentSettingsBinding;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.utils.EditTextDialog;
import com.example.cm.utils.LogoutDialog;
import com.example.cm.utils.Navigator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SettingsFragment extends Fragment implements LogoutDialog.OnLogoutListener {

    private FragmentSettingsBinding binding;
    private Navigator navigator;
    private SettingsViewModel settingsViewModel;
    private LogoutDialog logoutDialog;
    private EditTextDialog editTextDialog;
    private PopupMenu popup;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        initUI();
        initViewModel();
        initListeners();
        initAvailabilityMenu();

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
        binding.availabilityStateSetter.availabilityStateText.setText(R.string.availability);

        // Set icons of links
        binding.linkEditAccount.linkIcon.setImageResource(R.drawable.ic_edit_account);
        binding.linkLogout.linkIcon.setImageResource(R.drawable.ic_logout);
        binding.linkDeleteAccount.linkIcon.setImageResource(R.drawable.ic_delete);
    }

    private void initViewModel() {
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        settingsViewModel.getUser().observe(getViewLifecycleOwner(), user -> {

            if (user.getAvailability() != null) {
                switch (user.getAvailability()) {
                    case AVAILABLE:
                        setAvailableUI();
                        break;
                    case SOON_AVAILABLE:
                        setSoonAvailableUI();
                        break;
                    case UNAVAILABLE:
                        setUnavailableUI();
                        break;
                }
            }
        });
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.linkEditAccount.linkWrapper.setOnClickListener(v -> onEditAccountClicked());
        binding.linkLogout.linkWrapper.setOnClickListener(v -> onLogoutClicked());
        binding.linkDeleteAccount.linkWrapper.setOnClickListener(v -> onDeleteAccountClicked());
        binding.availabilityStateSetter.availabilityState.setOnClickListener(v -> popup.show());
        binding.tvAbout.setOnClickListener(v -> onAboutClicked());
    }

    private void initAvailabilityMenu() {
        popup = new PopupMenu(requireContext(), binding.availabilityStateSetter.availabilityState);
        setForceShowIcon(popup);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_availability_state, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuAvailable:
                    onAvailable();
                    break;
                case R.id.menuSoonAvailable:
                    onSoonAvailable();
                    break;
                case R.id.menuUnavailable:
                    onUnavailable();
                    break;
            }
            return true;
        });
    }

    // https://stackoverflow.com/questions/20836385/popup-menu-with-icon-on-android
    private void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] mFields = popupMenu.getClass().getDeclaredFields();
            for (Field field : mFields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> popupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method mMethods = popupHelper.getMethod("setForceShowIcon", boolean.class);
                    mMethods.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void onSoonAvailable() {
        settingsViewModel.updateAvailablilty(SOON_AVAILABLE, new UserListener<Availability>() {
            @Override
            public void onUserSuccess(Availability availability) {
                setSoonAvailableUI();
            }

            @Override
            public void onUserError(Exception error) {
                Snackbar.make(binding.getRoot(), R.string.availavilityError, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onUnavailable() {
        settingsViewModel.updateAvailablilty(UNAVAILABLE, new UserListener<Availability>() {
            @Override
            public void onUserSuccess(Availability availability) {
                setUnavailableUI();
            }

            @Override
            public void onUserError(Exception error) {
                Snackbar.make(binding.getRoot(), R.string.availavilityError, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onAvailable() {
        settingsViewModel.updateAvailablilty(AVAILABLE, new UserListener<Availability>() {
            @Override
            public void onUserSuccess(Availability availability) {
                setAvailableUI();
            }

            @Override
            public void onUserError(Exception error) {
                Snackbar.make(binding.getRoot(), R.string.availavilityError, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onAboutClicked() {
        navigator.getNavController().navigate(R.id.action_settingsFragment_to_aboutFragment);
    }

    private void setAvailableUI() {
        binding.availabilityStateSetter.availabilityText.setText(R.string.available);
        binding.availabilityStateSetter.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_available);
    }

    private void setUnavailableUI() {
        binding.availabilityStateSetter.availabilityText.setText(R.string.unavailable);
        binding.availabilityStateSetter.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_unavailable);
    }

    private void setSoonAvailableUI() {
        binding.availabilityStateSetter.availabilityText.setText(R.string.soonAvailable);
        binding.availabilityStateSetter.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_soon_available);
    }

    private void onEditAccountClicked() {
        navigator.getNavController().navigate(R.id.action_settingsFragment_to_editAccountFragment);
    }

    private void onLogoutClicked() {
        logoutDialog = new LogoutDialog(requireActivity(), this);
        logoutDialog.show();
    }

    private void onDeleteAccountClicked() {
        editTextDialog = new EditTextDialog(requireActivity(), (fieldToUpdate, updatedValue) -> {
            settingsViewModel.reauthenticate(requireContext(), updatedValue, new UserListener<Boolean>() {
                @Override
                public void onUserSuccess(Boolean aBoolean) {
                    settingsViewModel.deleteAccount(new UserListener<Boolean>() {
                        @Override
                        public void onUserSuccess(Boolean aBoolean) {
                            Snackbar.make(binding.getRoot(), R.string.account_deleted_success, Snackbar.LENGTH_LONG)
                                    .show();
                            editTextDialog.dismiss();
                            goToLoginScreen();
                        }

                        @Override
                        public void onUserError(Exception error) {
                            Snackbar.make(binding.getRoot(), R.string.edit_profile_general_error, Snackbar.LENGTH_LONG)
                                    .show();
                            editTextDialog.enableConfirmButton();
                        }
                    });
                }

                @Override
                public void onUserError(Exception error) {
                    if (error.getMessage() != null) {
                        Snackbar.make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    editTextDialog.enableConfirmButton();
                }
            });
        });
        editTextDialog
                .setTitle(getString(R.string.delete_account_title))
                .setDescription(getString(R.string.delete_account_description))
                .setConfirmButtonText(getString(R.string.dialog_delete_btn))
                .setIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE)
                .show();
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
        goToLoginScreen();
    }

    private void goToLoginScreen() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
package com.example.cm.ui.own_profile;

import android.graphics.Bitmap;
import static com.example.cm.data.models.Availability.AVAILABLE;
import static com.example.cm.data.models.Availability.SOON_AVAILABLE;
import static com.example.cm.data.models.Availability.UNAVAILABLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.Availability;
import com.example.cm.data.models.User;
import com.example.cm.databinding.FragmentOwnProfileBinding;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class OwnProfileFragment extends Fragment {

    private OwnProfileViewModel ownProfileViewModel;
    private FragmentOwnProfileBinding binding;
    private Navigator navigator;
    private Availability availability;
    private PopupMenu popup;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnProfileBinding.inflate(inflater, container, false);
        initAvailabilityMenu();
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.btnProfileSettings.setOnClickListener(v -> navigator.getNavController().navigate(R.id.action_navigation_profile_to_settingsFragment));
        binding.btnEditProfile.setOnClickListener(v -> navigator.getNavController().navigate(R.id.action_ownProfileFragment_to_editProfileFragment));

        binding.dotAvailabilityIcon.setOnClickListener(v -> popup.show());
    }

    private void initViewModel() {
        ownProfileViewModel = new ViewModelProvider(this).get(OwnProfileViewModel.class);
        ownProfileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            initUi(currentUser);
        });
    }

    private void initUi(User currentUser) {
        binding.btnEditProfile.setText(R.string.profile_btn_edit);
        binding.tvName.setText(currentUser.getFullName());
        binding.tvUsername.setText(currentUser.getUsername());
        binding.tvBioDescription.setText(currentUser.getBio());
        String profileImageString = currentUser.getProfileImageString();
        if (profileImageString != null && !profileImageString.isEmpty()) {
            binding.ivProfileImage.setImageTintMode(null);
            Bitmap img = Utils.convertBaseStringToBitmap(profileImageString);
            binding.ivProfileImage.setImageBitmap(img);
        }

        availability = currentUser.getAvailability();
        if (availability != null) {
            switch (availability) {
                case AVAILABLE:
                    binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_available);
                    break;
                case SOON_AVAILABLE:
                    binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_soon_available);
                    break;
                case UNAVAILABLE:
                    binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_unavailable);
                    break;
            }
        }
        binding.btnProfileSettings.bringToFront();
    }


    @SuppressLint("NonConstantResourceId")
    private void initAvailabilityMenu() {
        popup = new PopupMenu(requireContext(), binding.dotAvailabilityIcon);
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
        ownProfileViewModel.updateAvailability(SOON_AVAILABLE, new UserListener<Availability>() {
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
        ownProfileViewModel.updateAvailability(UNAVAILABLE, new UserListener<Availability>() {
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
        ownProfileViewModel.updateAvailability(AVAILABLE, new UserListener<Availability>() {
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

    private void setAvailableUI() {
        binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_available_button);
    }

    private void setUnavailableUI() {
        binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_unavailable_button);
    }

    private void setSoonAvailableUI() {
        binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_soon_button);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
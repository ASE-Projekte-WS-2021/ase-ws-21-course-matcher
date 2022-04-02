package com.example.cm.ui.other_profile;

import static com.example.cm.data.models.Availability.AVAILABLE;
import static com.example.cm.data.models.Availability.SOON_AVAILABLE;
import static com.example.cm.data.models.Availability.UNAVAILABLE;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.Availability;
import com.example.cm.databinding.FragmentOtherProfileBinding;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class OtherProfileFragment extends Fragment {

    private OtherProfileViewModel otherProfileViewModel;
    private FragmentOtherProfileBinding binding;
    private Bundle bundle;
    private String profileId;
    private Navigator navigator;
    private Availability availability;
    private PopupMenu popup;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtherProfileBinding.inflate(inflater, container, false);
        bundle = this.getArguments();
        initViewModel();
        initListener();
        return binding.getRoot();
    }

    private void initListener() {
        navigator = new Navigator(requireActivity());
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.btnAddRemoveFriend.setOnClickListener(view -> onBtnClick());
        binding.dotAvailabilityIconMenu.setOnClickListener(v -> popup.show());
    }

    private void onBtnClick() {
        if (bundle.containsKey(Constants.KEY_USER_ID)) {
            profileId = bundle.getString(Constants.KEY_USER_ID);
            otherProfileViewModel.getUserById(profileId);
            observeFriendship(profileId);
        }

        if (binding.btnAddRemoveFriend.getText() == getResources().getString(R.string.profile_btn_add_friend)) {
            otherProfileViewModel.sendFriendRequestTo(profileId);
            binding.btnAddRemoveFriend.setText(getResources().getString(R.string.btn_send_friend_request_pending));
        } else if (binding.btnAddRemoveFriend.getText() == getResources().getString(R.string.profile_btn_remove_friend)) {
            otherProfileViewModel.unfriend(profileId);
            binding.btnAddRemoveFriend.setText(getResources().getString(R.string.profile_btn_add_friend));
        } else if (binding.btnAddRemoveFriend.getText() == getResources().getString(R.string.profile_btn_edit)) {
            navigator.getNavController().navigate(R.id.action_global_to_edit_profile);
        }
    }

    private void initViewModel() {
        otherProfileViewModel = new ViewModelProvider(this).get(OtherProfileViewModel.class);
        getProfileInformation();
        otherProfileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }

            String profileImageString = currentUser.getProfileImageString();
            availability = currentUser.getAvailability();

            binding.tvName.setText(currentUser.getDisplayName());
            binding.tvUsername.setText(currentUser.getUsername());
            binding.tvBioDescription.setText(currentUser.getBio());

            if (profileImageString != null && !profileImageString.isEmpty()) {
                Bitmap img = Utils.convertBaseStringToBitmap(profileImageString);

                binding.ivProfileImage.setImageTintMode(null);
                binding.ivProfileImage.setImageBitmap(img);
            }

            switch (availability) {
                case AVAILABLE:
                    binding.dotAvailabilityIconMenu.setImageResource(R.drawable.ic_dot_available);
                    binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_available);
                    break;
                case SOON_AVAILABLE:
                    binding.dotAvailabilityIconMenu.setImageResource(R.drawable.ic_dot_soon_available);
                    binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_soon_available);
                    break;
                case UNAVAILABLE:
                    binding.dotAvailabilityIconMenu.setImageResource(R.drawable.ic_dot_unavailable);
                    binding.dotAvailabilityIcon.setImageResource(R.drawable.ic_dot_unavailable);
                    break;
            }

        });
    }

    private void getProfileInformation() {
        if (bundle == null) {
            return;
        }

        if (bundle.containsKey(Constants.KEY_USER_ID)) {
            profileId = bundle.getString(Constants.KEY_USER_ID);
            otherProfileViewModel.getUserById(profileId);
        }

        if (bundle.containsKey(Constants.KEY_IS_OWN_USER) && bundle.getBoolean(Constants.KEY_IS_OWN_USER)) {
            isOwnProfile();
        } else {
            observeFriendship(profileId);
        }
    }

    private void observeFriendship(String profileId) {
        otherProfileViewModel.isBefriended(profileId).observe(getViewLifecycleOwner(), isBefriended -> {
            if (isBefriended == null) {
                return;
            }

            if (isBefriended) {
                onIsAlreadyBefriended();
            } else {
                otherProfileViewModel.isFriendRequestPending(profileId).observe(getViewLifecycleOwner(), isPending -> {
                    if (isPending) {
                        onFriendRequestPending();
                    } else {
                        onIsNotBefriended();
                    }
                });
            }
        });
    }

    private void isOwnProfile() {
        ColorStateList btnBackground = ContextCompat.getColorStateList(requireActivity(), R.color.outgreyed);
        int btnTextColor = ContextCompat.getColor(requireActivity(), R.color.white);

        binding.btnAddRemoveFriend.setBackgroundTintList(btnBackground);
        binding.btnAddRemoveFriend.setTextColor(btnTextColor);
        binding.btnAddRemoveFriend.setText(R.string.profile_btn_edit);
        binding.dotAvailabilityIconMenu.setVisibility(View.VISIBLE);

        initAvailabilityMenu();

    }

    private void onFriendRequestPending() {
        ColorStateList btnBackground = ContextCompat.getColorStateList(requireActivity(), R.color.outgreyed);
        int btnTextColor = ContextCompat.getColor(requireActivity(), R.color.white);

        binding.btnAddRemoveFriend.setBackgroundTintList(btnBackground);
        binding.btnAddRemoveFriend.setTextColor(btnTextColor);
        binding.btnAddRemoveFriend.setText(R.string.btn_send_friend_request_pending);

        hideAvailabilityDot();
    }

    private void onIsNotBefriended() {
        ColorStateList btnBackground = ContextCompat.getColorStateList(requireActivity(), R.color.orange500);
        int btnTextColor = ContextCompat.getColor(requireActivity(), R.color.white);

        binding.btnAddRemoveFriend.setBackgroundTintList(btnBackground);
        binding.btnAddRemoveFriend.setTextColor(btnTextColor);
        binding.btnAddRemoveFriend.setText(R.string.profile_btn_add_friend);
        hideAvailabilityDot();
    }

    private void onIsAlreadyBefriended() {
        ColorStateList btnBackground = ContextCompat.getColorStateList(requireActivity(), R.color.gray400);
        int btnTextColor = ContextCompat.getColor(requireActivity(), R.color.gray700);

        binding.btnAddRemoveFriend.setBackgroundTintList(btnBackground);
        binding.btnAddRemoveFriend.setTextColor(btnTextColor);
        binding.btnAddRemoveFriend.setText(R.string.profile_btn_remove_friend);

        showAvailabilityDot();
    }


    private void hideAvailabilityDot() {
        binding.dotAvailabilityIcon.setVisibility(View.GONE);
    }

    private void showAvailabilityDot() {
        if (availability == null) {
            return;
        }
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

    @SuppressLint("NonConstantResourceId")
    private void initAvailabilityMenu() {
        popup = new PopupMenu(requireContext(), binding.dotAvailabilityIcon);
        MenuInflater inflater = popup.getMenuInflater();

        binding.dotAvailabilityIcon.setVisibility(View.GONE);
        inflater.inflate(R.menu.menu_availability_state, popup.getMenu());
        setForceShowIcon(popup);

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
                    Class<?> popupHelper = null;
                    if (menuPopupHelper != null) {
                        popupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    }
                    Method mMethods = null;
                    if (popupHelper != null) {
                        mMethods = popupHelper.getMethod("setForceShowIcon", boolean.class);
                    }
                    if (mMethods != null) {
                        mMethods.invoke(menuPopupHelper, true);
                    }
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void onSoonAvailable() {
        otherProfileViewModel.updateAvailability(SOON_AVAILABLE, new UserListener<Availability>() {
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
        otherProfileViewModel.updateAvailability(UNAVAILABLE, new UserListener<Availability>() {
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
        otherProfileViewModel.updateAvailability(AVAILABLE, new UserListener<Availability>() {
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
        binding.dotAvailabilityIconMenu.setImageResource(R.drawable.ic_available_button);
    }

    private void setUnavailableUI() {
        binding.dotAvailabilityIconMenu.setImageResource(R.drawable.ic_unavailable_button);
    }

    private void setSoonAvailableUI() {
        binding.dotAvailabilityIconMenu.setImageResource(R.drawable.ic_soon_button);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
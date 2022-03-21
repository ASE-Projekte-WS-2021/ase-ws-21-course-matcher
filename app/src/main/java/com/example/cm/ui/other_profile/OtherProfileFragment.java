package com.example.cm.ui.other_profile;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.databinding.FragmentOtherProfileBinding;
import com.example.cm.utils.Navigator;
import com.squareup.picasso.Picasso;

public class OtherProfileFragment extends Fragment {

    private OtherProfileViewModel otherProfileViewModel;
    private FragmentOtherProfileBinding binding;
    private Bundle bundle;
    private String profileId;
    private Navigator navigator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtherProfileBinding.inflate(inflater, container, false);
        bundle = this.getArguments();
        initViewModel();
        initListener();
        return binding.getRoot();
    }

    private void initListener() {
        navigator = new Navigator(getActivity());
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.btnAddRemoveFriend.setOnClickListener(view -> onBtnClick());
    }

    private void onBtnClick() {
        if (bundle.containsKey(Constants.KEY_USER_ID)) {
            String profileId = bundle.getString(Constants.KEY_USER_ID);
            otherProfileViewModel.getUserById(profileId);
            observeFriendship(profileId);
        }

        if (binding.btnAddRemoveFriend.getText() == getResources().getString(R.string.profile_btn_add_friend)){
            otherProfileViewModel.sendFriendRequestTo(profileId);
            binding.btnAddRemoveFriend.setText(getResources().getString(R.string.btn_send_friend_request_pending));
        } else if (binding.btnAddRemoveFriend.getText() == getResources().getString(R.string.profile_btn_remove_friend)){
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
            binding.tvName.setText(currentUser.getFullName());
            binding.tvUsername.setText(currentUser.getUsername());
            binding.tvBioDescription.setText(currentUser.getBio());
            if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
                binding.ivProfileImage.setImageTintMode(null);
                binding.ivProfileImage.setScaleX(1f);
                binding.ivProfileImage.setScaleY(1f);
                Picasso.get().load(currentUser.getProfileImageUrl()).fit().centerCrop().into(binding.ivProfileImage);
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
    }

    private void onFriendRequestPending() {
        ColorStateList btnBackground = ContextCompat.getColorStateList(requireActivity(), R.color.outgreyed);
        int btnTextColor = ContextCompat.getColor(requireActivity(), R.color.white);

        binding.btnAddRemoveFriend.setBackgroundTintList(btnBackground);
        binding.btnAddRemoveFriend.setTextColor(btnTextColor);
        binding.btnAddRemoveFriend.setText(R.string.btn_send_friend_request_pending);
    }

    private void onIsNotBefriended() {
        ColorStateList btnBackground = ContextCompat.getColorStateList(requireActivity(), R.color.orange500);
        int btnTextColor = ContextCompat.getColor(requireActivity(), R.color.white);

        binding.btnAddRemoveFriend.setBackgroundTintList(btnBackground);
        binding.btnAddRemoveFriend.setTextColor(btnTextColor);
        binding.btnAddRemoveFriend.setText(R.string.profile_btn_add_friend);
    }

    private void onIsAlreadyBefriended() {
        ColorStateList btnBackground = ContextCompat.getColorStateList(requireActivity(), R.color.gray400);
        int btnTextColor = ContextCompat.getColor(requireActivity(), R.color.gray700);

        binding.btnAddRemoveFriend.setBackgroundTintList(btnBackground);
        binding.btnAddRemoveFriend.setTextColor(btnTextColor);
        binding.btnAddRemoveFriend.setText(R.string.profile_btn_remove_friend);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
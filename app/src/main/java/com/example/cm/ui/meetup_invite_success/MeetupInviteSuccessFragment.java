package com.example.cm.ui.meetup_invite_success;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupInviteSuccessBinding;
import com.example.cm.utils.Navigator;

import java.util.Objects;


public class MeetupInviteSuccessFragment extends Fragment {
    private FragmentMeetupInviteSuccessBinding binding;
    private Navigator navigator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupInviteSuccessBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        initListener();

        return binding.getRoot();
    }

    private void initListener() {
        binding.btnToMeetupList.setOnClickListener(view -> {
            navigator.getNavController().navigate(R.id.action_global_navigate_to_meetups);
        });

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        // Prevent show/hide animation of action bar
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setShowHideAnimationEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }
}